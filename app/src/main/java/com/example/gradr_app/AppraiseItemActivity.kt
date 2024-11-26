package com.example.gradr_app

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AppraiseItemActivity : AppCompatActivity() {

    private var itemId: Int = 0
    private var imageUriString: String? = null
    private lateinit var portfolioItemDAO: PortfolioItemDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appraise_item)

        // initialise DAO
        portfolioItemDAO = PortfolioItemDAO(this)

        // ui elements
        val itemImage: ImageView = findViewById(R.id.itemImage)
        val itemName: TextView = findViewById(R.id.itemName)
        val itemDetails: TextView = findViewById(R.id.itemDetails)
        val itemValue: TextView = findViewById(R.id.itemValue)
        val itemDate: TextView = findViewById(R.id.itemDate)
        val itemTime: TextView = findViewById(R.id.itemTime)
        val itemQuantity: TextView = findViewById(R.id.itemQuantity)
        val editButton: Button = findViewById(R.id.editButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        // get item details from intent
        itemId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name")
        val details = intent.getStringExtra("details")
        val value = intent.getStringExtra("value")
        val quantity = intent.getStringExtra("quantity")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val collectionName = intent.getStringExtra("collectionName")
        imageUriString = intent.getStringExtra("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        // set item details to the ui elements
        itemName.text = name
        itemDetails.text = details
        itemValue.text = value
        itemDate.text = date
        itemTime.text = time
        itemQuantity.text = quantity
        imageUri?.let { itemImage.setImageURI(it) }

        backButton.setOnClickListener {
            onBackPressed()
        }

        Log.d("AppraiseItemActivity", "collectionName: $collectionName")

        // on click listener for edit button
        editButton.setOnClickListener {
            val intent = Intent(this, EditItemActivity::class.java).apply {
                putExtra("id", itemId)
                putExtra("name", name)
                putExtra("details", details)
                putExtra("value", value)
                putExtra("quantity", quantity)
                putExtra("date", date)
                putExtra("time", time)
                putExtra("imageUri", imageUriString)
                putExtra("collectionName", collectionName)
            }
            Log.d("AppraiseItemActivity", "Navigating to EditItemActivity with collectionName: $collectionName")
            startActivity(intent)
        }

        // delete button
        deleteButton.setOnClickListener {
            portfolioItemDAO.deleteItem(itemId)
            setResult(RESULT_OK)
            finish()
        }

        // camera icon
        itemImage.setOnClickListener {
            imageUri?.let { showImageDialog(it) }
        }
    }

    // show the image in a fullscreen dialog
    private fun showImageDialog(imageUri: Uri) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_fullscreen_image)
        val fullscreenImageView: ImageView = dialog.findViewById(R.id.fullscreenImageView)
        fullscreenImageView.setImageURI(imageUri)
        fullscreenImageView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
