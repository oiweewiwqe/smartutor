package com.example.smarttutorr.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.smarttutorr.utils.NotificationHelper

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Напоминание"
        val message = inputData.getString("message") ?: "Пора выполнить задание!"

        NotificationHelper.showNotification(applicationContext, title, message)
        return Result.success()
    }
}