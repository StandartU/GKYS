package com.example.walkiepaws

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.model.dto.request.AuthenticationDTO
import com.example.walkiepaws.backend.model.dto.responce.LoginDTO

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.walkiepaws.backend.RetrofitClient

class MainActivity : AppCompatActivity() {
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegistration: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val apiService = RetrofitClient.getInstance().create(ApiService::class.java)
        apiService.auth(authDTO).enqueue(object : Callback<LoginDTO> {
            override fun onResponse(call: Call<LoginDTO>, response: Response<LoginDTO>) {
                Log.d("DEBUG", response.toString())
                if (response.code() == 200) {
                    Log.d("DEBUG", response.body()?.token.toString())
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
        startActivity(Intent(this, MainGameScreen::class.java))
    }

    private fun navigateToMainGameScreen() {
        startActivity(Intent(this, MainGameScreen::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}