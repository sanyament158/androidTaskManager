package com.example.androidtaskmanager.ui.activities.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.databinding.ActivityTaskDetailsBinding

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}