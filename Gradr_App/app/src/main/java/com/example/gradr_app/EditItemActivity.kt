package com.example.gradr_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EditItemActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var imageResId: Int = 0
    private var itemId: Int = 0
    private lateinit var editDate: TextView
    private lateinit var editTime: TextView
    private var collectionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        dbHelper = DatabaseHelper(this)

        val backButton: ImageView = findViewById(R.id.backButton)
        val itemImage: ImageView = findViewById(R.id.itemImage)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)
        val editName: EditText = findViewById(R.id.editName)
        val editDetails: EditText = findViewById(R.id.editDetails)
        val editValue: EditText = findViewById(R.id.editValue)
        val editQuantity: EditText = findViewById(R.id.editQuantity)
        val saveButton: Button = findViewById(R.id.saveButton)
        editDate = findViewById(R.id.editDate)
        editTime = findViewById(R.id.editTime)

        // Get item details from intent
        itemId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name")
        val details = intent.getStringExtra("details")
        val value = intent.getStringExtra("value")
        val quantity = intent.getStringExtra("quantity")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        collectionName = intent.getStringExtra("collectionName")
        imageResId = intent.getIntExtra("imageResId", 0)

        editName.setText(name)
        editDetails.setText(details)
        editValue.setText(value)
        editQuantity.setText(quantity)
        itemImage.setImageResource(imageResId)
        editDate.text = date
        editTime.text = time

        Log.d("EditItemActivity", "collectionName: $collectionName")

        backButton.setOnClickListener {
            onBackPressed()
        }

        changeImageButton.setOnClickListener {
            // Handle image change logic here (either pick from gallery or take a photo)
        }

        editDate.setOnClickListener {
            showDatePicker()
        }

        editTime.setOnClickListener {
            showTimePicker()
        }

        saveButton.setOnClickListener {
            saveItem(
                itemId,
                editName.text.toString(),
                editDetails.text.toString(),
                editValue.text.toString(),
                editDate.text.toString(),
                editTime.text.toString(),
                editQuantity.text.toString(),
                imageResId
            )
            // After saving, return to the PortfolioActivity
            val intent = Intent(this, PortfolioActivity::class.java).apply {
                putExtra("collectionName", collectionName) // Pass collection name
            }
            Log.d("EditItemActivity", "Navigating to PortfolioActivity with collectionName: $collectionName")
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            editDate.text = date
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = "$selectedHour:$selectedMinute"
            editTime.text = time
        }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun saveItem(id: Int, name: String, details: String, value: String, date: String, time: String, quantity: String, imageResId: Int) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, name)
            put(DatabaseHelper.COLUMN_DETAILS, details)
            put(DatabaseHelper.COLUMN_VALUE, value)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_TIME, time)
            put(DatabaseHelper.COLUMN_QUANTITY, quantity)
            put(DatabaseHelper.COLUMN_IMAGE_RES_ID, imageResId)
            put(DatabaseHelper.COLUMN_PORTFOLIO_NAME, collectionName) // Save collection name
        }

        val selection = "${DatabaseHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.update(DatabaseHelper.TABLE_PORTFOLIO_ITEMS, values, selection, selectionArgs)
    }
}
