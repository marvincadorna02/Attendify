package com.example.trackerabsent

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SubjectStudentsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentTable: TableLayout
    private lateinit var tvTitle: TextView

    private var subjectId: String? = null
    private var subjectName: String? = null
    private val todayDate: String by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_students)

        dbHelper = DatabaseHelper(this)
        studentTable = findViewById(R.id.studentTableContainer)
        tvTitle = findViewById(R.id.tvTitle)

        subjectId = intent.getStringExtra("subject_id")
        subjectName = intent.getStringExtra("subject_name")

        tvTitle.text = "Students in ${subjectName ?: "Subject"}"

        if (!subjectId.isNullOrEmpty()) {
            loadStudents(subjectId!!)
        } else {
            Toast.makeText(this, "Invalid subject ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStudents(subjectId: String) {
        val students = dbHelper.getStudentsBySubject(subjectId)
        displayStudents(students)
    }

    private fun displayStudents(students: List<Pair<String, String>>) {
        // Keep header row, remove all other rows
        if (studentTable.childCount > 1) {
            studentTable.removeViews(1, studentTable.childCount - 1)
        }

        if (students.isEmpty()) {
            val emptyRow = TableRow(this).apply {
                val textView = TextView(this@SubjectStudentsActivity).apply {
                    text = "No students enrolled yet."
                    textSize = 16f
                    setTextColor(getColor(android.R.color.darker_gray))
                    setPadding(10, 20, 10, 20)
                }
                addView(textView)
            }
            studentTable.addView(emptyRow)
        } else {
            for ((id, name) in students) {
                val row = TableRow(this)

                // ID Cell
                val tvId = TextView(this).apply {
                    text = id
                    textSize = 16f
                    setPadding(10, 10, 10, 10)
                }

                // Name Cell
                val tvName = TextView(this).apply {
                    text = name
                    textSize = 16f
                    setPadding(10, 10, 10, 10)
                }

                // Attendance Spinner
                val spinner = Spinner(this).apply {
                    val options = listOf("Present", "Absent")
                    adapter = ArrayAdapter(
                        this@SubjectStudentsActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        options
                    )

                    // Set current attendance if exists
                    val currentStatus = dbHelper.getAttendance(subjectId!!, id, todayDate)
                    setSelection(if (currentStatus == "present") 0 else if (currentStatus == "absent") 1 else 0)

                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            idPos: Long
                        ) {
                            val selectedStatus = options[position].lowercase()

                            // Check if attendance exists
                            val exists = dbHelper.getAttendance(subjectId!!, id, todayDate)
                            if (exists == null) {
                                dbHelper.insertAttendance(subjectId!!, id, todayDate, selectedStatus)
                            } else {
                                dbHelper.updateAttendance(subjectId!!, id, todayDate, selectedStatus)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }

                row.addView(tvId)
                row.addView(tvName)
                row.addView(spinner)

                studentTable.addView(row)
            }
        }
    }
}
