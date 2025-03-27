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

class MainActivity : AppCompatActivity() {
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegistration: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEdgeToEdge()
        initViews()
        setupClickListeners()
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
        val credentialsValid = validateCredentials(
            editTextLogin.text.toString().trim(),
            editTextPassword.text.toString().trim()
        )

        if (credentialsValid) {
            showToast("Вы успешно вошли!")
            navigateToMainGameScreen()
        }
    }

    private fun validateCredentials(login: String, password: String): Boolean {
        return when {
            login.isBlank() || password.isBlank() -> {
                showToast("Не все поля заполнены!")
                false
            }
            else -> true
        }
    }

    private fun navigateToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun navigateToMainGameScreen() {
        startActivity(Intent(this, MainGameScreen::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}