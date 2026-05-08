package com.univeloued.rico.ui.screens.reminders.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.univeloued.rico.ui.components.RicoTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    onBack: () -> Unit,
    viewModel: AddReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    // Dropdown states
    var showUnitDropdown by remember { mutableStateOf(false) }
    val unitOptions = listOf("1", "2", "3")

    var showFrequencyDropdown by remember { mutableStateOf(false) }
    val frequencyOptions = listOf("Once daily", "Twice daily", "Once a week", "Twice a week")

    var showIntakeMethodDropdown by remember { mutableStateOf(false) }
    val intakeMethodOptions = listOf("Before lunch", "After lunch", "Before dinner", "After dinner")

    // Time Picker state
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = false
    )

    val isFormValid = uiState.medicineName.isNotBlank() && uiState.unit.isNotBlank()

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
                text = "Add reminder",
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
            Spacer(modifier = Modifier.height(8.dp))

            RicoTextField(
                label = "Medicine name",
                value = uiState.medicineName,
                onValueChange = { viewModel.onAction(AddReminderUiAction.UpdateMedicineName(it)) },
                placeholder = "Enter medicine name"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box {
                RicoTextField(
                    label = "Unit",
                    value = uiState.unit,
                    onValueChange = {},
                    placeholder = "Select unit",
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    readOnly = true,
                    onClick = { showUnitDropdown = true }
                )
                DropdownMenu(
                    expanded = showUnitDropdown,
                    onDismissRequest = { showUnitDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
                ) {
                    unitOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.onAction(AddReminderUiAction.UpdateUnit(option))
                                showUnitDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            RicoTextField(
                label = "Duration",
                value = uiState.duration,
                onValueChange = { viewModel.onAction(AddReminderUiAction.UpdateDuration(it)) },
                placeholder = "e.g. 5 days",
                leadingIcon = Icons.Default.CalendarMonth
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF102828)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Card for better visual grouping
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Frequency
                    Box {
                        ScheduleRow(
                            label = "Frequency",
                            value = uiState.frequency,
                            icon = Icons.Default.Repeat,
                            onClick = { showFrequencyDropdown = true }
                        )
                        DropdownMenu(
                            expanded = showFrequencyDropdown,
                            onDismissRequest = { showFrequencyDropdown = false }
                        ) {
                            frequencyOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onAction(AddReminderUiAction.UpdateFrequency(option))
                                        showFrequencyDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF5F5F5))

                    // Time
                    ScheduleRow(
                        label = "Time",
                        value = uiState.time,
                        icon = Icons.Default.AccessTime,
                        onClick = { showTimePicker = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF5F5F5))

                    // Intake Method
                    Box {
                        ScheduleRow(
                            label = "Intake method",
                            value = uiState.intakeMethod,
                            icon = Icons.Default.Restaurant,
                            onClick = { showIntakeMethodDropdown = true }
                        )
                        DropdownMenu(
                            expanded = showIntakeMethodDropdown,
                            onDismissRequest = { showIntakeMethodDropdown = false }
                        ) {
                            intakeMethodOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onAction(AddReminderUiAction.UpdateIntakeMethod(option))
                                        showIntakeMethodDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Save Button
            Button(
                onClick = { viewModel.onAction(AddReminderUiAction.SaveReminder) },
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
                        text = "Save reminder",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = if (timePickerState.hour % 12 == 0) 12 else timePickerState.hour % 12
                    val minute = String.format("%02d", timePickerState.minute)
                    val amPm = if (timePickerState.hour < 12) "AM" else "PM"
                    viewModel.onAction(AddReminderUiAction.UpdateTime("$hour:$minute $amPm"))
                    showTimePicker = false
                }) {
                    Text("OK", color = Color(0xFF00897B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun ScheduleRow(
    label: String, 
    value: String, 
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF607D8B),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF102828)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF00897B),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF00897B),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}
