package com.example.exp7

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeText: TextView
    private lateinit var longitudeText: TextView
    private lateinit var imagePreview: ImageView
    private lateinit var videoPreview: VideoView

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        displaySelectedMedia(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        latitudeText = findViewById(R.id.latitudeText)
        longitudeText = findViewById(R.id.longitudeText)
        imagePreview = findViewById(R.id.imagePreview)
        videoPreview = findViewById(R.id.videoPreview)

        val getLocationButton: Button = findViewById(R.id.getLocationButton)
        val pickMediaButton: Button = findViewById(R.id.pickMediaButton)

        getLocationButton.setOnClickListener {
            requestLocationAndFetch()
        }

        pickMediaButton.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
    }

    private fun requestLocationAndFetch() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val hasFinePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFinePermission && !hasCoarsePermission) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val bestLocation = getBetterLocation(gpsLocation, networkLocation)

        if (bestLocation != null) {
            latitudeText.text = "Latitude: ${bestLocation.latitude}"
            longitudeText.text = "Longitude: ${bestLocation.longitude}"
        } else {
            Toast.makeText(
                this,
                "Unable to get location. Turn on GPS and try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getBetterLocation(first: Location?, second: Location?): Location? {
        return when {
            first == null -> second
            second == null -> first
            first.time >= second.time -> first
            else -> second
        }
    }

    private fun displaySelectedMedia(uri: Uri) {
        val mimeType = contentResolver.getType(uri).orEmpty()
        if (mimeType.startsWith("video")) {
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = ImageView.GONE
            videoPreview.visibility = VideoView.VISIBLE
            videoPreview.setVideoURI(uri)
            videoPreview.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                videoPreview.start()
            }
        } else {
            videoPreview.stopPlayback()
            videoPreview.visibility = VideoView.GONE
            imagePreview.visibility = ImageView.VISIBLE
            imagePreview.setImageURI(uri)
        }
    }
}