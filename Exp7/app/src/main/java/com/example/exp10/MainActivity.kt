package com.example.exp10

import android.app.AlertDialog
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private lateinit var etName: EditText
    private lateinit var etRoll: EditText
    private lateinit var etBranch: EditText

    private lateinit var btnInsert: Button
    private lateinit var btnViewAll: Button
    private lateinit var btnSearch: Button

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        etName = findViewById(R.id.etName)
        etRoll = findViewById(R.id.etRoll)
        etBranch = findViewById(R.id.etBranch)

        btnInsert = findViewById(R.id.btnInsert)
        btnViewAll = findViewById(R.id.btnViewAll)
        btnSearch = findViewById(R.id.btnSearch)

        tvResult = findViewById(R.id.tvResult)

        btnInsert.setOnClickListener {
            val name = etName.text.toString().trim()
            val roll = etRoll.text.toString().trim()
            val branch = etBranch.text.toString().trim()

            if (name.isEmpty() || roll.isEmpty() || branch.isEmpty()) {
                showToast("Please fill all fields.")
                return@setOnClickListener
            }

            val result = db.insertStudent(name, roll, branch)
            if (result != -1L) {
                showToast("Student inserted successfully.")
                clearFields()
                showAllStudents()
            } else {
                showToast("Insert failed. Roll number may already exist.")
            }
        }

        btnViewAll.setOnClickListener {
            showAllStudents()
        }

        btnSearch.setOnClickListener {
            val query = etName.text.toString().trim()
            if (query.isEmpty()) {
                showToast("Enter a name to search.")
                return@setOnClickListener
            }
            val cursor = db.searchStudent(query)
            displayCursor(cursor, "Search Results")
        }

        // Optional: load existing data on launch
        showAllStudents()
    }

    private fun showAllStudents() {
        val cursor = db.getAllStudents()
        displayCursor(cursor, "All Students")
    }

    private fun displayCursor(cursor: Cursor, title: String) {
        cursor.use {
            if (it.count == 0) {
                tvResult.text = "$title\n\nNo records found."
                tvResult.setOnLongClickListener(null)
                return
            }

            val sb = StringBuilder()
            sb.appendLine("$title  (${it.count} record(s))")
            sb.appendLine("-".repeat(36))

            val idIdx = it.getColumnIndex(DatabaseHelper.COL_ID)
            val nameIdx = it.getColumnIndex(DatabaseHelper.COL_NAME)
            val rollIdx = it.getColumnIndex(DatabaseHelper.COL_ROLL)
            val branchIdx = it.getColumnIndex(DatabaseHelper.COL_BRANCH)

            while (it.moveToNext()) {
                val id = it.getInt(idIdx)
                val name = it.getString(nameIdx)
                val roll = it.getString(rollIdx)
                val branch = it.getString(branchIdx)

                sb.appendLine("ID     : $id")
                sb.appendLine("Name   : $name")
                sb.appendLine("Roll   : $roll")
                sb.appendLine("Branch : $branch")
                sb.appendLine()
            }

            tvResult.text = sb.toString()
        }

        // Long-press the result area to pick a record by ID for update/delete
        tvResult.setOnLongClickListener {
            promptRecordAction()
            true
        }
    }

    private fun promptRecordAction() {
        val input = EditText(this).apply {
            hint = "Enter student ID"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(40, 20, 40, 20)
        }

        AlertDialog.Builder(this)
            .setTitle("Manage Record")
            .setMessage("Enter the ID of the student you want to update or delete:")
            .setView(input)
            .setPositiveButton("Proceed") { _, _ ->
                val idStr = input.text.toString().trim()
                if (idStr.isEmpty()) {
                    showToast("No ID entered.")
                    return@setPositiveButton
                }
                val id = idStr.toIntOrNull() ?: run {
                    showToast("Invalid ID.")
                    return@setPositiveButton
                }
                fetchAndShowActionDialog(id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun fetchAndShowActionDialog(id: Int) {
        val cursor = db.getStudentById(id)
        cursor.use {
            if (!it.moveToFirst()) {
                showToast("No record found with ID $id.")
                return
            }

            val name = it.getString(it.getColumnIndex(DatabaseHelper.COL_NAME))
            val roll = it.getString(it.getColumnIndex(DatabaseHelper.COL_ROLL))
            val branch = it.getString(it.getColumnIndex(DatabaseHelper.COL_BRANCH))

            showActionDialog(id, name, roll, branch)
        }
    }

    private fun showActionDialog(id: Int, name: String, roll: String, branch: String) {
        val options = arrayOf("Update this student", "Delete this student")

        AlertDialog.Builder(this)
            .setTitle("Record ID: $id  |  $name")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateDialog(id, name, roll, branch)
                    1 -> confirmDelete(id, name)
                }
            }
            .show()
    }

    private fun showUpdateDialog(id: Int, oldName: String, oldRoll: String, oldBranch: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null)
        val etN = view.findViewById<EditText>(R.id.dialogEtName).apply { setText(oldName) }
        val etR = view.findViewById<EditText>(R.id.dialogEtRoll).apply { setText(oldRoll) }
        val etB = view.findViewById<EditText>(R.id.dialogEtBranch).apply { setText(oldBranch) }

        AlertDialog.Builder(this)
            .setTitle("Update Student (ID: $id)")
            .setView(view)
            .setPositiveButton("Update") { _, _ ->
                val newName = etN.text.toString().trim()
                val newRoll = etR.text.toString().trim()
                val newBranch = etB.text.toString().trim()

                if (newName.isEmpty() || newRoll.isEmpty() || newBranch.isEmpty()) {
                    showToast("All fields are required.")
                    return@setPositiveButton
                }

                val rows = db.updateStudent(id, newName, newRoll, newBranch)
                showToast(if (rows > 0) "Updated successfully." else "Update failed.")
                showAllStudents()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(id: Int, name: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Delete \"$name\" permanently? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val rows = db.deleteStudent(id)
                showToast(if (rows > 0) "Deleted successfully." else "Delete failed.")
                showAllStudents()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearFields() {
        etName.text.clear()
        etRoll.text.clear()
        etBranch.text.clear()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}