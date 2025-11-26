// app/src/main/java/com/example/smarttutorr/ui/main/chats/ChatListScreen.kt
package com.example.smarttutorr.ui.main.chats

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.model.Chat
import com.example.smarttutorr.data.repository.ChatRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit
) {
    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var triggerReload by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val chatRepository = ChatRepository()

    LaunchedEffect(triggerReload) {
        isLoading = true
        try {
            chats = chatRepository.getChatList()
        } finally {
            isLoading = false
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
            items(chats) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = { onChatClick(chat.id) },
                    onDelete = {
                        coroutineScope.launch {
                            chatRepository.deleteChat(chat.id)
                            triggerReload++ // ← ГАРАНТИРОВАННАЯ ПЕРЕЗАГРУЗКА
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChatListItem(chat: Chat, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { /* обязательно */ }
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Chat, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(chat.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    chat.createdAt.toDate().toString().take(19),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить")
            }
        }
    }
}