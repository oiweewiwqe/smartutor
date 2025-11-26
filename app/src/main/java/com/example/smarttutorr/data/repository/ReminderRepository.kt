// app/src/main/java/com/example/smarttutorr/data/repository/ReminderRepository.kt
package com.example.smarttutorr.data.repository

import com.example.smarttutorr.data.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReminderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    suspend fun getReminders(): List<Reminder> {
        val snapshot = db.collection("users").document(userId).collection("reminders")
            .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(Reminder::class.java)
    }
}