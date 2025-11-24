package com.example.trackerabsent

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Back arrow click -> Dashboard
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Profile row click -> StudentProfileActivity
        val profileRow = findViewById<LinearLayout>(R.id.profileRow)
        profileRow.setOnClickListener {
            val intent = Intent(this, StudentProfileActivity::class.java)
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

        // Logout row click -> MainActivity
        val logoutRow = findViewById<LinearLayout>(R.id.logoutRow)
        logoutRow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
