package com.example.exp3app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class MoviesFragment : Fragment(R.layout.fragment_movies) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn1 = view.findViewById<Button>(0)
    }
}