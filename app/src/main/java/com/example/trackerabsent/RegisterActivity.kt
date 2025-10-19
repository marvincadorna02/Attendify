package com.example.trackerabsent

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var tvLogin: TextView
    private lateinit var etSchoolID: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etCourse: TextInputEditText
    private lateinit var layoutSchooldID: TextInputLayout
    private lateinit var layoutPasswordd: TextInputLayout
    private lateinit var layoutConfirmPassword: TextInputLayout
    private lateinit var layoutCourse: TextInputLayout
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // connect TextView
        tvLogin = findViewById(R.id.tvLogin)

        // connect EditTexts and Layouts
        etSchoolID = findViewById(R.id.schooldID)
        etPassword = findViewById(R.id.password)
        etConfirmPassword = findViewById(R.id.confirmPassword)
        etCourse = findViewById(R.id.course)

        layoutSchooldID = findViewById(R.id.layoutSchooldID)
        layoutPasswordd = findViewById(R.id.layoutPasswordd)
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword)
        layoutCourse = findViewById(R.id.layoutCourse)

        btnRegister = findViewById(R.id.btnRegister)

        // click listener sa "Already have account? Login here"
        tvLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Define colors
        val normalColor = Color.parseColor("#6200EE") // blue/purple
        val errorColor = Color.RED

        // Helper function for realtime field reset
        fun setupRealtimeValidation(editText: TextInputEditText, layout: TextInputLayout) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        layout.helperText = null
                        layout.boxStrokeColor = normalColor
                        layout.defaultHintTextColor = ColorStateList.valueOf(normalColor)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // Apply realtime validation to all fields
        setupRealtimeValidation(etSchoolID, layoutSchooldID)
        setupRealtimeValidation(etPassword, layoutPasswordd)
        setupRealtimeValidation(etConfirmPassword, layoutConfirmPassword)
        setupRealtimeValidation(etCourse, layoutCourse)

        btnRegister.setOnClickListener {
            val schoolID = etSchoolID.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val course = etCourse.text.toString().trim()

            var isValid = true

            // Reset all colors before validation
            fun setError(layout: TextInputLayout, message: String) {
                layout.helperText = message
                layout.setHelperTextColor(ColorStateList.valueOf(errorColor))
                layout.boxStrokeColor = errorColor
                layout.defaultHintTextColor = ColorStateList.valueOf(errorColor)
            }

            if (schoolID.isEmpty()) {
                setError(layoutSchooldID, "Please enter your School ID")
                isValid = false
            }

            if (password.isEmpty()) {
                setError(layoutPasswordd, "Please enter your Password")
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                setError(layoutConfirmPassword, "Please Confirm your Password")
                isValid = false
            }

            if (course.isEmpty()) {
                setError(layoutCourse, "Please enter your Course")
                isValid = false
            }

            if (isValid && password != confirmPassword) {
                setError(layoutPasswordd, "Your Passwords do not match")
                setError(layoutConfirmPassword, "Your Passwords do not match")
                isValid = false
            }

            if (isValid) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            } else {
                Toast.makeText(this, "Please complete the following", Toast.LENGTH_SHORT).show()
            }
        }

        // back button behavior
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        })
    }
}
