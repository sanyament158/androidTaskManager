package com.example.androidtaskmanager.ui.activities.admin

import android.content.Intent
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
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import com.example.androidtaskmanager.ui.adapters.TaskAdapter
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {
    companion object{
        lateinit var enteredUser: User
    }
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

        Log.e("f", "$statusSwitcher")

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
                lifecycleScope.launch { binding.rvTasks.adapter = TaskAdapter(updateTasks(),
                    {task ->
                        TaskDetailsActivity.task = task
                        TaskDetailsActivity.enteredUser = enteredUser
                        startActivity(Intent(this@TasksActivity,
                            TaskDetailsActivity::class.java))
                    }
                ) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val tasks = updateTasks()
            binding.rvTasks.layoutManager = LinearLayoutManager(this@TasksActivity)
            binding.rvTasks.adapter = TaskAdapter(tasks,
                {task ->
                    TaskDetailsActivity.task = task
                    TaskDetailsActivity.enteredUser = enteredUser
                    startActivity(Intent(this@TasksActivity,
                    TaskDetailsActivity::class.java))}
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
    private suspend fun updateTasks(): MutableList<Task> {
        val r = db.getTasks().filter { it.Status.Id == statusSwitcher }
        return r.toMutableList()
    }
}