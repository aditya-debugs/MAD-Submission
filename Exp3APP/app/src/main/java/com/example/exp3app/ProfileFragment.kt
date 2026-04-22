package com.example.exp3app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvProfile = view.findViewById<TextView>(R.id.tvProfile)

        val name = arguments?.getString("USERNAME")
        tvProfile.text = "User: $name"
    }
}