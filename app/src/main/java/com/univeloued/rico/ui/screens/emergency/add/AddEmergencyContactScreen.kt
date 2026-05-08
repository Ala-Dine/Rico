package com.univeloued.rico.ui.screens.emergency.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.univeloued.rico.ui.components.ProfileImageWithAdd
import com.univeloued.rico.ui.components.RicoTextField

@Composable
fun AddEmergencyContactScreen(
    onBack: () -> Unit,
    viewModel: AddEmergencyContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAction(AddEmergencyContactUiAction.UpdatePhotoUri(uri?.toString()))
    }

    val isFormValid = uiState.name.isNotBlank() &&
            uiState.phone.isNotBlank() &&
            uiState.email.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FCFB))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF102828))
            }
            Text(
                text = "Add contact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF102828)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ProfileImageWithAdd(
                    photoUri = uiState.photoUri,
                    onClick = { photoPickerLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            RicoTextField(
                value = uiState.name,
                onValueChange = { viewModel.onAction(AddEmergencyContactUiAction.UpdateName(it)) },
                placeholder = "Name",
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            RicoTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onAction(AddEmergencyContactUiAction.UpdatePhone(it)) },
                placeholder = "Phone",
                leadingIcon = Icons.Default.Call
            )

            Spacer(modifier = Modifier.height(16.dp))

            RicoTextField(
                value = uiState.email,
                onValueChange = { viewModel.onAction(AddEmergencyContactUiAction.UpdateEmail(it)) },
                placeholder = "Email",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF102828),
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        viewModel.onAction(AddEmergencyContactUiAction.SaveContact)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B),
                        disabledContainerColor = Color(0xFFB2DFDB)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid && !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
