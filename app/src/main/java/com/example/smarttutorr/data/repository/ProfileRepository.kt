package com.example.smarttutorr.data.repository

import com.example.smarttutorr.data.model.TopicProgress
import com.example.smarttutorr.data.model.UserProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {
    suspend fun getUserProgress(): UserProgress {
        return UserProgress(
            topics = listOf(
                TopicProgress("Алгебра", 7),
                TopicProgress("Физика", 3)
            )
            // activityMap временно убран
        )
    }
}