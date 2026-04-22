package com.example.exp4

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var actionTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Match the sample: always use light mode
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        actionTextView = findViewById(R.id.actionTextView)

        val clickMeButton: Button = findViewById(R.id.clickMeButton)
        val longPressButton: Button = findViewById(R.id.longPressButton)
        val swipeActionButton: Button = findViewById(R.id.swipeActionButton)

        clickMeButton.setOnClickListener {
            showAction(getString(R.string.msg_click_me_clicked))
        }

        longPressButton.setOnLongClickListener {
            showAction(getString(R.string.msg_long_press_detected))
            true
        }

        // Swipe detection tuned to feel like the sample on phones + emulator
        val density = resources.displayMetrics.density
        val swipeMinDistancePx = 96f * density
        val swipeMinVelocityPx = 180f * density

        val swipeDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 == null) return false

                    val deltaX = e2.x - e1.x
                    val deltaY = e2.y - e1.y

                    val isHorizontal = abs(deltaX) > abs(deltaY)
                    val hasDistance = abs(deltaX) >= swipeMinDistancePx
                    val hasVelocity = abs(velocityX) >= swipeMinVelocityPx

                    if (isHorizontal && hasDistance && hasVelocity) {
                        if (deltaX > 0) {
                            showAction(getString(R.string.msg_swipe_right))
                        } else {
                            showAction(getString(R.string.msg_swipe_left))
                        }
                        return true
                    }

                    return false
                }
            }
        )

        // Important: return the result so swipe consumes the touch when detected.
        swipeActionButton.setOnTouchListener { _, event ->
            swipeDetector.onTouchEvent(event)
        }

        swipeActionButton.setOnClickListener {
            showAction(getString(R.string.msg_swipe_action_clicked))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showAction(message: String) {
        actionTextView.text = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}