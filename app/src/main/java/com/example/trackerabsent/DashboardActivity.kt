package com.example.trackerabsent

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var etStudentId: EditText
    private lateinit var etSubjectId: EditText
    private lateinit var etDate: EditText
    private lateinit var btnCheck: Button
    private lateinit var tvResult: TextView

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dbHelper = DatabaseHelper(this)

        etStudentId = findViewById(R.id.etStudentId)
        etSubjectId = findViewById(R.id.etSubjectId)
        etDate = findViewById(R.id.etDate)
        btnCheck = findViewById(R.id.btnCheck)
        tvResult = findViewById(R.id.tvResult)

        // Date Picker
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Check attendance button
        btnCheck.setOnClickListener {
            checkAttendance()
        }

        // Profile icon navigation
        val profileIcon = findViewById<ImageView>(R.id.profile)
        profileIcon.setOnClickListener {
            val intent = Intent(this, StudentProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            R.style.SpinnerDatePicker,  // <-- apply custom spinner theme
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                etDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        dialog.show()
    }



    private fun checkAttendance() {
        val studentId = etStudentId.text.toString().trim()
        val subjectName = etSubjectId.text.toString().trim()
        val date = etDate.text.toString().trim()

        Log.d("DashboardActivity", "Input -> studentId='$studentId', subjectName='$subjectName', date='$date'")

        if (studentId.isEmpty()) {
            etStudentId.error = "Enter Student ID"
            return
        }
        if (subjectName.isEmpty()) {
            etSubjectId.error = "Enter Subject Name"
            return
        }
        if (date.isEmpty()) {
            etDate.error = "Enter Date"
            return
        }

        val subject = dbHelper.getSubjectByName(subjectName)
        if (subject == null) {
            Toast.makeText(this, "Subject not found", Toast.LENGTH_SHORT).show()
            tvResult.text = ""
            return
        }

        val subjectId = subject.id
        val isEnrolled = dbHelper.isStudentEnrolledInSubject(subjectId, studentId)

        if (!isEnrolled) {
            Toast.makeText(this, "Student is not enrolled in this subject", Toast.LENGTH_SHORT).show()
            tvResult.text = ""
            return
        }

        val presentCount = dbHelper.countAttendanceByStatus(studentId, subjectId, date, "present")
        val absentCount = dbHelper.countAttendanceByStatus(studentId, subjectId, date, "absent")

        tvResult.text = if (presentCount == 0 && absentCount == 0) {
            "No attendance records found for this date."
        } else {
            "Present: $presentCount\nAbsent: $absentCount"
        }
    }

    // EXIT DIALOG
    override fun onBackPressed() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit this application?")

        builder.setPositiveButton("Yes") { _, _ ->
            finishAffinity()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}
