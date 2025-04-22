package com.example.walkiepaws.main_game

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.UserAddCashDTO
import com.example.walkiepaws.backend.model.dto.responce.CashDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoseActivity : AppCompatActivity() {
    private val ACTIVITY_RECOGNITION_PERMISSION_CODE: Int = 100
    private lateinit var stepLayout: TextView
    private val handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tryStartStepService()
        setContentView(R.layout.activity_lose)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        stepLayout = findViewById(R.id.number_of_steps)
        val button = findViewById<Button>(R.id.buttonRegistration)
        button.setOnClickListener {
            if (stepLayout.text.toString().toInt() >= 1500) {
                RetrofitClient.getApiService().addCash(
                    (applicationContext as App).token,
                    UserAddCashDTO(-1500, false)
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            RetrofitClient.getApiService().updateReturnPet((applicationContext as App).token).enqueue(
                                object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                                }
                            )
                            startActivity(Intent(applicationContext, MainGameScreen::class.java))
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
        }

        handler.post(updateSteps)
    }

    private val updateSteps = object : Runnable {
        override fun run() {
            RetrofitClient.getApiService().getCash((applicationContext as App).token)
                .enqueue(object : Callback<CashDTO> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<CashDTO>, response: Response<CashDTO>) {
                        stepLayout.text = response.body()?.cash.toString()
                    }

                    override fun onFailure(call: Call<CashDTO>, t: Throwable) {
                        Log.e("DEBUG", "Failure: ${t.message}")
                    }
                })
            handler.postDelayed(this, 10000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStepService()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun tryStartStepService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startStepServiceActual()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                Toast.makeText(
                    this,
                    "Разрешение на физическую активность нужно для подсчета шагов",
                    Toast.LENGTH_LONG
                ).show()
                requestActivityRecognitionPermission()
            } else {
                requestActivityRecognitionPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_PERMISSION_CODE
            )
            startStepServiceActual()
        } else {
            onRequestPermissionsResult(
                ACTIVITY_RECOGNITION_PERMISSION_CODE,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
            startStepServiceActual()
        }
    }

    private fun startStepServiceActual() {
        val serviceIntent = Intent(this, StepService::class.java)
        startForegroundService(serviceIntent)
    }

    private fun stopStepService() {
        val serviceIntent = Intent(this, StepService::class.java)
        stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepServiceActual()
            } else {
                Toast.makeText(
                    this,
                    "Без разрешения подсчет шагов невозможен",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}