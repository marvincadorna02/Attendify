package com.example.trackerabsent

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class TeacherSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_settings)

        // Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle back arrow click -> TeacherDashboardActivity
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, TeacherDashboardActivity::class.java)
            startActivity(intent)
            finish() // Close TeacherSettingsActivity
        }

        // Profile row click listener -> TeacherProfileActivity
        val profileRow = findViewById<LinearLayout>(R.id.profileRow)
        profileRow.setOnClickListener {
            val intent = Intent(this, TeacherProfileActivity::class.java)
            startActivity(intent)
        }

        // Notifications toggle
        val notificationSwitch = findViewById<SwitchCompat>(R.id.notificationSwitch)
        notificationSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout row click -> MainActivity (clear back stack)
        val logoutRow = findViewById<LinearLayout>(R.id.logoutRow)
        logoutRow.setOnClickListener {
            val intent = Intent(this, TeacherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close TeacherSettingsActivity
        }
    }
}
