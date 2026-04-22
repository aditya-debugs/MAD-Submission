package com.example.exp8authapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        val emailText = findViewById<TextView>(R.id.tvEmail)
        val uidText = findViewById<TextView>(R.id.tvUID)
        val logoutBtn = findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser

        emailText.text = "Email: ${user?.email}"
        uidText.text = "UID: ${user?.uid}"

        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}