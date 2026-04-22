package com.example.exp5

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // SharedPreferences constants
    private val PREF_NAME = "UserPrefs"
    private val KEY_NAME  = "user_name"
    private val KEY_EMAIL = "user_email"
    private val KEY_AGE   = "user_age"

    private lateinit var sharedPreferences: SharedPreferences

    // UI references
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAge: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLoad: Button
    private lateinit var btnClear: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Bind views
        etName   = findViewById(R.id.etName)
        etEmail  = findViewById(R.id.etEmail)
        etAge    = findViewById(R.id.etAge)
        btnSave  = findViewById(R.id.btnSave)
        btnLoad  = findViewById(R.id.btnLoad)
        btnClear = findViewById(R.id.btnClear)
        tvStatus = findViewById(R.id.tvStatus)

        // ── SAVE ──────────────────────────────────────────────
        btnSave.setOnClickListener {
            val name  = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val age   = etAge.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || age.isEmpty()) {
                showStatus("⚠️ Please fill all fields before saving.", false)
                return@setOnClickListener
            }

            with(sharedPreferences.edit()) {
                putString(KEY_NAME,  name)
                putString(KEY_EMAIL, email)
                putInt(KEY_AGE, age.toIntOrNull() ?: 0)
                apply()          // async write — use commit() if you need synchronous
            }

            showStatus("✅ Data saved successfully!", true)
        }

        // ── LOAD ──────────────────────────────────────────────
        btnLoad.setOnClickListener {
            val name  = sharedPreferences.getString(KEY_NAME,  null)
            val email = sharedPreferences.getString(KEY_EMAIL, null)
            val age   = sharedPreferences.getInt(KEY_AGE, -1)

            if (name == null) {
                showStatus("ℹ️ No saved data found. Save something first!", false)
                return@setOnClickListener
            }

            etName.setText(name)
            etEmail.setText(email)
            etAge.setText(age.toString())
            showStatus("📂 Data loaded successfully!", true)
        }

        // ── CLEAR ─────────────────────────────────────────────
        btnClear.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            etName.text.clear()
            etEmail.text.clear()
            etAge.text.clear()
            showStatus("🗑️ All data cleared.", false)
        }
    }

    private fun showStatus(message: String, success: Boolean) {
        tvStatus.text = message
        tvStatus.setTextColor(
            if (success)
                getColor(android.R.color.holo_green_dark)
            else
                getColor(android.R.color.holo_orange_dark)
        )
    }
}