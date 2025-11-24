        package com.example.trackerabsent

        import android.app.NotificationChannel
        import android.app.NotificationManager
        import android.content.Intent
        import android.net.Uri
        import android.os.Build
        import android.os.Bundle
        import android.widget.Button
        import android.widget.ImageView
        import android.widget.Toast
        import androidx.activity.result.contract.ActivityResultContracts
        import androidx.appcompat.app.AppCompatActivity
        import androidx.appcompat.widget.Toolbar
        import androidx.core.app.NotificationCompat
        import androidx.core.app.NotificationManagerCompat
        import com.bumptech.glide.Glide
        import com.google.android.material.textfield.TextInputEditText

        class TeacherProfileActivity : AppCompatActivity() {

            private lateinit var imgProfile: ImageView
            private lateinit var etFullName: TextInputEditText
            private lateinit var etDepartment: TextInputEditText
            private lateinit var etEmail: TextInputEditText
            private lateinit var etContact: TextInputEditText
            private lateinit var btnSave: Button

            private val PREFS_NAME = "TeacherProfilePrefs"
            private val KEY_IMAGE_URI = "profileImageUri"
            private val CHANNEL_ID = "teacher_profile_update_channel"

            // Image picker launcher
            private val pickImage = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    Glide.with(this)
                        .load(it)
                        .circleCrop()
                        .into(imgProfile)

                    // Save image URI permanently
                    val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    prefs.edit()
                        .putString(KEY_IMAGE_URI, it.toString())
                        .apply()
                }
            }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.teacher_profile)

                // Toolbar setup
                val toolbar = findViewById<Toolbar>(R.id.topAppBar)
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = "Teacher Profile"
                toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

                // Find Views
                imgProfile = findViewById(R.id.imgProfile)
                etFullName = findViewById(R.id.etTeacherName)
                etDepartment = findViewById(R.id.etDepartment)
                etEmail = findViewById(R.id.etEmail)
                etContact = findViewById(R.id.etContact)
                btnSave = findViewById(R.id.btnSave)

                val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

                // Load saved profile image
                prefs.getString(KEY_IMAGE_URI, null)?.let {
                    Glide.with(this)
                        .load(Uri.parse(it))
                        .circleCrop()
                        .into(imgProfile)
                }

                // Load saved input fields
                etFullName.setText(prefs.getString("fullName", ""))
                etDepartment.setText(prefs.getString("department", ""))
                etEmail.setText(prefs.getString("email", ""))
                etContact.setText(prefs.getString("contact", ""))

                // Pick new profile image
                imgProfile.setOnClickListener { pickImage.launch("image/*") }

                // Create notification channel
                createNotificationChannel()

                // Save button click
                btnSave.setOnClickListener {
                    // Save all data permanently
                    prefs.edit()
                        .putString("fullName", etFullName.text.toString())
                        .putString("department", etDepartment.text.toString())
                        .putString("email", etEmail.text.toString())
                        .putString("contact", etContact.text.toString())
                        .apply()

                    Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()

                    // Show notification
                    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Profile Updated")
                        .setContentText("Your profile has been saved successfully!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

                    NotificationManagerCompat.from(this).notify(1002, builder.build())

                    // Navigate to Teacher Dashboard
                    val intent = Intent(this, TeacherSettingsActivity::class.java)
                    startActivity(intent)
                    finish() // close this activity
                }
            }

            private fun createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Teacher Profile Updates"
                    val descriptionText = "Notifications when teacher profile is updated"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(CHANNEL_ID, name, importance)
                    channel.description = descriptionText

                    val notificationManager: NotificationManager =
                        getSystemService(NotificationManager::class.java)
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }
