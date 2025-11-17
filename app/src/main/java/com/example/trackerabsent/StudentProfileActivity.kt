package com.example.trackerabsent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: CircleImageView
    private lateinit var etFullname: TextInputEditText
    private lateinit var etCourse: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etContact: TextInputEditText
    private lateinit var btnSave: Button

    private val PREFS_NAME = "StudentProfilePrefs"
    private val KEY_IMAGE_URI = "profileImageUri"
    private val CHANNEL_ID = "profile_update_channel"

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(imgProfile)
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(KEY_IMAGE_URI, it.toString())
                .apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Student Profile"
        }
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Views
        imgProfile = findViewById(R.id.imgProfile)
        etFullname = findViewById(R.id.etFullname)
        etCourse = findViewById(R.id.etCourse)
        etEmail = findViewById(R.id.etEmail)
        etContact = findViewById(R.id.etContact)
        btnSave = findViewById(R.id.btnSave)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Load saved profile image
        prefs.getString(KEY_IMAGE_URI, null)?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .centerCrop()
                .into(imgProfile)
        }

        // Load saved input fields
        etFullname.setText(prefs.getString("studentName", ""))
        etCourse.setText(prefs.getString("course", ""))
        etEmail.setText(prefs.getString("email", ""))
        etContact.setText(prefs.getString("phone", ""))

        // Image picker
        imgProfile.setOnClickListener { pickImage.launch("image/*") }

        // Notification channel
        createNotificationChannel()

        // Save button
        btnSave.setOnClickListener {
            prefs.edit()
                .putString("studentName", etFullname.text.toString())
                .putString("course", etCourse.text.toString())
                .putString("email", etEmail.text.toString())
                .putString("phone", etContact.text.toString())
                .apply()

            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()

            // Notification
            NotificationManagerCompat.from(this).notify(
                1001,
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Profile Updated")
                    .setContentText("Your profile has been saved successfully!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
            )

            // Navigate to Dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Profile Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when profile is updated"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
