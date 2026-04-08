package com.example.androidtaskmanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityLoginBinding
import com.example.androidtaskmanager.models.User
import com.example.androidtaskmanager.ui.activities.admin.MainActivity
import com.example.androidtaskmanager.ui.activities.admin.TaskDetailsActivity
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val db = DatabaseService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        with(binding) {
            binding.buttonSignIn.setOnClickListener {
                val username = loginField.text.toString()
                val password = passwordField.text.toString()

                lifecycleScope.launch {
                    val loginedUser = db.login(username, password)
                    if (loginedUser.Role.Id == 2) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        MainActivity.enteredUser = db.getUsers().first { it.Id == loginedUser.Id }
                        Log.e("asdf", loginedUser.Scopes.count().toString())
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Вход только для сотрудников",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}