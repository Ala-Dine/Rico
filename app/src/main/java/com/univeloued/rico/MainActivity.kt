package com.univeloued.rico

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univeloued.rico.data.security.CryptoManager
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.KeyStoreManager
import com.univeloued.rico.ui.navigation.BottomNavItem
import com.univeloued.rico.ui.navigation.RicoNavGraph
import com.univeloued.rico.ui.navigation.Screen
import com.univeloued.rico.ui.security.AuthScreen
import com.univeloued.rico.ui.security.SecurityViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RicoTheme {
                val viewModel: SecurityViewModel = viewModel()
                val isAuthenticated by viewModel.isAuthenticated.collectAsState()

                if (isAuthenticated) {
                    RicoApp()
                } else {
                    AuthScreen(
                        keyStoreManager = keyStoreManager,
                        cryptoManager = cryptoManager,
                        databasePassphraseManager = databasePassphraseManager,
                    ) { passphrase ->
                        databasePassphraseManager.setUnlockedDatabasePassphrase(passphrase)
                        viewModel.setAuthenticated(authenticated = true)
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
