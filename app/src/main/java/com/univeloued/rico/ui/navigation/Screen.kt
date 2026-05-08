package com.univeloued.rico.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object History : Screen("history")
    data object Reminders : Screen("reminders")
    data object Family : Screen("family")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object Emergency : Screen("emergency")
    data object AddRecord : Screen("add_record")
    data object AddReminder : Screen("add_reminder")
    data object AddFamilyMember : Screen("add_family_member")
    data object AddEmergencyContact : Screen("add_emergency_contact")
}

enum class BottomNavItem(
    val route: String,
    val label: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    HISTORY(Screen.History.route, "History", Icons.Outlined.Schedule, Icons.Filled.Schedule),
    REMINDERS(Screen.Reminders.route, "Reminders", Icons.Outlined.Notifications, Icons.Filled.Notifications),
    FAMILY(Screen.Family.route, "Family", Icons.Outlined.Group, Icons.Filled.Group),
    PROFILE(Screen.Profile.route, "Profile", Icons.Outlined.Person, Icons.Filled.Person),
    EMERGENCY(Screen.Emergency.route, "Emergency", Icons.Outlined.Call, Icons.Filled.Call)
}
