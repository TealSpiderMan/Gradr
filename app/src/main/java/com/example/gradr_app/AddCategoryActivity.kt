package com.example.gradr_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddCategoryActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_PERMISSIONS = 3
    }

    private lateinit var categoryNameEditText: EditText
    private lateinit var categoryImageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // initialise DAO
        categoryDAO = CategoryDAO(this)

        // ui elements from xml
        categoryNameEditText = findViewById(R.id.categoryNameEditText)
        categoryImageView = findViewById(R.id.categoryImageView)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)
        val addCategoryButton: Button = findViewById(R.id.addCategoryButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        // back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        // change image button
        changeImageButton.setOnClickListener {
            showImageOptions()
        }

        // create add category button (has a camera icon image attached)
        addCategoryButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()
            if (categoryName.isNotBlank()) {
                val category = Category(name = categoryName, imageUri = imageUri?.toString())
                categoryDAO.insertCategory(category)
                val intent = Intent().apply {
                    putExtra("categoryUpdated", true)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                // Display an error message
            }
        }
    }

    // show options to take from camera or gallery
    private fun showImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    // open camera
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

    // open gallery
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    // request permission for camera and gallery access
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
                    categoryImageView.setImageBitmap(imageBitmap)
                    imageUri = getImageUriFromBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    categoryImageView.setImageURI(imageUri)
                }
            }
        }
    }

    // convert bitmap to URI
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "CategoryImage", null)
        return Uri.parse(path)
    }

    // handle permission request results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted
                } else {
                    // Permission denied
                }
            }
        }
    }
}
