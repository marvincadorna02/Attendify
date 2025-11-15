package com.example.trackerabsent

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView

    // SharedPreferences constants
    private val PREFS_NAME = "TeacherProfilePrefs"
    private val KEY_IMAGE_URI = "profileImageUri"

    // Register the image picker launcher
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Load selected image with Glide into circle
            Glide.with(this@TeacherProfileActivity)
                .load(it)
                .circleCrop()
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
        setContentView(R.layout.teacher_profile)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Teacher Profile"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Find ImageView
        imgProfile = findViewById(R.id.imgProfile)

        // Load saved image URI (if exists) using Glide
        val savedUri = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_IMAGE_URI, null)
        savedUri?.let {
            Glide.with(this@TeacherProfileActivity)
                .load(Uri.parse(it))
                .circleCrop()
                .into(imgProfile)
        }

        // Click to open gallery
        imgProfile.setOnClickListener {
            pickImage.launch("image/*")
        }
    }
}
