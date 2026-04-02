package com.example.androidtaskmanager.ui.activities.admin

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.lifecycle.lifecycleScope
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityTaskDetailsBinding
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toKotlinLocalDate

class TaskDetailsActivity() : AppCompatActivity() {
    companion object {
        lateinit var task: Task
        lateinit var enteredUser: User
    }

    private lateinit var binding: ActivityTaskDetailsBinding
    private val db = DatabaseService()
    private lateinit var tasks: MutableList<Task>
    private var actualTaskIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init a tasks list
        lifecycleScope.launch {
            tasks = db.getTasks()
            actualTaskIndex = tasks.indexOf(
                tasks.find {it.Id == task.Id}
            )
        }

        if (task.Id == 0) {
            throw Exception("task == null")
        }

        with(binding) {
            setupTaskFields()

            // btAction setting
            when (task.Status.Id) {
                // if status == 'В процессе'
                1 -> {
                    lifecycleScope.launch {
//                        val scopes =
                    }
                    btAction.text = "Готово"
                    btAction.setOnClickListener {
                        AlertDialog.Builder(this@TaskDetailsActivity)
                            .setTitle("Подтверждение")
                            .setMessage("Задача выполнена вами?")
                            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("Да") { _, _ -> sendForVerify() }
                            .show()
                    }
                }
                // if status == 'Готово'
                2 -> {
                    btAction.text = "Задача уже выполнена"
                    btAction.isEnabled = false
                    btAction.background = R.color.grey.toDrawable()
                }
                // if status == 'На проверке'
                3 -> {
                    btAction.text = "Проверить"
                    btAction.setOnClickListener {
                        AlertDialog.Builder(this@TaskDetailsActivity)
                            .setTitle("Подтверждение")
                            .setMessage("Вы проверили результат?")
                            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("Да") { _, _ -> sendForFinished() }
                            .show()
                    }
                }
            }
            btNext.setOnClickListener {
                if (actualTaskIndex == tasks.count() - 1){
                    actualTaskIndex = 0
                } else{
                    actualTaskIndex++
                }

                task = tasks[actualTaskIndex]
                setupTaskFields()
            }
            btPrevious.setOnClickListener {
                if (actualTaskIndex == 0){
                    actualTaskIndex = tasks.count() - 1
                } else{
                    actualTaskIndex--
                }

                task = tasks[actualTaskIndex]
                setupTaskFields()
            }
        }
    }

    private fun sendForVerify() {
        lifecycleScope.launch {
            db.sendTaskForVerify(task)
        }
    }

    private fun sendForFinished() {
        lifecycleScope.launch {
            db.verifyTask(task, enteredUser.Id)
        }
    }

    private fun setupTaskFields() {
        with(binding) {
            tvTaskTitle.text = "Задача ${task.Title}"
            tvOwner.text = task.Owner.Lname
            tvStatus.text = task.Status.Name
            tvScope.text = task.Scope.Name
            tvDeadline.text =
                java.time.LocalDate.now().toKotlinLocalDate().daysUntil(task.Deadline).toString()
            if (java.time.LocalDate.now().toKotlinLocalDate().daysUntil(task.Deadline) < 0) {
                tvDeadlineTitle.text = "Дней просрочено"
            }
            tvSince.text = "Задача создана ${task.Since}"
        }
    }
}