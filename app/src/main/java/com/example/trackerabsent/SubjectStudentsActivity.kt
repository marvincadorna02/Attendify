package com.example.trackerabsent

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SubjectStudentsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentTable: TableLayout
    private lateinit var tvTitle: TextView
    private lateinit var btnAddStudent: Button

    private var subjectId: String? = null
    private var subjectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_students)

        dbHelper = DatabaseHelper(this)
        studentTable = findViewById(R.id.studentTableContainer)
        tvTitle = findViewById(R.id.tvTitle)
        btnAddStudent = findViewById(R.id.btnAddStudent)

        subjectId = intent.getStringExtra("subject_id")
        subjectName = intent.getStringExtra("subject_name")

        tvTitle.text = "All Students in ${subjectName ?: "Subject"}"

        if (!subjectId.isNullOrEmpty()) {
            loadStudents(subjectId!!)
        } else {
            Toast.makeText(this, "Invalid subject ID", Toast.LENGTH_SHORT).show()
        }

        btnAddStudent.setOnClickListener {
            showAddStudentDialog()
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
                val row = TableRow(this).apply {
                    addView(createCell(id))
                    addView(createCell(name))
                }
                studentTable.addView(row)
            }
        }
    }

    private fun createCell(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(10, 10, 10, 10)
            setTextColor(android.graphics.Color.BLACK)
        }
    }

    private fun showAddStudentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val etStudentId = dialogView.findViewById<EditText>(R.id.etStudentId)
        val etStudentName = dialogView.findViewById<EditText>(R.id.etStudentName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnAdd.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()
            val studentName = etStudentName.text.toString().trim()

            if (studentId.isEmpty() || studentName.isEmpty()) {
                Toast.makeText(this, "Please enter both ID and Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            subjectId?.let { id ->
                val success = dbHelper.addStudentToSubject(studentId, studentName, id)
                if (success) {
                    Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show()
                    loadStudents(id)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Student already exists in this subject.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }
}
