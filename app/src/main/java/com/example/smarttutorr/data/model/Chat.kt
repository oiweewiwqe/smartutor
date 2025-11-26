// app/src/main/java/com/example/smarttutorr/data/model/Chat.kt
package com.example.smarttutorr.data.model

import com.google.firebase.Timestamp

data class Chat(
    val id: String = "",
    val title: String = "Новый чат",
    val createdAt: Timestamp = Timestamp.now()
)