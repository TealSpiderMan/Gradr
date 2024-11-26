package com.example.gradr_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// class for displaying and managing portfolio items in the portfolio page
class PortfolioActivity : AppCompatActivity() {

    private lateinit var portfolioRecyclerView: RecyclerView
    private lateinit var portfolioTitle: TextView
    private lateinit var portfolioSpinner: Spinner
    private lateinit var portfolioItemDAO: PortfolioItemDAO
    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        // initializing DAOs
        portfolioItemDAO = PortfolioItemDAO(this)
        categoryDAO = CategoryDAO(this)

        // UI elements
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView)
        portfolioTitle = findViewById(R.id.portfolioTitle)
        portfolioSpinner = findViewById(R.id.portfolioSpinner)
        val addItemButton: ImageButton = findViewById(R.id.addItemButton)

        // normal clicker to add an item
        addItemButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra(
                "collectionName",
                portfolioSpinner.selectedItem.toString()
            )
            startActivity(intent)
        }

        // long clicker to open CategoryActivity
        addItemButton.setOnLongClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            true  // indicate long press was handled
        }

        // retrieve the collection name from the intent or default to "Pokemon Cards"
        val collectionName = intent.getStringExtra("collectionName") ?: "Pokemon Cards"
        Log.d("PortfolioActivity", "Received collectionName: $collectionName")
        portfolioTitle.text = "Portfolio: $collectionName"

        addStaticCategories() // add static categories

        // load categories from database
        val categories = categoryDAO.getAllCategories().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        portfolioSpinner.adapter = adapter

        // set a spinner to the received collection name
        portfolioSpinner.setSelection(categories.indexOf(collectionName))

        // set an item selected listener on the spinner
        portfolioSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedPortfolio = categories[position]
                portfolioTitle.text = "Portfolio: $selectedPortfolio"

                // update the RecyclerView with the selected portfolio's items
                val portfolioItems = portfolioItemDAO.getItemsByPortfolioName(selectedPortfolio)
                Log.d(
                    "PortfolioActivity",
                    "Portfolio items count for $selectedPortfolio: ${portfolioItems.size}"
                )
                val adapter = PortfolioAdapter(this@PortfolioActivity, portfolioItems)
                portfolioRecyclerView.adapter = adapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // set the RecyclerView layout manager to GridLayoutManager with 2 columns (modify here)
        portfolioRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // initialize the RecyclerView with the items
        val initialPortfolioItems = portfolioItemDAO.getItemsByPortfolioName(collectionName)
        Log.d("PortfolioActivity", "Initial items count: ${initialPortfolioItems.size}")
        val initialAdapter = PortfolioAdapter(this, initialPortfolioItems)
        portfolioRecyclerView.adapter = initialAdapter

        // nav
        val navHome: ImageView = findViewById(R.id.navHome)
        val navPortfolio: ImageView = findViewById(R.id.navPortfolio)

        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        navPortfolio.setOnClickListener {
        }
    }

    // called when the activity is resumed
    override fun onResume() {
        super.onResume()
        val selectedItem = portfolioSpinner.selectedItem
        if (selectedItem != null) {
            val collectionName = selectedItem.toString()
            val portfolioItems = portfolioItemDAO.getItemsByPortfolioName(collectionName)
            Log.d(
                "PortfolioActivity",
                "Portfolio items count for $collectionName: ${portfolioItems.size}"
            )
            val adapter = PortfolioAdapter(this, portfolioItems)
            portfolioRecyclerView.adapter = adapter
        } else {
            Log.e("PortfolioActivity", "Selected item is null in onResume")
        }
    }

    // Static categories to test
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
}
