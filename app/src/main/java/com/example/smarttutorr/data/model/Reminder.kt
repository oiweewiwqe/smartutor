// app/src/main/java/com/example/smarttutorr/data/model/Reminder.kt
package com.example.smarttutorr.data.model

import com.google.firebase.Timestamp

data class Reminder(
    val id: String = "",
    val text: String = "",
    val dateTime: Timestamp = Timestamp.now()
)