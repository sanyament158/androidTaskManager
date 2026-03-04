package com.example.androidtaskmanager.ui.activities.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.databinding.ActivityMainBinding
import com.example.androidtaskmanager.fragments.HeaderFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null){
            val headerFragment = HeaderFragment.newInstance("Все задачи")
            supportFragmentManager.commit {
                add(R.id.headerTitle, headerFragment)
            }
        }


        with (binding){

        }
    }
}