package com.example.authlistapp.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class FetchDataWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val response = fetchDataFromMockApi()
        if (response != null) {
            showNotification("Fetch Completed", "Data: $response")
            return Result.success()
        }
        return Result.retry()
    }

    private fun fetchDataFromMockApi(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts/1")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: Exception) {
            null
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "workManagerChannelID"
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, notification)
        }
    }
}
