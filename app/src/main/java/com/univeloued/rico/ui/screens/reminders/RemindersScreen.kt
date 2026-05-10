package com.univeloued.rico.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.univeloued.rico.domain.model.Reminder

@Composable
fun RemindersScreen(
    onAddClick: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminders = uiState.reminders

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FCFB))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF102828)
                )
                Text(
                    text = "${reminders.size} active reminders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF00897B)
                )
            } else if (reminders.isEmpty()) {
                EmptyRemindersState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onToggle = { viewModel.onAction(RemindersUiAction.ToggleReminder(reminder)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isActive) Color.White else Color(0xFFF1F1F1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (reminder.isActive) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (reminder.isActive) Color(0xFFE0F2F1) else Color(0xFFE0E0E0)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = if (reminder.isActive) Color(0xFF00897B) else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.medicineName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (reminder.isActive) Color(0xFF102828) else Color.Gray
                )
                Text(
                    text = "${reminder.unit} • ${reminder.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Switch(
                checked = reminder.isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF00897B),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun EmptyRemindersState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().offset(y = (-40).dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF78909C)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No reminders yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF102828)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap Add to set medication reminders",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray)
    }
}
