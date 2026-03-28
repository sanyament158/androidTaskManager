package com.example.androidtaskmanager.ui.activities.admin

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityTasksBinding
import com.example.androidtaskmanager.fragments.HeaderFragment
import com.example.androidtaskmanager.ui.adapters.TaskAdapter
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTasksBinding
    private val db = DatabaseService()
    private var _statusSwitcher = 0
    private var statusSwitcher = 0
        get() = when(_statusSwitcher % 3){
            0 -> 1
            1 -> 2
            2 -> 3
            else -> 4
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val headerTitle = HeaderFragment.newInstance("Все задачи")
        supportFragmentManager.commit {
            add(R.id.headerTitle, headerTitle)
        }
        with(binding){
            buttonStatusSwitcher.text = updateStatusSwitcherText()
            buttonStatusSwitcher.setOnClickListener {
                _statusSwitcher++
                buttonStatusSwitcher.text = updateStatusSwitcherText()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val tasks = db.getTasks()
            binding.rvTasks.layoutManager = LinearLayoutManager(this@TasksActivity)
            binding.rvTasks.adapter = TaskAdapter(tasks,
                {task -> Log.i("RV_TASKS", "task title - ${task.Title}")}
            )

        }
    }
    private fun updateStatusSwitcherText(): String =
        when (statusSwitcher){
            1 -> "В процессе"
            2 -> "Выполнено"
            3 -> "На проверке"
            else -> "unknown"
        }

}