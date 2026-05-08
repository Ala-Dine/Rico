package com.univeloued.rico.ui.screens.family.add

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberScreen(
    onBack: () -> Unit,
    viewModel: AddFamilyMemberViewModel = hiltViewModel()
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
        viewModel.onAction(AddFamilyMemberUiAction.UpdatePhotoUri(uri?.toString()))
    }

    // Relationship Dropdown State
    var showRelationshipDropdown by remember { mutableStateOf(false) }
    val relationshipOptions = listOf("Child", "Father", "Mother", "Wife", "Brother", "Sister")

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val isFormValid = uiState.name.isNotBlank() &&
            uiState.relationship.isNotBlank() &&
            uiState.birthdate.isNotBlank()

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
                text = "Add family member",
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
                label = "Full name",
                value = uiState.name,
                onValueChange = { viewModel.onAction(AddFamilyMemberUiAction.UpdateName(it)) },
                placeholder = "Enter name"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Relationship Dropdown
            Box {
                RicoTextField(
                    label = "Relationship",
                    value = uiState.relationship,
                    onValueChange = {},
                    placeholder = "Select relationship",
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    readOnly = true,
                    onClick = { showRelationshipDropdown = true }
                )
                DropdownMenu(
                    expanded = showRelationshipDropdown,
                    onDismissRequest = { showRelationshipDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
                ) {
                    relationshipOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.onAction(AddFamilyMemberUiAction.UpdateRelationship(option))
                                showRelationshipDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Birthdate Picker
            RicoTextField(
                label = "Birthdate",
                value = uiState.birthdate,
                onValueChange = {},
                placeholder = "Select date",
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
                    onClick = { viewModel.onAction(AddFamilyMemberUiAction.UpdateGender("Male")) },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    label = "Female",
                    isSelected = uiState.gender == "Female",
                    onClick = { viewModel.onAction(AddFamilyMemberUiAction.UpdateGender("Female")) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.onAction(AddFamilyMemberUiAction.SaveMember) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                        text = "Save member",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.onAction(AddFamilyMemberUiAction.UpdateBirthdate(formatter.format(date)))
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
}
