package com.example.walkiepaws

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrationActivity : AppCompatActivity() {
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegistration: Button
    private lateinit var buttonBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                showToast("Регистрация прошла успешно!")
                navigateToMain()
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