// app/src/main/java/com/example/smarttutorr/ui/main/reminders/RemindersScreen.kt
package com.example.smarttutorr.ui.main.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // ← обязателен!
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.model.Reminder
import com.example.smarttutorr.data.repository.ReminderRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen() {
    var reminders by remember { mutableStateOf<List<Reminder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val repository = ReminderRepository()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                reminders = repository.getReminders()
            } catch (e: Exception) {
                // ignore
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Напоминания") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Позже можно добавить диалог
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (reminders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет напоминаний")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(reminders) { reminder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(reminder.text, style = MaterialTheme.typography.titleMedium)
                            Text(reminder.dateTime.toDate().toString().take(19))
                        }
                    }
                }
            }
        }
    }
}