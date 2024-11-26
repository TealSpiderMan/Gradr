package com.example.gradr_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {
    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // category dao is initialised
        categoryDAO = CategoryDAO(this)

        // ui elements
        val categoryGridLayout: GridLayout = findViewById(R.id.categoryGridLayout)
        val backButton: ImageView = findViewById(R.id.imageView)

        // back button clicker to end activity
        backButton.setOnClickListener {
            finish()
        }

        // Add static categories for testing
        addStaticCategories()

        // Load categories from database
        loadCategories(categoryGridLayout)

        // "Add new Category" button
        addAddNewCategoryButton(categoryGridLayout)

        // navigation
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

    // static preset categories for testing and visualisation
    private fun addStaticCategories() {
        val staticCategories = listOf(
            Category(name = "Pokemon Cards", imageUri = "android.resource://${packageName}/${R.drawable.pokemon_logo}"),
            Category(name = "Coins", imageUri = "android.resource://${packageName}/${R.drawable.coins_logo}")
        )

        for (category in staticCategories) {
            if (!categoryDAO.isCategoryExists(category.name)) {
                categoryDAO.insertCategory(category)
            }
        }
    }

    // load category class and display in GridLayout
    private fun loadCategories(gridLayout: GridLayout) {
        gridLayout.removeAllViews()
        val categories = categoryDAO.getAllCategories()

        for (category in categories) {
            val imageUri = category.imageUri?.let { Uri.parse(it) } ?: Uri.parse("android.resource://${packageName}/${R.drawable.ic_add_photo}")
            addCategoryItem(gridLayout, CategoryItem(category.id, category.name, imageUri))
        }
    }

    // add category item to GridLayout
    private fun addCategoryItem(gridLayout: GridLayout, category: CategoryItem) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            background = getDrawable(R.drawable.outline_background)
        }

        val imageView = ImageView(this).apply {
            if (category.imageUri is Uri) {
                setImageURI(category.imageUri)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                340
            )
        }

        val textView = TextView(this).apply {
            text = category.name
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 18f
        }

        itemLayout.addView(imageView)
        itemLayout.addView(textView)

        // short clicker to pencil icon
        itemLayout.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java).apply {
                putExtra("collectionName", category.name)
            }
            startActivity(intent)
        }

        // long clicker to pencil icon
        itemLayout.setOnLongClickListener {
            val intent = Intent(this, EditCategoryActivity::class.java).apply {
                putExtra("categoryId", category.id)
                putExtra("categoryName", category.name)
                putExtra("categoryImageUri", category.imageUri.toString())
            }
            startActivityForResult(intent, 1)
            true
        }

        // layout parameters for category item (modify margin)
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(16, 16, 16, 16)
        }

        gridLayout.addView(itemLayout, params)
    }

    // add new category button to GridLayout
    private fun addAddNewCategoryButton(gridLayout: GridLayout) {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            background = getDrawable(R.drawable.outline_background) // Add outline
        }

        // camera icon replaces the box
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.ic_add_photo) // Use the "camera icon"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                340
            )
        }

        // add new category text below the camera icon
        val textView = TextView(this).apply {
            text = "Add new Category"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 18f
        }

        // ui elements in item layout
        itemLayout.addView(imageView)
        itemLayout.addView(textView)
        itemLayout.setOnClickListener {
            val intent = Intent(this@CategoryActivity, AddCategoryActivity::class.java)
            startActivityForResult(intent, 1)
        }

        // fixes alignment of grid for "add new category" (modify here)
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(16, 16, 16, 16)
        }

        gridLayout.addView(itemLayout, params)
    }

    // handle results from Edit and Add category page
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val categoryGridLayout: GridLayout = findViewById(R.id.categoryGridLayout)
            loadCategories(categoryGridLayout)
        }
    }

    data class CategoryItem(val id: Long, val name: String, val imageUri: Uri)
}
