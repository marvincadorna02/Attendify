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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tvRegister: TextView
    private lateinit var tvTeacherOnly: TextView // ✅ Added this
    private lateinit var etSchoolID: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var layoutSchoolID: TextInputLayout
    private lateinit var layoutPassword: TextInputLayout
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvRegister = findViewById(R.id.tvRegister)
        tvTeacherOnly = findViewById(R.id.tvForgotPassword) // ✅ Find the TextView
        layoutSchoolID = findViewById(R.id.layoutSchoolID)
        layoutPassword = findViewById(R.id.layoutPassword)
        etSchoolID = findViewById(R.id.schooldID)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btnLogin)

        // ✅ When user clicks "Exclusive for Teachers Only"
        tvTeacherOnly.setOnClickListener {
            val intent = Intent(this, TeacherActivity::class.java)
            startActivity(intent)
        }

        // Open register page
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Default color
        val normalColor = Color.parseColor("#6200EE")
        val errorColor = Color.RED

        // Realtime remove error
        etSchoolID.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    layoutSchoolID.helperText = null
                    layoutSchoolID.boxStrokeColor = normalColor
                    layoutSchoolID.defaultHintTextColor = ColorStateList.valueOf(normalColor)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    layoutPassword.helperText = null
                    layoutPassword.boxStrokeColor = normalColor
                    layoutPassword.defaultHintTextColor = ColorStateList.valueOf(normalColor)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnLogin.setOnClickListener {
            val schoolID = etSchoolID.text.toString().trim()
            val password = etPassword.text.toString().trim()

            var isValid = true

            if (schoolID.isEmpty()) {
                layoutSchoolID.helperText = "Required field"
                layoutSchoolID.setHelperTextColor(ColorStateList.valueOf(errorColor))
                layoutSchoolID.boxStrokeColor = errorColor
                layoutSchoolID.defaultHintTextColor = ColorStateList.valueOf(errorColor)
                isValid = false
            }

            if (password.isEmpty()) {
                layoutPassword.helperText = "Required field"
                layoutPassword.setHelperTextColor(ColorStateList.valueOf(errorColor))
                layoutPassword.boxStrokeColor = errorColor
                layoutPassword.defaultHintTextColor = ColorStateList.valueOf(errorColor)
                isValid = false
            }

            if (isValid) {
                Toast.makeText(this, "Successfully Login!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
