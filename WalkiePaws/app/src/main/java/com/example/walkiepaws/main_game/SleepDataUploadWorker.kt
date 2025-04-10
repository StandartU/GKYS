package com.example.walkiepaws.main_game

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.UserSetStateDTO

import java.util.concurrent.TimeUnit

class SleepDataUploadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private lateinit var apiService: ApiService

    override fun doWork(): Result {
        val success = sendSleepDataToServer()

        if (!success) {
            return Result.retry()
        }
        Log.d("SleepTracking", "start new transaction")
        val nextWork = OneTimeWorkRequest.Builder(SleepDataUploadWorker::class.java)
            .setInitialDelay(8, TimeUnit.MINUTES)
            .addTag("sleep_tracking")
            .build()

        WorkManager.getInstance(applicationContext).enqueue(nextWork)

        return Result.success()
    }

    private fun sendSleepDataToServer(): Boolean {
        return try {
            apiService = RetrofitClient.getApiService()
            val response = apiService.setState((applicationContext as App).token, UserSetStateDTO("sleep", 15)).execute()
            Log.d("SleepTracking", "complete transaction ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SleepTracking", "Error sending sleep data", e)
            false
        }
    }
}