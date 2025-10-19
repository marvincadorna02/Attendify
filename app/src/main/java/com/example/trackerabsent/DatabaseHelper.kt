package com.example.trackerabsent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "AbsentTracker.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE subjects (
                id TEXT PRIMARY KEY,
                subject_name TEXT NOT NULL,
                teacher_name TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE student_subjects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                student_school_id TEXT NOT NULL,
                FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_id TEXT NOT NULL,
                student_school_id TEXT NOT NULL,
                date TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS attendance")
        db.execSQL("DROP TABLE IF EXISTS student_subjects")
        db.execSQL("DROP TABLE IF EXISTS subjects")
        onCreate(db)
    }

    fun insertSubject(id: String, subjectName: String, teacherName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id.trim().uppercase())
            put("subject_name", subjectName)
            put("teacher_name", teacherName)
        }
        return db.insert("subjects", null, values)
    }

    fun insertStudentToSubject(subjectId: String, studentId: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("subject_id", subjectId.trim().uppercase())
            put("student_school_id", studentId.trim())
        }
        val result = db.insert("student_subjects", null, values)
        if (result == -1L) {
            Log.e("DatabaseHelper", "Failed to insert student_subject: subjectId=$subjectId, studentId=$studentId. Possibly missing subject in subjects table.")
        } else {
            Log.d("DatabaseHelper", "Inserted student_subject: subjectId=$subjectId, studentId=$studentId, rowId=$result")
        }
        return result
    }

    fun insertAttendance(subjectId: String, studentId: String, date: String, status: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("subject_id", subjectId.trim().uppercase())
            put("student_school_id", studentId.trim())
            put("date", date.trim())
            put("status", status.trim().lowercase())
        }
        return db.insert("attendance", null, values)
    }

    fun getAttendance(subjectId: String, studentId: String, date: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT status FROM attendance
            WHERE subject_id = ? AND student_school_id = ? AND date = ?
            """.trimIndent(),
            arrayOf(subjectId.trim().uppercase(), studentId.trim(), date.trim())
        )
        var status: String? = null
        if (cursor.moveToFirst()) {
            status = cursor.getString(0)
        }
        cursor.close()
        return status
    }

    fun countAttendanceByStatus(
        studentId: String,
        subjectId: String,
        date: String,
        status: String
    ): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT COUNT(*) FROM attendance
            WHERE student_school_id = ? AND subject_id = ? AND date = ? AND status = ?
            """.trimIndent(),
            arrayOf(studentId.trim(), subjectId.trim().uppercase(), date.trim(), status.trim().lowercase())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getSubjectsByTeacher(teacherName: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT subject_name FROM subjects WHERE teacher_name = ?",
            arrayOf(teacherName.trim())
        )
        val subjects = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjects
    }

    fun getSubjectsByStudent(studentId: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT s.subject_name FROM subjects s
            JOIN student_subjects ss ON s.id = ss.subject_id
            WHERE ss.student_school_id = ?
            """.trimIndent(),
            arrayOf(studentId.trim())
        )
        val subjects = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjects
    }

    fun getAllSubjects(): List<Subject> {
        val subjectList = mutableListOf<Subject>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM subjects", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val subjectName = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"))
                val teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
                subjectList.add(Subject(id, subjectName, teacherName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjectList
    }

    fun getAllSubjectsByTeacher(teacherName: String): List<Subject> {
        val subjectList = mutableListOf<Subject>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM subjects WHERE teacher_name = ?",
            arrayOf(teacherName.trim())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val subjectName = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"))
                val teacherNameDb = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
                subjectList.add(Subject(id, subjectName, teacherNameDb))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjectList
    }

    fun getStudentsBySubject(subjectId: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT student_school_id FROM student_subjects WHERE subject_id = ?",
            arrayOf(subjectId.trim().uppercase())
        )

        val students = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                students.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return students
    }

    fun isStudentEnrolledInSubject(subjectId: String, studentId: String): Boolean {
        val trimmedSubjectId = subjectId.trim().uppercase()
        val trimmedStudentId = studentId.trim()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT 1 FROM student_subjects 
            WHERE subject_id = ? AND student_school_id = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(trimmedSubjectId, trimmedStudentId)
        )
        val isEnrolled = cursor.moveToFirst()
        cursor.close()

        Log.d("DatabaseHelper", "Enrollment check for student='$trimmedStudentId', subject='$trimmedSubjectId': $isEnrolled")
        return isEnrolled
    }

    fun getSubjectByName(subjectName: String): Subject? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM subjects WHERE UPPER(subject_name) = ?",
            arrayOf(subjectName.trim().uppercase())
        )
        var subject: Subject? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"))
            val teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"))
            subject = Subject(id, name, teacher)
        }
        cursor.close()
        return subject
    }

    fun logAllEnrollments() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT subject_id, student_school_id FROM student_subjects", null)
        if (cursor.moveToFirst()) {
            do {
                val subjectId = cursor.getString(0)
                val studentId = cursor.getString(1)
                Log.d("DatabaseHelper", "Enrollment Record - Subject: $subjectId, Student: $studentId")
            } while (cursor.moveToNext())
        } else {
            Log.d("DatabaseHelper", "No enrollment records found.")
        }
        cursor.close()
    }

    fun logAllSubjects() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, subject_name FROM subjects", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val name = cursor.getString(1)
                Log.d("DatabaseHelper", "Subject Record - ID: $id, Name: $name")
            } while (cursor.moveToNext())
        } else {
            Log.d("DatabaseHelper", "No subjects found.")
        }
        cursor.close()
    }
}
