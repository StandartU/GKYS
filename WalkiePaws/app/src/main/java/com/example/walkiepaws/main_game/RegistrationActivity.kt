package com.example.walkiepaws.main_game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.model.dto.request.RegisterDTO

import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.ApiService
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegistration: Button
    private lateinit var buttonBack: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = RetrofitClient.getApiService()
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        initViews()
        setupClickListeners()
    }



    private fun initViews() {
        editTextLogin = findViewById(R.id.editTextWriteLogin)
        editTextPassword = findViewById(R.id.editTextWritePassword)
        buttonRegistration = findViewById(R.id.buttonRegistration)
        buttonBack = findViewById(R.id.buttonBack)
    }

    private fun setupClickListeners() {
        buttonRegistration.setOnClickListener { handleRegistration() }
        buttonBack.setOnClickListener { navigateToMain() }
    }

    private fun handleRegistration() {
        val login = editTextLogin.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        when {
            login.isBlank() || password.isBlank() -> showToast("Не все поля заполнены!")
            else -> {
                val registerDTO = RegisterDTO(login, password)
                apiService.register(registerDTO).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            showToast("Регистрация прошла успешно!")
                            navigateToMain()
                        } else {
                            showToast("Ошибка регистрации: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        showToast("Ошибка связи с сервером")
                    }
                })
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}