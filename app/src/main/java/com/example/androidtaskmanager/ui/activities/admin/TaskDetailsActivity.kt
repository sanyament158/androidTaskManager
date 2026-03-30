package com.example.androidtaskmanager.ui.activities.admin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.databinding.ActivityTaskDetailsBinding
import com.example.androidtaskmanager.models.Task

class TaskDetailsActivity() : AppCompatActivity() {
    companion object{
        lateinit var task: Task
    }
    private lateinit var binding: ActivityTaskDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("START TASK_DETAILS", task.Title)
    }
}