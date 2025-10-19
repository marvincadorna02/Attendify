package com.example.trackerabsent

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubjectStudentsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentListContainer: LinearLayout
    private lateinit var tvTitle: TextView

    // Change to String to support IDs like "CC106"
    private var subjectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_students)

        dbHelper = DatabaseHelper(this)
        studentListContainer = findViewById(R.id.studentListContainer)
        tvTitle = findViewById(R.id.tvTitle)

        // Get subject info from intent as String now
        subjectId = intent.getStringExtra("subject_id")
        val subjectName = intent.getStringExtra("subject_name")
        tvTitle.text = "All Students in ${subjectName ?: "Subject"}"

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

    private fun displayStudents(students: List<String>) {
        studentListContainer.removeAllViews()

        if (students.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No students enrolled yet."
                textSize = 16f
                setTextColor(getColor(android.R.color.darker_gray))
                setPadding(10, 20, 10, 20)
            }
            studentListContainer.addView(emptyText)
        } else {
            for (student in students) {
                val studentView = TextView(this).apply {
                    text = "• $student"
                    textSize = 16f
                    setPadding(10, 10, 10, 10)
                    setTextColor(android.graphics.Color.parseColor("#FF000000"))
                }
                studentListContainer.addView(studentView)
            }
        }
    }
}
