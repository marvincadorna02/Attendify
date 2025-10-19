package com.example.trackerabsent

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var itemContainer: LinearLayout
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
        itemContainer = findViewById(R.id.itemContainer)

        // Optional: Use a DatePickerDialog for date input
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnCheck.setOnClickListener {
            checkAttendance()
        }

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        fab.setOnClickListener {
            showAddWorkDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format date as yyyy-MM-dd
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            etDate.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }

    private fun checkAttendance() {
        val studentId = etStudentId.text.toString().trim()
        val subjectName = etSubjectId.text.toString().trim()  // this is actually subject NAME input
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

        // 1. Get the subject by name from the DB (you need to add this method in DatabaseHelper)
        val subject = dbHelper.getSubjectByName(subjectName)
        if (subject == null) {
            Toast.makeText(this, "Subject not found", Toast.LENGTH_SHORT).show()
            tvResult.text = ""
            return
        }

        val subjectId = subject.id  // UUID string from DB

        // 2. Now check enrollment using the subjectId (UUID) instead of subjectName
        val isEnrolled = dbHelper.isStudentEnrolledInSubject(subjectId, studentId)
        Log.d("DashboardActivity", "Enrollment check result: $isEnrolled")

        if (!isEnrolled) {
            Toast.makeText(this, "Student is not enrolled in this subject", Toast.LENGTH_SHORT).show()
            tvResult.text = ""
            return
        }

        // 3. Use subjectId to count attendance
        val presentCount = dbHelper.countAttendanceByStatus(studentId, subjectId, date, "present")
        val absentCount = dbHelper.countAttendanceByStatus(studentId, subjectId, date, "absent")

        Log.d("DashboardActivity", "Present count: $presentCount, Absent count: $absentCount")

        tvResult.text = if (presentCount == 0 && absentCount == 0) {
            "No attendance records found for this date."
        } else {
            "Present: $presentCount\nAbsent: $absentCount"
        }
    }


    private fun showAddWorkDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_work, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etWorkName = dialogView.findViewById<EditText>(R.id.etWorkName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreate)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnCreate.setOnClickListener {
            val workName = etWorkName.text.toString().trim()
            if (workName.isEmpty()) {
                etWorkName.error = "Please enter a value"
            } else {
                addItemToDashboard(workName)
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun addItemToDashboard(workName: String) {
        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.item_card, itemContainer, false)

        val subjectName = itemView.findViewById<TextView>(R.id.subjectName)
        val removeButton = itemView.findViewById<ImageButton>(R.id.removeButton)

        subjectName.text = workName

        removeButton.setOnClickListener {
            val confirmDialog = android.app.AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove \"$workName\"?")
                .setPositiveButton("Yes") { _, _ ->
                    itemContainer.removeView(itemView)
                    Toast.makeText(this, "\"$workName\" removed.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .create()
            confirmDialog.show()
        }

        itemContainer.addView(itemView)
    }
}
