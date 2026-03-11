package com.example.androidtaskmanager.ui.activities.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.databinding.ActivityTasksBinding
import com.example.androidtaskmanager.fragments.HeaderFragment

class TasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTasksBinding
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
    private fun updateStatusSwitcherText(): String =
        when (statusSwitcher){
            1 -> "В процессе"
            2 -> "Выполнено"
            3 -> "На проверке"
            else -> "unknown"
        }

}