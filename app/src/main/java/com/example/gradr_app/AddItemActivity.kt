package com.example.gradr_app

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
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

class AddItemActivity : AppCompatActivity() {

    // initialise companion objects
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_PERMISSIONS = 3
    }

    // initialise necessary variables
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var addDate: TextView
    private lateinit var addTime: TextView
    private var collectionName: String? = null
    private lateinit var itemImage: ImageView
    private var imageUri: Uri? = null

    // activity creation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        // database helper initialisation
        dbHelper = DatabaseHelper(this)

        // UI elements initialisation
        val backButton: ImageView = findViewById(R.id.backButton)
        itemImage = findViewById(R.id.itemImage)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)
        val addName: EditText = findViewById(R.id.addName)
        val addDetails: EditText = findViewById(R.id.addDetails)
        val addValue: EditText = findViewById(R.id.addValue)
        val addQuantity: EditText = findViewById(R.id.addQuantity)
        val saveButton: Button = findViewById(R.id.saveButton)
        addDate = findViewById(R.id.addDate)
        addTime = findViewById(R.id.addTime)

        // get collection name from intent
        collectionName = intent.getStringExtra("collectionName")
        val categoryTitle: TextView = findViewById(R.id.categoryTitle)
        categoryTitle.text = "Category: $collectionName"

        // set clickers for ui elements
        backButton.setOnClickListener {
            onBackPressed()
        }

        changeImageButton.setOnClickListener {
            showImageOptions()
        }

        // date picker listener
        addDate.setOnClickListener {
            showDatePicker()
        }

        // time picker listener
        addTime.setOnClickListener {
            showTimePicker()
        }

        // save button listener
        saveButton.setOnClickListener {
            saveItem(
                addName.text.toString(),
                addDetails.text.toString(),
                addValue.text.toString(),
                addDate.text.toString(),
                addTime.text.toString(),
                addQuantity.text.toString(),
                imageUri
            )
            // after saving, return to the PortfolioActivity
            val intent = Intent(this, PortfolioActivity::class.java).apply {
                putExtra("collectionName", collectionName) // Pass collection name
            }
            startActivity(intent)
            finish()
        }
    }

    // show options to take photo or choose gallery
    private fun showImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    // open camera function
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

    // open gallery to choose photo
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    // permissions for camera and gallery
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

    // handle camera or gallery output intents
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    itemImage.setImageBitmap(imageBitmap)
                    // Convert Bitmap to Uri
                    imageUri = getImageUriFromBitmap(this, imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    itemImage.setImageURI(imageUri)
                }
            }
        }
    }

    // convert bitmap to URI
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    // handle permission request results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
                else {
                }
                return
            }
        }
    }

    // show date picker dialog
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            addDate.text = date
        }, year, month, day)
        datePickerDialog.show()
    }

    // show time picker dialog
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = "$selectedHour:$selectedMinute"
            addTime.text = time
        }, hour, minute, true)
        timePickerDialog.show()
    }

    // save item details to the database and updates
    private fun saveItem(name: String, details: String, value: String, date: String, time: String, quantity: String, imageUri: Uri?) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, name)
            put(DatabaseHelper.COLUMN_DETAILS, details)
            put(DatabaseHelper.COLUMN_VALUE, value)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_TIME, time)
            put(DatabaseHelper.COLUMN_QUANTITY, quantity)
            put(DatabaseHelper.COLUMN_IMAGE_URI, imageUri?.toString()) // Save the URI as a string
            put(DatabaseHelper.COLUMN_PORTFOLIO_NAME, collectionName) // Save collection name
        }

        val result = db.insert(DatabaseHelper.TABLE_PORTFOLIO_ITEMS, null, values)
        Log.d("AddItemActivity", "Inserted item with result: $result, Collection: $collectionName")
    }
}


