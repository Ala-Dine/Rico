package com.univeloued.rico.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SupabaseAuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: SupabaseAuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isPasswordReset) {
        ResetPasswordScreen(viewModel = viewModel)
    } else {
        AuthMainScreen(uiState = uiState, viewModel = viewModel, onAuthenticated = onAuthenticated)
    }
}

@Composable
fun ResetPasswordScreen(viewModel: SupabaseAuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reset Your Password", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = uiState.newPassword,
            onValueChange = viewModel::onNewPasswordChange,
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error?.contains("Password", ignoreCase = true) == true,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = viewModel::onUpdatePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Password")
            }
        }
    }
}

@Composable
fun AuthMainScreen(
    uiState: SupabaseAuthUiState,
    viewModel: SupabaseAuthViewModel,
    onAuthenticated: () -> Unit
) {
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Rico Cloud Login", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error?.contains("Email", ignoreCase = true) == true,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Login Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error?.contains("Login password", ignoreCase = true) == true,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = uiState.masterPassword,
            onValueChange = viewModel::onMasterPasswordChange,
            label = { Text("Master Password (For Data Encryption)") },
            placeholder = { Text("Don't forget this! Needed for recovery") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error?.contains("Master", ignoreCase = true) == true,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.successMessage != null) {
            Text(uiState.successMessage!!, color = Color(0xFF00897B))
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = viewModel::onSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In")
            }
            
            TextButton(onClick = viewModel::onSignUp) {
                Text("Don't have an account? Sign Up")
            }

            TextButton(onClick = viewModel::onResetPassword) {
                Text("Forgot Password?")
            }
        }
    }
}
