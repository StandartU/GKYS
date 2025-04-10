package com.example.walkiepaws.main_game

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.UserSetStateDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SleepDataUploadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private lateinit var apiService: ApiService

    override fun doWork(): Result {
        sendSleepDataToServer()
        return Result.success()
    }

    private fun sendSleepDataToServer() {
        apiService = RetrofitClient.getApiService()
        apiService.setState((DataManager.appContext as App).token, UserSetStateDTO("sleep", 25)).enqueue(
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            }
        )
    }
}