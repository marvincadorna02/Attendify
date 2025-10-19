package com.example.trackerabsent

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.UUID
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        btnAddSubject.setOnClickListener {
            showAddSubjectDialog()
        }

        loadSubjects()
    }

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
                val subjectId = UUID.randomUUID().toString() // generate unique id for subject
                dbHelper.insertSubject(subjectId, subjectName, "Teacher")
                Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadSubjects()
            }
        }


        dialog.show()
    }

    // Now subjectId is a String, not Int
    private fun showAddStudentDialog(subjectId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val etStudentId = dialogView.findViewById<EditText>(R.id.etStudentId)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnAdd.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()

            if (studentId.isEmpty()) {
                etStudentId.error = "Please enter student ID"
            } else {
                dbHelper.insertStudentToSubject(subjectId, studentId)
                Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun loadSubjects() {
        itemContainer.removeAllViews()
        val subjects = dbHelper.getAllSubjects() // Ensure returns List<Subject> with id as String

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

                tvSubjectName.text = subject.subjectName

                // Use subject.id as String here
                subjectView.setOnClickListener {
                    val intent = Intent(this, SubjectStudentsActivity::class.java)
                    intent.putExtra("subject_id", subject.id)  // String
                    intent.putExtra("subject_name", subject.subjectName)
                    startActivity(intent)
                }

                btnAddStudent.setOnClickListener {
                    showAddStudentDialog(subject.id)
                }

                btnRemove.setOnClickListener {
                    val confirmDialog = AlertDialog.Builder(this)
                        .setTitle("Remove Subject")
                        .setMessage("Are you sure you want to delete '${subject.subjectName}'?")
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
}
