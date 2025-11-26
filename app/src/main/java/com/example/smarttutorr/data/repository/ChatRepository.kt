// app/src/main/java/com/example/smarttutorr/data/repository/ChatRepository.kt
package com.example.smarttutorr.data.repository

import com.example.smarttutorr.data.model.Chat
import com.example.smarttutorr.data.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    suspend fun createChat(title: String = "Новый чат"): String {
        val chatRef = db.collection("users").document(userId).collection("chats").document()
        val chatId = chatRef.id
        val chat = Chat(id = chatId, title = title)
        chatRef.set(chat).await()
        return chatId
    }

    suspend fun addMessage(chatId: String, message: Message) {
        db.collection("users").document(userId).collection("chats").document(chatId)
            .collection("messages").document()
            .set(message).await()
    }

    suspend fun getMessages(chatId: String): List<Message> {
        val snapshot = db.collection("users").document(userId).collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .await()
        return snapshot.toObjects(Message::class.java)
    }

    suspend fun getChatList(): List<Chat> {
        val snapshot = db.collection("users").document(userId).collection("chats")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(Chat::class.java).filter { it.id.isNotEmpty() }
    }

    suspend fun deleteChat(chatId: String) {
        if (chatId.isEmpty()) return
        try {
            val chatRef = db.collection("users").document(userId).collection("chats").document(chatId)
            val messages = chatRef.collection("messages").get().await()
            for (doc in messages) {
                doc.reference.delete().await()
            }
            chatRef.delete().await()
        } catch (e: Exception) {
            // Игнорируем ошибки
        }
    }
}