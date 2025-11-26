// app/src/main/java/com/example/smarttutorr/ui/main/profile/ProfileScreen.kt
package com.example.smarttutorr.ui.main.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.model.ProgressStats
import com.example.smarttutorr.data.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var userInfo by remember { mutableStateOf<UserInfo?>(null) }
    var stats by remember { mutableStateOf<ProgressStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val chatRepository = ChatRepository()
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val userDoc = firestore.collection("users").document(user.uid).get().await()
                    val name = userDoc.getString("name") ?: user.email ?: "Студент"

                    val chats = chatRepository.getChatList()
                    var totalMessages = 0
                    for (chat in chats) {
                        totalMessages += chatRepository.getMessages(chat.id).size
                    }
                    stats = ProgressStats(chats.size, totalMessages)
                    userInfo = UserInfo(name)
                }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("👤 Профиль", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Имя: ${userInfo?.name}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("📊 Прогресс обучения", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { ProfileStatCard(title = "Всего чатов", value = stats?.totalChats.toString()) }
            item { ProfileStatCard(title = "Сообщений", value = stats?.totalMessages.toString()) }
            item { ProfileStatCard(title = "Активность", value = "Сегодня") }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { FirebaseAuth.getInstance().signOut() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выйти")
                }
            }
        }
    }
}

@Composable
fun ProfileStatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
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

data class UserInfo(val name: String)