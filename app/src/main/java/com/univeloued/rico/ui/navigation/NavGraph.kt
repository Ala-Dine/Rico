package com.univeloued.rico.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.univeloued.rico.ui.screens.history.add.AddRecordScreen
import com.univeloued.rico.ui.screens.history.MedicalHistoryScreen
import com.univeloued.rico.ui.screens.reminders.add.AddReminderScreen
import com.univeloued.rico.ui.screens.reminders.RemindersScreen
import com.univeloued.rico.ui.screens.family.add.AddFamilyMemberScreen
import com.univeloued.rico.ui.screens.family.FamilyScreen
import com.univeloued.rico.ui.screens.emergency.add.AddEmergencyContactScreen
import com.univeloued.rico.ui.screens.emergency.EmergencyScreen
import com.univeloued.rico.ui.screens.profile.ProfileScreen
import com.univeloued.rico.ui.screens.profile.edit.EditProfileScreen

@Composable
fun RicoNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.History.route
    ) {
        composable(Screen.History.route) {
            MedicalHistoryScreen()
        }
        composable(Screen.Reminders.route) {
            RemindersScreen(
                onAddClick = { navController.navigate(Screen.AddReminder.route) }
            )
        }
        composable(Screen.Family.route) {
            FamilyScreen(
                onAddClick = { navController.navigate(Screen.AddFamilyMember.route) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditClick = { navController.navigate(Screen.EditProfile.route) }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Emergency.route) {
            EmergencyScreen(
                onAddClick = { navController.navigate(Screen.AddEmergencyContact.route) }
            )
        }
        composable(Screen.AddRecord.route) {
            AddRecordScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddReminder.route) {
            AddReminderScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddFamilyMember.route) {
            AddFamilyMemberScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddEmergencyContact.route) {
            AddEmergencyContactScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
