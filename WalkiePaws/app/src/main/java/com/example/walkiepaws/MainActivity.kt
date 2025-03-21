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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val writeLogin: EditText = findViewById(R.id.writeLogin)
        val writePassword: EditText = findViewById(R.id.writePassword)

        val buttonLog: Button = findViewById(R.id.buttonLogin)
        val buttonReg: Button = findViewById(R.id.buttonRegistration)

        buttonLog.setOnClickListener{
            val login = writeLogin.text.toString().trim()
            val password = writePassword.text.toString().trim()

            if(login.isBlank() || password.isBlank())
                showText("Не все поля заполнены!")
            else {
                showText("Вы успешно вошли!")
                navigateToMainGameScreen()
            }
        }

        buttonReg.setOnClickListener {
            navigateToRegistration()
        }

    }

    private fun showText(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainGameScreen() {
        val intent = Intent(this, MainGameScreen::class.java)
        startActivity(intent)
    }
}
