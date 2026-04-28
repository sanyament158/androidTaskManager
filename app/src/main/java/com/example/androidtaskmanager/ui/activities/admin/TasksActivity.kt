package com.example.androidtaskmanager.ui.activities.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityTasksBinding
import com.example.androidtaskmanager.fragments.HeaderFragment
import com.example.androidtaskmanager.models.Status
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import com.example.androidtaskmanager.ui.adapters.TaskAdapter
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {
    companion object {
        lateinit var enteredUser: User
    }

    private val statusSwitcher = StatusSwitcher()
    private lateinit var binding: ActivityTasksBinding
    private val db = DatabaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val headerTitle = HeaderFragment.newInstance("Все задачи")
        supportFragmentManager.commit {
            add(R.id.headerTitle, headerTitle)
        }
        with(binding) {

            buttonStatusSwitcher.text = statusSwitcher.getStatusName()
            buttonStatusSwitcher.setOnClickListener {
                lifecycleScope.launch {
                    statusSwitcher.switchStatus()
                    buttonStatusSwitcher.text = statusSwitcher.getStatusName()
                    binding.rvTasks.adapter = TaskAdapter(
                        updateTasks(),
                        { task ->
                            TaskDetailsActivity.task = task
                            TaskDetailsActivity.enteredUser = enteredUser
                            startActivity(
                                Intent(
                                    this@TasksActivity,
                                    TaskDetailsActivity::class.java
                                )
                            )
                        }
                    )
                }
            }
            buttonAddTask.setOnClickListener {
                val intent = Intent(this@TasksActivity, AddTaskActivity::class.java)
                AddTaskActivity.enteredUser = enteredUser
                startActivity(intent)
            }
            buttonExit.setOnClickListener {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val tasks = updateTasks()
            binding.rvTasks.layoutManager = LinearLayoutManager(this@TasksActivity)
            binding.rvTasks.adapter = TaskAdapter(
                tasks,
                { task ->
                    TaskDetailsActivity.task = task
                    TaskDetailsActivity.enteredUser = enteredUser
                    startActivity(
                        Intent(
                            this@TasksActivity,
                            TaskDetailsActivity::class.java
                        )
                    )
                }
            )
        }
    }

    private suspend fun updateTasks(): MutableList<Task> {
        val tasks = db.getTasks().filter { it.Status.Id == statusSwitcher.getStatusId() }
        val sorted = mutableListOf<Task>()
        for (t in tasks) {
            for (s in enteredUser.Scopes) {
                if (s.Id == t.Scope.Id) {
                    sorted.add(t)
                }
            }
        }
        return sorted.toMutableList()
    }
}


class StatusSwitcher(){
    private var _statusId = 1
    private var _statusName = "В процессе"
    fun switchStatus(){
        if (_statusId == 4){
            _statusId = 1
            return
        }
        _statusId++
    }
    fun getStatusName(): String {
        return when (_statusId) {
            1 -> "В процессе"
            2 -> "Готово"
            3 -> "На проверке"
            4 -> "Ожидает"
            else -> "Неизвестно"
        }
    }
    fun getStatusId(): Int = _statusId

    }