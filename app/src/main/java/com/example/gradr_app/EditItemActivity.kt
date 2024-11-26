package com.example.gradr_app

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

// defines all variables in edit item activity
class EditItemActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_PERMISSIONS = 3
    }

    private lateinit var dbHelper: DatabaseHelper
    private var itemId: Int = 0
    private lateinit var editDate: TextView
    private lateinit var editTime: TextView
    private var collectionName: String? = null
    private lateinit var itemImage: ImageView
    private var imageUri: Uri? = null

    // called when the page is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        // uses database helper
        dbHelper = DatabaseHelper(this)

        // ui elements
        val backButton: ImageView = findViewById(R.id.backButton)
        itemImage = findViewById(R.id.itemImage)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)
        val editName: EditText = findViewById(R.id.editName)
        val editDetails: EditText = findViewById(R.id.editDetails)
        val editValue: EditText = findViewById(R.id.editValue)
        val editQuantity: EditText = findViewById(R.id.editQuantity)
        val saveButton: Button = findViewById(R.id.saveButton)
        editDate = findViewById(R.id.editDate)
        editTime = findViewById(R.id.editTime)

        // retrieve item details from the intent
        itemId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name")
        val details = intent.getStringExtra("details")
        val value = intent.getStringExtra("value")
        val quantity = intent.getStringExtra("quantity")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        collectionName = intent.getStringExtra("collectionName")
        val imageUriString = intent.getStringExtra("imageUri")
        imageUri = imageUriString?.let { Uri.parse(it) }

        // set the item details to the UI elements
        editName.setText(name)
        editDetails.setText(details)
        editValue.setText(value)
        editQuantity.setText(quantity)
        imageUri?.let { itemImage.setImageURI(it) }
        editDate.text = date
        editTime.text = time

        Log.d("EditItemActivity", "collectionName: $collectionName")


        // click listeners for page flow
        backButton.setOnClickListener {
            onBackPressed()
        }

        changeImageButton.setOnClickListener {
            showImageOptions()
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
                imageUri
            )
            // after saving, return to the PortfolioActivity
            val intent = Intent(this, PortfolioActivity::class.java).apply {
                putExtra("collectionName", collectionName) // Pass collection name
            }
            Log.d("EditItemActivity", "Navigating to PortfolioActivity with collectionName: $collectionName")
            startActivity(intent)
            finish()
        }
    }

    // function to show the date picker
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

    // function to show the time picker
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

    // function to show image options
    private fun showImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    // function to open the camera and take a photo (camera access)
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // function to open the gallery and pick a photo (media access)
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    // function to request permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            ),
            REQUEST_PERMISSIONS
        )
    }

    // handle results from camera or gallery intents
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    itemImage.setImageBitmap(imageBitmap)
                    // save the bitmap to the database
                }
                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    itemImage.setImageURI(imageUri)
                    // save the Uri to the database
                }
            }
        }
    }

    // handle permission request results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {

                }
                return
            }
        }
    }

    // save item details to the database
    private fun saveItem(id: Int, name: String, details: String, value: String, date: String, time: String, quantity: String, imageUri: Uri?) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, name)
            put(DatabaseHelper.COLUMN_DETAILS, details)
            put(DatabaseHelper.COLUMN_VALUE, value)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_TIME, time)
            put(DatabaseHelper.COLUMN_QUANTITY, quantity)
            put(DatabaseHelper.COLUMN_IMAGE_URI, imageUri?.toString()) // Store the URI as a string
            put(DatabaseHelper.COLUMN_PORTFOLIO_NAME, collectionName)
        }

        val selection = "${DatabaseHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.update(DatabaseHelper.TABLE_PORTFOLIO_ITEMS, values, selection, selectionArgs)
    }
}
