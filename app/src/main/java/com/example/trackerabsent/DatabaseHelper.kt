package com.example.trackerabsent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "AbsentTracker.db", null, 6) {

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
                student_name TEXT NOT NULL,
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

    // Insert a new subject
    fun insertSubject(id: String, subjectName: String, teacherName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id.trim().uppercase())
            put("subject_name", subjectName.trim())
            put("teacher_name", teacherName.trim())
        }
        return db.insert("subjects", null, values)
    }

    // Add student to subject
    fun addStudentToSubject(studentId: String, studentName: String, subjectId: String): Boolean {
        if (isStudentEnrolledInSubject(subjectId, studentId)) return false

        val db = writableDatabase
        val values = ContentValues().apply {
            put("subject_id", subjectId.trim().uppercase())
            put("student_school_id", studentId.trim())
            put("student_name", studentName.trim())
        }
        val result = db.insert("student_subjects", null, values)
        return result != -1L
    }

    // Check if student is already enrolled
    fun isStudentEnrolledInSubject(subjectId: String, studentId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM student_subjects WHERE subject_id = ? AND student_school_id = ? LIMIT 1",
            arrayOf(subjectId.trim().uppercase(), studentId.trim())
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // Get students for a subject as Pair(ID, Name)
    fun getStudentsBySubject(subjectId: String): List<Pair<String, String>> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT student_school_id, student_name FROM student_subjects WHERE subject_id = ?",
            arrayOf(subjectId.trim().uppercase())
        )
        val students = mutableListOf<Pair<String, String>>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val name = cursor.getString(1)
                students.add(Pair(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return students
    }

    // Attendance functions
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
            "SELECT status FROM attendance WHERE subject_id=? AND student_school_id=? AND date=?",
            arrayOf(subjectId.trim().uppercase(), studentId.trim(), date.trim())
        )
        val status = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return status
    }

    fun countAttendanceByStatus(studentId: String, subjectId: String, date: String, status: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM attendance WHERE student_school_id=? AND subject_id=? AND date=? AND status=?",
            arrayOf(studentId.trim(), subjectId.trim().uppercase(), date.trim(), status.trim().lowercase())
        )
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    // Get all subjects
    fun getAllSubjects(): List<Subject> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, subject_name, teacher_name FROM subjects", null)
        val list = mutableListOf<Subject>()
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Subject(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Get subject by name
    fun getSubjectByName(subjectName: String): Subject? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, subject_name, teacher_name FROM subjects WHERE UPPER(subject_name)=?",
            arrayOf(subjectName.trim().uppercase())
        )
        var subject: Subject? = null
        if (cursor.moveToFirst()) {
            subject = Subject(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2)
            )
        }
        cursor.close()
        return subject
    }

    // Get subjects by teacher
    fun getSubjectsByTeacher(teacherName: String): List<Subject> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, subject_name, teacher_name FROM subjects WHERE teacher_name=?",
            arrayOf(teacherName.trim())
        )
        val list = mutableListOf<Subject>()
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Subject(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
