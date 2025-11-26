// app/src/main/java/com/example/smarttutorr/ui/main/admin/AdminScreen.kt
package com.example.smarttutorr.ui.main.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.model.AdminStats // ← ПРАВИЛЬНЫЙ ИМПОРТ
import com.example.smarttutorr.data.repository.AdminRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    var stats by remember { mutableStateOf<AdminStats?>(null) } // ← тип из data.model
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val adminRepository = AdminRepository()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                stats = adminRepository.getAdminStats() // ← возвращает data.model.AdminStats
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("🛡️ Админка", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            StatCard("Всего пользователей", stats?.totalUsers.toString())
            Spacer(modifier = Modifier.height(16.dp))
            StatCard("Всего чатов", stats?.totalChats.toString())
            Spacer(modifier = Modifier.height(16.dp))
            StatCard("Решено заданий", stats?.completedTasks.toString())

            Spacer(modifier = Modifier.height(32.dp))
            Text("Требования ТЗ: п. 2.1 — Администратор", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}