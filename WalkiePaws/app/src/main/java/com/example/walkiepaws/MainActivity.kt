package com.example.walkiepaws

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.AuthenticationDTO
import com.example.walkiepaws.backend.model.dto.responce.LoginDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegistration: Button
    private lateinit var apiService: ApiService
    private val TAG: String = "MainActivity"
    private val ACTIVITY_RECOGNITION_PERMISSION_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = RetrofitClient.getApiService()
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        initViews()
        setupClickListeners()
    }


    private fun initViews() {
        editTextLogin = findViewById(R.id.editTextWriteLogin)
        editTextPassword = findViewById(R.id.editTextWritePassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonRegistration = findViewById(R.id.buttonRegistration)
    }

    private fun setupClickListeners() {
        buttonLogin.setOnClickListener { handleLogin() }
        buttonRegistration.setOnClickListener { navigateToRegistration() }
    }

    private fun handleLogin() {
        val authDTO = AuthenticationDTO(
            editTextLogin.text.toString().trim(),
            editTextPassword.text.toString().trim()
        )

        apiService.auth(authDTO).enqueue(object : Callback<LoginDTO> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call<LoginDTO>, response: Response<LoginDTO>) {
                if (response.code() == 200) {
                    val loginResponse = response.body()
                    (applicationContext as App).token = "Bearer " + loginResponse?.token
                    showToast("Вы успешно вошли!")
                    navigateToMainGameScreen()
                } else {
                    showToast("Неверный логин или пароль")
                }
            }

            override fun onFailure(call: Call<LoginDTO>, t: Throwable) {
                t.message?.let { Log.d("DEBUG", it) }
                showToast("Ошибка связи с сервером")
            }
        })
    }

    private fun navigateToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun navigateToMainGameScreen() {
        tryStartStepService()
        DataManager.updateData()
        startActivity(Intent(this, MainGameScreen::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun tryStartStepService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "ACTIVITY_RECOGNITION permission already granted.")
            startStepServiceActual()  // Запускаем сервис, если разрешение уже дано
        } else {
            // Запрашиваем разрешение
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                Log.w(TAG, "Showing rationale for ACTIVITY_RECOGNITION permission.")
                Toast.makeText(
                    this,
                    "Разрешение на физическую активность нужно для подсчета шагов",
                    Toast.LENGTH_LONG
                ).show()
                requestActivityRecognitionPermission()
            } else {
                Log.d(TAG, "Requesting ACTIVITY_RECOGNITION permission...")
                requestActivityRecognitionPermission()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Запрос разрешения
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
                intArrayOf(PackageManager.PERMISSION_GRANTED)  // Симулируем, что разрешение предоставлено
            )
            startStepServiceActual()
        }
    }

    private fun startStepServiceActual() {

        Log.d(TAG, "Starting StepService...")
        val serviceIntent = Intent(
            this,
            StepService::class.java
        )
        startForegroundService(serviceIntent)
    }

    private fun stopStepService() {
        Log.d(TAG, "Stopping StepService...")
        val serviceIntent = Intent(
            this,
            StepService::class.java
        )
        stopService(serviceIntent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Проверка, что мы получаем результат именно для разрешения ACTIVITY_RECOGNITION
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "ACTIVITY_RECOGNITION permission granted by user.")
                startStepServiceActual()  // Запускаем сервис, если разрешение предоставлено
            } else {
                Log.w(TAG, "ACTIVITY_RECOGNITION permission denied by user.")
                Toast.makeText(
                    this,
                    "Без разрешения подсчет шагов невозможен",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}