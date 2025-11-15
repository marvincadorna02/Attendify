package com.example.trackerabsent

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView

    // SharedPreferences constants
    private val PREFS_NAME = "StudentProfilePrefs"
    private val KEY_IMAGE_URI = "profileImageUri"

    // Register the image picker
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Load image using Glide
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(imgProfile)

            // Save URI to SharedPreferences
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_IMAGE_URI, it.toString())
                .apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Student Profile"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Find ImageView
        imgProfile = findViewById(R.id.imgProfile)

        // Load saved image URI if it exists using Glide
        val savedUri = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_IMAGE_URI, null)
        savedUri?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .centerCrop()
                .into(imgProfile)
        }

        // Open gallery on click
        imgProfile.setOnClickListener {
            pickImage.launch("image/*")
        }
    }
}
