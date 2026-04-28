package com.example.androidtaskmanager.ui.activities.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.data.DatabaseService
import com.example.androidtaskmanager.databinding.ActivityAddTaskBinding
import com.example.androidtaskmanager.fragments.HeaderFragment
import com.example.androidtaskmanager.models.Scope
import com.example.androidtaskmanager.models.Status
import com.example.androidtaskmanager.models.Task
import com.example.androidtaskmanager.models.User
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

class AddTaskActivity : AppCompatActivity() {
    companion object{
        lateinit var enteredUser: User
    }
    private lateinit var binding: ActivityAddTaskBinding
    private val db = DatabaseService()
    private var selectedScopeId = 0
    private lateinit var scopesList: List<Scope>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            scopesList = db.getScopes()
            setupScopesSpinner()
        }
        if (savedInstanceState == null){
            val headerFragment = HeaderFragment.newInstance("Добавить новую задачу")
            supportFragmentManager.commit {
                add(R.id.headerTitle, headerFragment)
            }
        }
        with(binding){
            btAdd.setOnClickListener {
                // create a Task object and call db method
                val task = Task(
                    0,
                    User(enteredUser.Id),
                    Status(4),
                    title.text.toString(),
                    "without description",
                    Scope(selectedScopeId),
                    LocalDate.now().toKotlinLocalDate(),
                    kotlinx.datetime.LocalDate.parse(getSelectedDate()),
                    enteredUser.Id
                )
                lifecycleScope.launch {
                    val r = db.putTask(task)
                    if (r) {
                        Toast.makeText(this@AddTaskActivity, "Задача добавлена", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddTaskActivity, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btBack.setOnClickListener {
                finish()
            }
        }
    }
    private fun getSelectedDate(): String{
        var moth: String = binding.deadline.month.plus(1).toString()
        var day: String = binding.deadline.dayOfMonth.toString()

        if (binding.deadline.month < 10){
            moth = "0${binding.deadline.month.plus(1)}"
        }
        if (binding.deadline.dayOfMonth < 10){
            day = "0${binding.deadline.dayOfMonth}"
        }
        return "${binding.deadline.year}-${moth}-${day}"
    }
    private fun setupScopesSpinner(){
        val scopeNames = scopesList.map { it.Name }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            scopeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.scope.adapter = adapter

        binding.scope.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedScopeId = scopesList[position].Id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

/**
* Для добавления задачи:
* val data: JsonObject
 * properties:
 * idOwner, idStatus, title, idScope, since, deadline
* */
