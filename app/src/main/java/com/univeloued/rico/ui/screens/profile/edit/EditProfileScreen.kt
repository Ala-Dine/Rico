package com.univeloued.rico.ui.screens.profile.edit

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.univeloued.rico.ui.components.GenderButton
import com.univeloued.rico.ui.components.ProfileImageWithAdd
import com.univeloued.rico.ui.components.RicoTextField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showBloodTypeMenu by remember { mutableStateOf(false) }
    
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAction(EditProfileUiAction.UpdatePhotoUri(uri?.toString()))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = formatter.format(calendar.time)
                        viewModel.onAction(EditProfileUiAction.UpdateBirthdate(formattedDate))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Color(0xFF00897B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FCFB))
    ) {
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
                text = "Edit Profile",
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

            // Profile Header with Image Picker
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProfileImageWithAdd(
                    photoUri = uiState.photoUri,
                    onClick = { photoPickerLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            RicoTextField(
                label = "Full Name",
                value = uiState.name,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdateName(it)) },
                placeholder = "Enter name"
            )

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Birthdate",
                value = uiState.birthdate,
                onValueChange = { },
                placeholder = "Select birthdate",
                trailingIcon = Icons.Default.CalendarMonth,
                readOnly = true,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF102828)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderButton(
                    label = "Male",
                    isSelected = uiState.gender == "Male",
                    onClick = { viewModel.onAction(EditProfileUiAction.UpdateGender("Male")) },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    label = "Female",
                    isSelected = uiState.gender == "Female",
                    onClick = { viewModel.onAction(EditProfileUiAction.UpdateGender("Female")) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                RicoTextField(
                    label = "Blood Type",
                    value = uiState.bloodType,
                    onValueChange = { },
                    placeholder = "Select blood type",
                    trailingIcon = Icons.Default.ArrowDropDown,
                    readOnly = true,
                    onClick = { showBloodTypeMenu = true }
                )
                DropdownMenu(
                    expanded = showBloodTypeMenu,
                    onDismissRequest = { showBloodTypeMenu = false },
                    modifier = Modifier.fillMaxWidth(0.5f).background(Color.White)
                ) {
                    bloodTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onAction(EditProfileUiAction.UpdateBloodType(type))
                                showBloodTypeMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Insurance Number",
                value = uiState.insuranceNumber,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdateInsuranceNumber(it)) },
                placeholder = "Enter insurance number"
            )

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Address",
                value = uiState.address,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdateAddress(it)) },
                placeholder = "Enter address"
            )

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Phone",
                value = uiState.phone,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdatePhone(it)) },
                placeholder = "Enter phone number"
            )

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdateEmail(it)) },
                placeholder = "Enter email address"
            )

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Notes",
                value = uiState.notes,
                onValueChange = { viewModel.onAction(EditProfileUiAction.UpdateNotes(it)) },
                placeholder = "Additional information"
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.onAction(EditProfileUiAction.SaveProfile) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    disabledContainerColor = Color(0xFFB2DFDB)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00897B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00897B))
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
