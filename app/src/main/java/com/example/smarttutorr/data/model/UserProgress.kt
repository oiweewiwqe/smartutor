package com.example.smarttutorr.data.model

data class UserProgress(
    val topics: List<TopicProgress> = emptyList(),
    val activityMap: Map<String, Int> = emptyMap()
)