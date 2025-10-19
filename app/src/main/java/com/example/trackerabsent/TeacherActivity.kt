package com.example.trackerabsent

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TeacherActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var layoutUsername: TextInputLayout
    private lateinit var layoutPassword: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var tvStudentPanel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        // Initialize views
        etUsername = findViewById(R.id.teacheruser)
        etPassword = findViewById(R.id.teacherpassword)
        layoutUsername = findViewById(R.id.layoutSchoolID)
        layoutPassword = findViewById(R.id.layoutPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvStudentPanel = findViewById(R.id.studentpanel)

        // Switch to Student Panel (MainActivity)
        tvStudentPanel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Colors for states
        val normalColor = Color.parseColor("#8c52ff")
        val errorColor = Color.RED

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            var isValid = true

            // Reset colors before validation
            layoutUsername.boxStrokeColor = normalColor
            layoutPassword.boxStrokeColor = normalColor
            layoutUsername.helperText = null
            layoutPassword.helperText = null

            // Check empty fields
            if (username.isEmpty()) {
                layoutUsername.helperText = "Please type your username"
                layoutUsername.setHelperTextColor(ColorStateList.valueOf(errorColor))
                layoutUsername.boxStrokeColor = errorColor
                layoutUsername.defaultHintTextColor = ColorStateList.valueOf(errorColor)
                isValid = false
            } else {
                layoutUsername.helperText = null
                layoutUsername.boxStrokeColor = normalColor
                layoutUsername.defaultHintTextColor = ColorStateList.valueOf(normalColor)
            }

            if (password.isEmpty()) {
                layoutPassword.helperText = "Please type your password"
                layoutPassword.setHelperTextColor(ColorStateList.valueOf(errorColor))
                layoutPassword.boxStrokeColor = errorColor
                layoutPassword.defaultHintTextColor = ColorStateList.valueOf(errorColor)
                isValid = false
            } else {
                layoutPassword.helperText = null
                layoutPassword.boxStrokeColor = normalColor
                layoutPassword.defaultHintTextColor = ColorStateList.valueOf(normalColor)
            }

            // Validate login credentials
            if (isValid) {
                if (username == "teacher" && password == "teacher123") {
                    Toast.makeText(this, "Welcome Teacher!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TeacherDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // ❌ Wrong credentials → turn both inputs red
                    layoutUsername.helperText = "Invalid username or password"
                    layoutUsername.setHelperTextColor(ColorStateList.valueOf(errorColor))
                    layoutUsername.boxStrokeColor = errorColor
                    layoutUsername.defaultHintTextColor = ColorStateList.valueOf(errorColor)

                    layoutPassword.helperText = "Invalid username or password"
                    layoutPassword.setHelperTextColor(ColorStateList.valueOf(errorColor))
                    layoutPassword.boxStrokeColor = errorColor
                    layoutPassword.defaultHintTextColor = ColorStateList.valueOf(errorColor)

                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
