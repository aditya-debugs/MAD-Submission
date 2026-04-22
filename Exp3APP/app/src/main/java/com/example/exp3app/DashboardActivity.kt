package com.example.exp3app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnMovies = findViewById<Button>(R.id.btnMovies)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnDial = findViewById<Button>(R.id.btnDial)
        val btnTrailer = findViewById<Button>(R.id.btnTrailer)

        val name = intent.getStringExtra("USERNAME")
        tvWelcome.text = "Welcome $name"

        btnMovies.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MoviesFragment())
                .addToBackStack(null)
                .commit()

            Toast.makeText(this, "Movies Loaded", Toast.LENGTH_SHORT).show()
        }

        btnProfile.setOnClickListener {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("USERNAME", name)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()

            Toast.makeText(this, "Profile Loaded", Toast.LENGTH_SHORT).show()
        }

        btnDial.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:9876543210")
            startActivity(intent)
        }

        btnTrailer.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com")
            startActivity(intent)
        }
    }
}