// app/src/main/java/com/example/smarttutorr/data/model/Note.kt
package com.example.smarttutorr.data.model

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val title: String = "Без названия",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)