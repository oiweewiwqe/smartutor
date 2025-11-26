package com.example.smarttutorr.ui.main.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.repository.ChatRepository
import kotlinx.coroutines.launch
import com.example.smarttutorr.data.model.ProgressStats

@Composable
fun ProgressScreen() {
    var stats by remember { mutableStateOf<ProgressStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val chatRepository = ChatRepository()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val chats = chatRepository.getChatList()
                val totalChats = chats.size
                var totalMessages = 0
                for (chat in chats) {
                    totalMessages += chatRepository.getMessages(chat.id).size
                }
                stats = ProgressStats(totalChats, totalMessages)
            } catch (e: Exception) {
                // ignore
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("📊 Статистика обучения", style = MaterialTheme.typography.headlineMedium)

            StatCard(title = "Всего чатов", value = stats?.totalChats.toString())
            StatCard(title = "Всего сообщений", value = stats?.totalMessages.toString())
            StatCard(title = "Активность", value = "Сегодня")
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

