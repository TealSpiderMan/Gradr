package com.example.gradr_app

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val categoryGridLayout: GridLayout = findViewById(R.id.categoryGridLayout)
        val backButton: ImageView = findViewById(R.id.imageView)

        backButton.setOnClickListener {
            finish()
        }

        val categories = listOf(
            CategoryItem("Pokemon", R.drawable.pokemon_logo),
            CategoryItem("Coins", R.drawable.coins_logo)
        )

        for (category in categories) {
            addCategoryItem(categoryGridLayout, category)
        }

        // Add the "Add new Category" button
        addAddNewCategoryButton(categoryGridLayout)

        // Handle navigation
        val navHome: ImageView = findViewById(R.id.navHome)
        val navPortfolio: ImageView = findViewById(R.id.navPortfolio)

        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        navPortfolio.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addCategoryItem(gridLayout: GridLayout, category: CategoryItem) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            background = getDrawable(R.drawable.outline_background) // Add outline
        }

        val imageView = ImageView(this).apply {
            setImageResource(category.imageResId)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                340 // Increase height for larger icons
            )
        }

        val textView = TextView(this).apply {
            text = category.name
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 18f // Increase text size if needed
        }

        itemLayout.addView(imageView)
        itemLayout.addView(textView)

        itemLayout.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java).apply {
                putExtra("collectionName", category.name) // Pass collection name
            }
            startActivity(intent)
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(16, 16, 16, 16) // Add margin for spacing between items
        }

        gridLayout.addView(itemLayout, params)
    }

    private fun addAddNewCategoryButton(gridLayout: GridLayout) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            background = getDrawable(R.drawable.outline_background) // Add outline
        }

        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.ic_add_photo) // Use the appropriate icon
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                340 // Increase height for larger icons
            )
        }

        val textView = TextView(this).apply {
            text = "Add new Category"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 18f // Increase text size if needed
        }

        itemLayout.addView(imageView)
        itemLayout.addView(textView)

        itemLayout.setOnClickListener {
            // Handle add new category
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(16, 16, 16, 16) // Add margin for spacing between items
        }

        gridLayout.addView(itemLayout, params)
    }

    data class CategoryItem(val name: String, val imageResId: Int)
}
