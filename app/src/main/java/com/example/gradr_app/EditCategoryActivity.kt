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

// edit category page
class EditCategoryActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_PERMISSIONS = 3
        private const val TAG = "EditCategoryActivity"
    }

    private lateinit var categoryNameEditText: EditText
    private lateinit var categoryImageView: ImageView
    private var imageUri: Uri? = null
    private var categoryId: Long = -1
    private lateinit var categoryDAO: CategoryDAO
    private var categoryChanged = false

    // activity on create page
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // initialise DAO for category
        categoryDAO = CategoryDAO(this)

        // ui elements
        categoryNameEditText = findViewById(R.id.categoryNameEditText)
        categoryImageView = findViewById(R.id.categoryImageView)
        val changeImageButton: Button = findViewById(R.id.changeImageButton)
        val saveCategoryButton: Button = findViewById(R.id.saveCategoryButton)
        val deleteCategoryButton: Button = findViewById(R.id.deleteCategoryButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        // back button clicker
        backButton.setOnClickListener {
            finish()
        }

        // get category details from intent
        categoryId = intent.getLongExtra("categoryId", -1)
        val categoryName = intent.getStringExtra("categoryName")
        val categoryImageUri = intent.getStringExtra("categoryImageUri")

        if (categoryId == -1L || categoryName == null) {
            finish()
            return
        }

        // set category details to the repective UI elements
        categoryNameEditText.setText(categoryName)
        if (categoryImageUri != null) {
            imageUri = Uri.parse(categoryImageUri)
            categoryImageView.setImageURI(imageUri)
        }

        // clicker to show large image
        changeImageButton.setOnClickListener {
            showImageOptions()
        }

        // save button clicker
        saveCategoryButton.setOnClickListener {
            val newName = categoryNameEditText.text.toString()
            if (newName.isNotBlank()) {
                val updatedCategory = Category(categoryId, newName, imageUri?.toString())
                categoryDAO.updateCategory(updatedCategory)
                categoryChanged = true
                finish()
            } else {
            }
        }

        // delete button clicker
        deleteCategoryButton.setOnClickListener {
            categoryDAO.deleteCategory(categoryId)
            categoryChanged = true
            finish()
        }
    }

    // show image option dialog (take photo, open gallery..)
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

    // camera functions
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

    // gallery functions
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    // request permission functions
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

    // get image URI from bitmap functions
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
                    // Permission was granted, you can perform the action
                } else {
                    // Permission denied, show a message to the user
                }
            }
        }
    }

    // finish activity and set result if category was changed
    override fun finish() {
        if (categoryChanged) {
            setResult(Activity.RESULT_OK)
        }
        super.finish()
    }
}
