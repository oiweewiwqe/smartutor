package com.example.smarttutorr.network.model

data class ChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val max_tokens: Int = 500
)

data class Message(
    val role: String, // "user" или "assistant"
    val content: String
)