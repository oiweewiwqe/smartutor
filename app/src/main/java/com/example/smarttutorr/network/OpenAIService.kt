package com.example.smarttutorr.network

import com.example.smarttutorr.network.model.ChatRequest
import com.example.smarttutorr.network.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun createChatCompletion(@Body request: ChatRequest): Response<ChatResponse>
}