package com.example.androidtaskmanager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.androidtaskmanager.R

class HeaderFragment (): Fragment(R.layout.header_fragment){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.headerTitle)
        textView.text = arguments?.getString(ARG_TITLE)
    }

    companion object{
        private const val ARG_TITLE = "title"

        fun newInstance(title: String): HeaderFragment{
            val fragment = HeaderFragment()
            fragment.arguments = Bundle().apply{
                putString(ARG_TITLE, title)
            }
            return fragment
        }
    }
}