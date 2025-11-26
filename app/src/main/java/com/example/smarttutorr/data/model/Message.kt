package com.example.smarttutorr.data.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val role: String = "user",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)