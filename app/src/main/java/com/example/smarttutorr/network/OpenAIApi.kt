package com.example.smarttutorr.network

import com.example.smarttutorr.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenAIClient {
    val instance: OpenAIService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(OpenAIService::class.java)
    }
}