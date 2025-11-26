// app/src/main/java/com/example/smarttutorr/ui/main/MainScreen.kt
package com.example.smarttutorr.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smarttutorr.R
import com.example.smarttutorr.ui.main.admin.AdminScreen
import com.example.smarttutorr.ui.main.chat.ChatAIScreen
import com.example.smarttutorr.ui.main.chats.ChatListScreen
import com.example.smarttutorr.ui.main.notes.NoteScreen
import com.example.smarttutorr.ui.main.profile.ProfileScreen
import com.example.smarttutorr.ui.main.reminders.RemindersScreen
import com.example.smarttutorr.ui.main.notes.NoteEditorScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chat") { ChatAIScreen() }
            composable("chat/{chatId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId")
                ChatAIScreen(chatId = chatId)
            }
            composable("notes") {
                NoteScreen(
                    onNoteClick = { note ->
                        navController.navigate("note_editor?noteId=${note.id}")
                    },
                    onAddNote = {
                        navController.navigate("note_editor")
                    }
                )
            }
            composable("note_editor") { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")
                NoteEditorScreen(
                    noteId = noteId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("reminders") { RemindersScreen() }
            composable("profile") { ProfileScreen() }
            composable("admin") { AdminScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Chat,
        BottomNavItem.Notes,
        BottomNavItem.Reminders,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(stringResource(item.titleRes)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val titleRes: Int) {
    object Chat : BottomNavItem("chat", Icons.Filled.Chat, R.string.chat)
    object Notes : BottomNavItem("notes", Icons.Filled.Bookmark, R.string.notes)
    object Reminders : BottomNavItem("reminders", Icons.Filled.Alarm, R.string.reminders)
    object Profile : BottomNavItem("profile", Icons.Filled.Person, R.string.profile)
}