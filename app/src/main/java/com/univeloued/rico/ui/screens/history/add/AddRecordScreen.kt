package com.univeloued.rico.ui.screens.history.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.univeloued.rico.ui.components.RicoTextField
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    onBack: () -> Unit,
    viewModel: AddRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showMemberDropdown by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            viewModel.onAction(AddRecordUiAction.UpdateFileUri(uri))
        }
    )

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
                        viewModel.onAction(AddRecordUiAction.UpdateCreatedOn(date))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val isFormValid = uiState.fileName.isNotBlank() &&
            uiState.recordFor.isNotBlank() &&
            uiState.recordType.isNotBlank() &&
            uiState.createdOn.isNotBlank() &&
            uiState.selectedFileUri != null

    val members = listOf("Myself", "John Doe", "Jane Smith", "Child 1")

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
                text = "Add record",
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
            // Photo Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo Placeholder / Preview
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (uiState.selectedFileUri != null) {
                            val isPdf = uiState.selectedFileUri.toString().endsWith(".pdf", ignoreCase = true)
                            Icon(
                                imageVector = if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF00897B)
                            )
                        } else {
                            Text("photo", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                // Add Photo Button
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .clickable {
                            filePickerLauncher.launch(arrayOf("image/*", "application/pdf"))
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add photo",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF102828)
                        )
                    }
                }
            }

            if (uiState.selectedFileUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "File selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00897B)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            RicoTextField(
                label = "File name",
                value = uiState.fileName,
                onValueChange = { viewModel.onAction(AddRecordUiAction.UpdateFileName(it)) },
                placeholder = "Enter file name"
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Record type",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF102828)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val recordTypes = listOf("Visits", "Rx", "Labs")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recordTypes) { type ->
                    val isSelected = uiState.recordType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onAction(AddRecordUiAction.UpdateRecordType(type)) },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00897B),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Box {
                RicoTextField(
                    label = "Record for",
                    value = uiState.recordFor,
                    onValueChange = { },
                    placeholder = "Select member",
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    readOnly = true,
                    onClick = { showMemberDropdown = true }
                )
                
                DropdownMenu(
                    expanded = showMemberDropdown,
                    onDismissRequest = { showMemberDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                ) {
                    members.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                viewModel.onAction(AddRecordUiAction.UpdateRecordFor(name))
                                showMemberDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            RicoTextField(
                label = "Record create on",
                value = uiState.createdOn,
                onValueChange = { },
                placeholder = "Select date",
                trailingIcon = Icons.Default.CalendarMonth,
                readOnly = true,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Save Button
            Button(
                onClick = { viewModel.onAction(AddRecordUiAction.SaveRecord) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    disabledContainerColor = Color(0xFFB2DFDB)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = isFormValid && !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Save record",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
