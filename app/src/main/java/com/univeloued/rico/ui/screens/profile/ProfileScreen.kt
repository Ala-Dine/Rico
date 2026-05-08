package com.univeloued.rico.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.univeloued.rico.ui.components.ProfileActionButton
import com.univeloued.rico.ui.components.ProfileImageWithAdd

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAction(ProfileUiAction.UpdatePhoto(uri?.toString()))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FCFB))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Profile Header with Image Picker
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ProfileImageWithAdd(
                photoUri = profile.photoUri,
                onClick = { photoPickerLauncher.launch("image/*") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileItem(label = "Name", value = profile.name.ifEmpty { "Not set" })
            ProfileItem(label = "Birthdate", value = profile.birthdate.ifEmpty { "Not set" })
            ProfileItem(label = "Gender", value = profile.gender.ifEmpty { "Not set" })
            ProfileItem(label = "Blood type", value = profile.bloodType.ifEmpty { "Not set" })
            ProfileItem(label = "Insurance No.\nNumber", value = profile.insuranceNumber.ifEmpty { "Not set" })

            HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

            ProfileItem(label = "Address", value = profile.address.ifEmpty { "Not set" })
            ProfileItem(label = "Phone", value = profile.phone.ifEmpty { "Not set" })
            ProfileItem(label = "E-mail", value = profile.email.ifEmpty { "Not set" })

            HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

            ProfileItem(label = "Notes", value = profile.notes.ifEmpty { "Not set" })
        }

        // Bottom Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileActionButton(
                icon = Icons.Outlined.Edit,
                label = "EDIT",
                onClick = onEditClick
            )
            ProfileActionButton(
                icon = Icons.AutoMirrored.Outlined.Assignment,
                label = "MED-DATA"
            )
            ProfileActionButton(
                icon = Icons.Outlined.Delete,
                label = "DEL."
            )
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF102828),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}
