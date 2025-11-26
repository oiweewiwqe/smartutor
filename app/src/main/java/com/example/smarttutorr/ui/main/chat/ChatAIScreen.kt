// app/src/main/java/com/example/smarttutorr/ui/main/chat/ChatAIScreen.kt
package com.example.smarttutorr.ui.main.chat

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smarttutorr.data.model.Message
import com.example.smarttutorr.data.repository.ChatRepository
import com.example.smarttutorr.network.OpenAIClient
import com.example.smarttutorr.network.model.ChatRequest as NetworkChatRequest
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAIScreen(
    chatId: String? = null,
    onChatCreated: (String) -> Unit = {}
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentChatId by remember { mutableStateOf(chatId) }
    val coroutineScope = rememberCoroutineScope()
    val chatRepository = ChatRepository()

    // 🆕 Умный системный промпт
    val systemPrompt = """
        Ты — персональный репетитор для студента. Отвечай чётко, по делу, на русском языке.

        Если студент просит **задание**, **тест** или **упражнение**, сгенерируй **ровно одно** учебное задание по текущей теме диалога.
        Формат ответа **строго**:
        Задание: [текст задачи]
        Ответ: [ключ или решение]

        Не добавляй ничего лишнего — только задание и ответ.
    """.trimIndent()

    LaunchedEffect(currentChatId) {
        if (currentChatId != null) {
            try {
                val loadedMessages = chatRepository.getMessages(currentChatId!!)
                messages = loadedMessages
            } catch (e: Exception) {
                Log.e("ChatAIScreen", "Ошибка загрузки чата", e)
            }
        } else {
            val newChatId = chatRepository.createChat("Чат от ${Timestamp.now().toDate()}")
            currentChatId = newChatId
            onChatCreated(newChatId)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(messages) { message ->
            ChatMessageBubble(message = message.content, isUser = message.role == "user")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            placeholder = { Text("Напишите вопрос или 'Дай задание по теме...'") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (userInput.isNotBlank() && currentChatId != null) {
                    val userMessage = Message(role = "user", content = userInput)
                    messages = messages + userMessage
                    isLoading = true
                    val chatId = currentChatId!!

                    coroutineScope.launch {
                        try {
                            chatRepository.addMessage(chatId, userMessage)
                        } catch (e: Exception) {
                            Log.e("ChatAIScreen", "Не удалось сохранить сообщение", e)
                        }
                    }

                    coroutineScope.launch {
                        try {
                            // 🆕 История + системный промпт
                            val requestMessages = listOf(
                                com.example.smarttutorr.network.model.Message("system", systemPrompt)
                            ) + messages.map { m ->
                                com.example.smarttutorr.network.model.Message(m.role, m.content)
                            }

                            val request = NetworkChatRequest(messages = requestMessages)
                            val response = OpenAIClient.instance.createChatCompletion(request)
                            isLoading = false

                            if (response.isSuccessful && response.body() != null) {
                                val aiContent = response.body()!!.choices[0].message.content
                                val aiMessage = Message(role = "assistant", content = aiContent)
                                chatRepository.addMessage(chatId, aiMessage)
                                messages = messages + aiMessage
                            } else {
                                val errorMessage = Message(role = "assistant", content = "ИИ временно недоступен.")
                                messages = messages + errorMessage
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("ChatAIScreen", "Ошибка при вызове OpenAI", e)
                            val errorMessage = Message(role = "assistant", content = "Не удалось подключиться к ИИ.")
                            messages = messages + errorMessage
                        }
                        userInput = ""
                    }
                }
            },
            enabled = userInput.isNotBlank() && !isLoading,
            modifier = Modifier.align(Alignment.End)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text("Отправить")
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: String, isUser: Boolean) {
    Row(
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = message,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}