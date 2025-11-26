// app/src/main/java/com/example/smarttutorr/data/repository/AdminRepository.kt
package com.example.smarttutorr.data.repository
import com.example.smarttutorr.data.model.AdminStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAdminStats(): AdminStats {
        val users = db.collection("users").get().await()
        val allChats = db.collectionGroup("chats").get().await()

        var totalTasks = 0
        // Проходим по всем чатам и считаем сообщения с "Задание:" и "Ответ:"
        for (chatDoc in allChats) {
            val messages = chatDoc.reference.collection("messages").get().await()
            totalTasks += messages.count { msg ->
                val content = msg.getString("content") ?: ""
                content.contains("Задание:", ignoreCase = true) &&
                        content.contains("Ответ:", ignoreCase = true)
            }
        }

        return AdminStats(
            totalUsers = users.size(),
            totalChats = allChats.size(),
            completedTasks = totalTasks
        )
    }
}