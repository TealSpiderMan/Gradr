package com.example.gradr_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AppraiseItemActivity : AppCompatActivity() {

    private var itemId: Int = 0
    private var imageResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appraise_item)

        val itemImage: ImageView = findViewById(R.id.itemImage)
        val itemName: TextView = findViewById(R.id.itemName)
        val itemDetails: TextView = findViewById(R.id.itemDetails)
        val itemValue: TextView = findViewById(R.id.itemValue)
        val itemDate: TextView = findViewById(R.id.itemDate)
        val itemTime: TextView = findViewById(R.id.itemTime)
        val itemQuantity: TextView = findViewById(R.id.itemQuantity)
        val editButton: Button = findViewById(R.id.editButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        // Get item details from intent
        itemId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name")
        val details = intent.getStringExtra("details")
        val value = intent.getStringExtra("value")
        val quantity = intent.getStringExtra("quantity")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val collectionName = intent.getStringExtra("collectionName")
        imageResId = intent.getIntExtra("imageResId", 0)

        itemName.text = name
        itemDetails.text = details
        itemValue.text = value
        itemDate.text = date
        itemTime.text = time
        itemQuantity.text = quantity
        itemImage.setImageResource(imageResId)

        backButton.setOnClickListener {
            onBackPressed()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditItemActivity::class.java).apply {
                putExtra("id", itemId)
                putExtra("name", name)
                putExtra("details", details)
                putExtra("value", value)
                putExtra("quantity", quantity)
                putExtra("date", date)
                putExtra("time", time)
                putExtra("imageResId", imageResId)
                putExtra("collectionName", collectionName) // Pass collection name
            }
            startActivity(intent)
        }
    }
}
