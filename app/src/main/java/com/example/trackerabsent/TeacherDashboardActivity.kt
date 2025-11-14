package com.example.trackerabsent

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var itemContainer: LinearLayout
    private lateinit var btnAddSubject: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        dbHelper = DatabaseHelper(this)
        itemContainer = findViewById(R.id.itemContainer)
        btnAddSubject = findViewById(R.id.addsubject)

        // Make settings icon clickable
        val settingsIcon = findViewById<ImageView>(R.id.settings)
        settingsIcon.setOnClickListener {
            val intent = Intent(this, TeacherProfileActivity::class.java)
            startActivity(intent)
        }

        btnAddSubject.setOnClickListener {
            showAddSubjectDialog()
        }

        loadSubjects()

        // 🔥 Back button exit confirmation
        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }
    }

    // Add a new subject
    private fun showAddSubjectDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null)
        val etSubjectName = dialogView.findViewById<EditText>(R.id.etSubjectName)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAddStudent)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnAdd.setOnClickListener {
            val subjectName = etSubjectName.text.toString().trim()

            if (subjectName.isEmpty()) {
                Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show()
            } else {
                val subjectId = UUID.randomUUID().toString() // unique id
                dbHelper.insertSubject(subjectId, subjectName, "Teacher")
                Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadSubjects()
            }
        }

        dialog.show()
    }

    // Add student to a subject
    private fun showAddStudentDialog(subjectId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val etStudentId = dialogView.findViewById<EditText>(R.id.etStudentId)
        val etStudentName = dialogView.findViewById<EditText>(R.id.etStudentName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnAdd.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()
            val studentName = etStudentName.text.toString().trim()

            if (studentId.isEmpty()) {
                etStudentId.error = "Please enter student ID"
            } else if (studentName.isEmpty()) {
                etStudentName.error = "Please enter student Name"
            } else {
                val success = dbHelper.addStudentToSubject(studentId, studentName, subjectId)
                if (success) {
                    Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Student already enrolled!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // Load all subjects
    private fun loadSubjects() {
        itemContainer.removeAllViews()
        val subjects = dbHelper.getAllSubjects()

        if (subjects.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No subjects yet. Tap + to add one."
                textSize = 16f
                setTextColor(Color.parseColor("#888888"))
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            itemContainer.addView(emptyText)
        } else {
            for (subject in subjects) {
                val subjectView = LayoutInflater.from(this)
                    .inflate(R.layout.subject_item, itemContainer, false)

                val tvSubjectName = subjectView.findViewById<TextView>(R.id.tvSubjectName)
                val btnAddStudent = subjectView.findViewById<Button>(R.id.btnAddStudent)
                val btnRemove = subjectView.findViewById<Button>(R.id.btnRemove)

                tvSubjectName.text = subject.name

                // Open SubjectStudentsActivity on click
                subjectView.setOnClickListener {
                    val intent = Intent(this, SubjectStudentsActivity::class.java)
                    intent.putExtra("subject_id", subject.id)
                    intent.putExtra("subject_name", subject.name)
                    startActivity(intent)
                }

                // Add student
                btnAddStudent.setOnClickListener {
                    showAddStudentDialog(subject.id)
                }

                // Remove subject
                btnRemove.setOnClickListener {
                    val confirmDialog = AlertDialog.Builder(this)
                        .setTitle("Remove Subject")
                        .setMessage("Are you sure you want to delete '${subject.name}'?")
                        .setPositiveButton("Yes") { _, _ ->
                            val db = dbHelper.writableDatabase
                            db.delete("subjects", "id = ?", arrayOf(subject.id))
                            db.close()
                            Toast.makeText(this, "Subject removed", Toast.LENGTH_SHORT).show()
                            loadSubjects()
                        }
                        .setNegativeButton("No", null)
                        .create()
                    confirmDialog.show()
                }

                itemContainer.addView(subjectView)
            }
        }
    }

    // 🔥 Exit App dialog
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
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
