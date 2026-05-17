package com.univeloued.rico

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univeloued.rico.data.security.CryptoManager
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.KeyStoreManager
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.ui.navigation.BottomNavItem
import com.univeloued.rico.ui.navigation.RicoNavGraph
import com.univeloued.rico.ui.navigation.Screen
import com.univeloued.rico.ui.security.AuthScreen
import com.univeloued.rico.ui.security.LocalSecurityViewModel
import com.univeloued.rico.ui.security.SecurityViewModel
import com.univeloued.rico.ui.security.SupabaseAuthScreen
import com.univeloued.rico.ui.security.SupabaseAuthViewModel
import com.univeloued.rico.ui.theme.RicoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    @Inject
    lateinit var keyStoreManager: KeyStoreManager
    @Inject
    lateinit var cryptoManager: CryptoManager
    @Inject
    lateinit var databasePassphraseManager: DatabasePassphraseManager
    @Inject
    lateinit var authRepository: com.univeloued.rico.domain.repository.AuthRepository
    @Inject
    lateinit var syncManager: SyncManager

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        authRepository.handleDeepLink(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        intent?.let { authRepository.handleDeepLink(it) }

        val securityViewModel: SecurityViewModel by viewModels()

        // Silent unlock attempt
        if (securityViewModel.isAuthenticated.value) {
            try {
                databasePassphraseManager.getDatabasePassphrase()
            } catch (e: Exception) {
                // Ignore silent failure, will be caught by UI
            }
        }

        // Smart-lock when app goes to background
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (!isChangingConfigurations) {
                        if (!securityViewModel.isIgnoringNextStop()) {
                            securityViewModel.onMoveToBackground()
                        } else {
                            android.util.Log.d("MainActivity", "ON_STOP: Ignoring lock due to ignore flag.")
                        }
                    }
                }
                Lifecycle.Event.ON_START -> {
                    if (!securityViewModel.isIgnoringNextStop()) {
                        if (securityViewModel.onReturnToForeground()) {
                            android.util.Log.d("MainActivity", "ON_START: App was in background for too long. Locking.")
                            securityViewModel.setAuthenticated(false)
                        }
                    } else {
                        android.util.Log.d("MainActivity", "ON_START: Skipping timeout check due to ignore flag.")
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    // We delay clearing the ignore flag slightly to ensure LaunchedEffects have run
                }
                else -> {}
            }
        })

        setContent {
            CompositionLocalProvider(LocalSecurityViewModel provides securityViewModel) {
                RicoTheme {
                    val supabaseViewModel: SupabaseAuthViewModel = viewModel()
                    val supabaseState by supabaseViewModel.uiState.collectAsState()
                    
                    val isLocallyAuthenticated by securityViewModel.isAuthenticated.collectAsState()
                    
                    val isDbLocked by databasePassphraseManager.isLocked.collectAsState()
                    val isAppUnlocked = isLocallyAuthenticated && !isDbLocked

                    // Trigger sync when app is fully unlocked
                    LaunchedEffect(isAppUnlocked) {
                        if (isAppUnlocked) {
                            android.util.Log.d("MainActivity", "App fully unlocked, triggering manual sync...")
                            syncManager.triggerManualSync()
                        }
                    }

                    // Reset local authentication when Supabase logout occurs
                    LaunchedEffect(supabaseState.isAuthenticated, supabaseState.isSessionLoaded) {
                        if (supabaseState.isSessionLoaded && !supabaseState.isAuthenticated && isLocallyAuthenticated) {
                            if (!securityViewModel.isIgnoringNextStop()) {
                                android.util.Log.d("MainActivity", "Supabase session lost, resetting app state...")
                                securityViewModel.setAuthenticated(false)
                                databasePassphraseManager.clear()
                            } else {
                                android.util.Log.d("MainActivity", "Supabase session unavailable during ignored flow. Skipping reset.")
                            }
                        }
                        
                        // Clear the ignore flag after the session has definitely had a chance to load
                        if (supabaseState.isSessionLoaded && securityViewModel.isIgnoringNextStop()) {
                            android.util.Log.d("MainActivity", "Session loaded, clearing ignore flag.")
                            securityViewModel.clearIgnoreNextStop()
                        }
                    }

                    // Handle Deep Link
                    LaunchedEffect(intent?.data) {
                        supabaseViewModel.checkDeepLink(intent?.data)
                    }

                    when {
                        !supabaseState.isSessionLoaded -> {
                            // Show nothing or a loading splash while checking session
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                            }
                        }
                        !supabaseState.isAuthenticated -> {
                            SupabaseAuthScreen(onAuthenticated = { })
                        }
                        !isAppUnlocked -> {
                            AuthScreen(
                                keyStoreManager = keyStoreManager,
                                cryptoManager = cryptoManager,
                                databasePassphraseManager = databasePassphraseManager,
                            ) { passphrase ->
                                databasePassphraseManager.setUnlockedDatabasePassphrase(passphrase)
                                securityViewModel.setAuthenticated(authenticated = true)
                            }
                        }
                        else -> {
                            RicoApp()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RicoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = BottomNavItem.entries

    // Show bottom bar only for main top-level destinations
    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                ) {
                    bottomBarScreens.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                                    contentDescription = item.label,
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00897B),
                                selectedTextColor = Color(0xFF00897B),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Only show FAB on the History screen
            if (currentDestination?.route == Screen.History.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddRecord.route) },
                    containerColor = Color(0xFF00897B),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Record")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            RicoNavGraph(navController = navController)
        }
    }
}
