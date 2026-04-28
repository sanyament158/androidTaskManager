package com.example.androidtaskmanager.ui.activities.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityTaskDetailsBinding
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import kotlinx.coroutines.coroutineScope
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
    private val onSwitchTask: () -> Unit = {
        setupActionButton()
        setupTaskFields()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init a tasks list
        lifecycleScope.launch {
            tasks = db.getTasks().filter { it.Status.Id == task.Status.Id }.toMutableList()
            actualTaskIndex = tasks.indexOf(
                tasks.find { it.Id == task.Id }
            )
        }

        if (task.Id == 0) {
            throw Exception("task == null")
        }

        with(binding) {
            onSwitchTask()
            Log.i("taskinfo", task.IdUserTaked.toString())
            btNext.setOnClickListener {
                if (actualTaskIndex == tasks.count() - 1) {
                    actualTaskIndex = 0
                } else {
                    actualTaskIndex++
                }

                task = tasks[actualTaskIndex]
                onSwitchTask()
            }
            btPrevious.setOnClickListener {
                if (actualTaskIndex == 0) {
                    actualTaskIndex = tasks.count() - 1
                } else {
                    actualTaskIndex--
                }

                task = tasks[actualTaskIndex]
                onSwitchTask()
            }
            btBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun sendForVerify() {
        lifecycleScope.launch {
            db.sendTaskForVerify(task)
            finish()
        }
    }

    private fun sendForFinished() {
        lifecycleScope.launch {
            db.verifyTask(task, enteredUser.Id)
            finish()
        }
    }
    private fun sendForWaiting(){
        lifecycleScope.launch {
            db.sendTaskForWaiting(task)
            finish()
        }
    }
    private fun takeTask(){
        lifecycleScope.launch{
            db.takeTask(task, enteredUser.Id)
            finish()
        }
    }
    private fun sendForFinishedWithoutUserTaked(){
        lifecycleScope.launch{
            db.takeTask(task, enteredUser.Id)
            finish()
        }
    }

    private fun setupTaskFields() {
        with(binding) {
            tvTaskTitle.text = "Задача ${task.Title}"
            tvOwner.text = task.Owner.Lname
            tvStatus.text = task.Status.Name
            tvScope.text = task.Scope.Name
            lifecycleScope.launch {
                if (task.IdUserTaked != null){
                    val userTakedName =
                        db.getUsers().filter { it.Id == task.IdUserTaked }.first().Lname
                    tvUserTaked.text = userTakedName
                } else {
                    tvUserTaked.text = "Задачу никто не взял"
                }
            }

            tvDeadline.text =
                java.time.LocalDate.now().toKotlinLocalDate().daysUntil(task.Deadline).toString()
            if (java.time.LocalDate.now().toKotlinLocalDate().daysUntil(task.Deadline) < 0) {
                tvDeadlineTitle.text = "Дней просрочено"
            }
            tvSince.text = "Задача создана ${task.Since}"
        }
    }
    private fun setupActionButton(){
        with(binding){
            when (task.Status.Id) {
                // if status == 'В процессе'
                1 -> {
                    if (enteredUser.Id == task.IdUserTaked){
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
                    else {
                        btAction.isVisible = false
                    }
                }
                // if status == 'Готово'
                2 -> {
                    btAction.text = "Задача уже выполнена"
                    btAction.isEnabled = false
                    btAction.isVisible = false
                }
                // if status == 'На проверке'
                3 -> {
                    if (task.Owner.Id == enteredUser.Id){
                        btAction.text = "Проверить"
                        btAction.setOnClickListener {
                            AlertDialog.Builder(this@TaskDetailsActivity)
                                .setTitle("Подтверждение")
                                .setMessage("Результат удовлетворительный?")
                                .setNegativeButton("Нет") { dialog, _ -> sendForWaiting() }
                                .setPositiveButton("Да") { _, _ -> sendForFinished() }
                                .show()
                        }
                    } else {
                        btAction.isVisible = false
                    }
                }
                // if status == 'В ожидании'
                4 -> {
                    if (task.Owner.Id == enteredUser.Id){
                        btAction.text = "Проверить"
                        btAction.setOnClickListener {
                            AlertDialog.Builder(this@TaskDetailsActivity)
                                .setTitle("Подтверждение")
                                .setMessage("Результат удовлетворительный?")
                                .setNegativeButton("Нет") { dialog, _ -> sendForWaiting() }
                                .setPositiveButton("Да") { _, _ -> sendForFinishedWithoutUserTaked() }
                                .show()
                        }
                    } else {
                            btAction.text = "Взять задачу"
                            var isResponsible = false
                            for (s in enteredUser.Scopes) {
                                if (s.Id == task.Scope.Id) {
                                    isResponsible = true
                                    btAction.setOnClickListener {
                                        AlertDialog.Builder(this@TaskDetailsActivity)
                                            .setTitle("Подтверждение")
                                            .setMessage("Вы хотите взять задачу?")
                                            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                            .setPositiveButton("Да") { _, _ -> takeTask() }
                                            .show()
                                    }
                                }
                            }
                            if (!isResponsible) {
                                btAction.background = R.drawable.field_shape.toDrawable()
                            }
                        }
                }
            }
        }
    }
}