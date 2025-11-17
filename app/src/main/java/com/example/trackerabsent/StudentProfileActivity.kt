package com.example.trackerabsent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText

class StudentProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var etStudentName: EditText
    private lateinit var etStudentId: EditText
    private lateinit var etCourse: TextInputEditText
    private lateinit var etYearLevel: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var btnSave: Button

    private val PREFS_NAME = "StudentProfilePrefs"
    private val KEY_IMAGE_URI = "profileImageUri"
    private val CHANNEL_ID = "profile_update_channel"

    // Image picker launcher
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
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

        // Find Views
        imgProfile = findViewById(R.id.imgProfile)
        etStudentName = findViewById(R.id.etStudentName)
        etStudentId = findViewById(R.id.etStudentId)
        etCourse = findViewById(R.id.etCourse)
        etYearLevel = findViewById(R.id.etYearLevel)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etContact)
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
        etStudentName.setText(prefs.getString("studentName", ""))
        etStudentId.setText(prefs.getString("studentId", ""))
        etCourse.setText(prefs.getString("course", ""))
        etYearLevel.setText(prefs.getString("yearLevel", ""))
        etEmail.setText(prefs.getString("email", ""))
        etPhone.setText(prefs.getString("phone", ""))

        // Click to pick new profile image
        imgProfile.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Create notification channel
        createNotificationChannel()

        // Save button click
        btnSave.setOnClickListener {
            // Save all input fields
            prefs.edit()
                .putString("studentName", etStudentName.text.toString())
                .putString("studentId", etStudentId.text.toString())
                .putString("course", etCourse.text.toString())
                .putString("yearLevel", etYearLevel.text.toString())
                .putString("email", etEmail.text.toString())
                .putString("phone", etPhone.text.toString())
                .apply()

            // Show notification
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Profile Updated")
                .setContentText("Your profile has been saved successfully!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            NotificationManagerCompat.from(this).notify(1001, builder.build())

            // Navigate to DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // close this activity
        }
    }

    // Notification channel setup
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Profile Updates"
            val descriptionText = "Notifications when profile is updated"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
