package com.example.gradr_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//initialize main activity class
class MainActivity : AppCompatActivity() {

    //declare variables
    private lateinit var collectionsRecyclerView: RecyclerView
    private lateinit var viewAll: TextView
    private lateinit var navHome: ImageView
    private lateinit var navPortfolio: ImageView
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var portfolioItemDAO: PortfolioItemDAO
    private lateinit var totalAssetWorthTextView: TextView
    private lateinit var mostValuable1Name: TextView
    private lateinit var mostValuable1Value: TextView
    private lateinit var mostValuable2Name: TextView
    private lateinit var mostValuable2Value: TextView
    private lateinit var mostValuable3Name: TextView
    private lateinit var mostValuable3Value: TextView

    //call when activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ui elements
        collectionsRecyclerView = findViewById(R.id.collectionsRecyclerView)
        viewAll = findViewById(R.id.viewAll)
        navHome = findViewById(R.id.navHome)
        navPortfolio = findViewById(R.id.navPortfolio)
        totalAssetWorthTextView = findViewById(R.id.valueText)
        mostValuable1Name = findViewById(R.id.mostValuable1Name)
        mostValuable1Value = findViewById(R.id.mostValuable1Value)
        mostValuable2Name = findViewById(R.id.mostValuable2Name)
        mostValuable2Value = findViewById(R.id.mostValuable2Value)
        mostValuable3Name = findViewById(R.id.mostValuable3Name)
        mostValuable3Value = findViewById(R.id.mostValuable3Value)

        // dao are initialised
        categoryDAO = CategoryDAO(this)
        portfolioItemDAO = PortfolioItemDAO(this)

        // load categories and load valuable items data
        loadCategoriesAndCollections()
        loadMostValuableItems()

        // view all button
        viewAll.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            intent.putExtra("collectionName", "Main")
            startActivity(intent)
        }

        // home button which does not do anything currently
        navHome.setOnClickListener {
            // Navigate to Home
        }

        // portfolio button in nav bar
        navPortfolio.setOnClickListener {
            // Navigate to Portfolio
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to load categories and collections data
    private fun loadCategoriesAndCollections() {
        val categories = categoryDAO.getAllCategories()
        val collectionItems = categories.map { category ->
            val totalValue = portfolioItemDAO.getTotalValueByPortfolioName(category.name)
            CollectionItem(
                name = category.name,
                value = "MYR %.2f".format(totalValue),
                imageUri = category.imageUri?.let { Uri.parse(it) } ?: Uri.parse("android.resource://${packageName}/${R.drawable.ic_add_photo}")
            )
        }

        // set up recycler view with linearlayourmanager and adapter
        collectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        collectionsRecyclerView.adapter = CollectionAdapter(this, collectionItems)

        // calculate total asset and display it
        val totalAssetWorth = collectionItems.sumOf {
            it.value.removePrefix("MYR ").toDoubleOrNull() ?: 0.0
        }
        totalAssetWorthTextView.text = "MYR %.2f".format(totalAssetWorth)
    }

    // function to load the most valuable items data
    private fun loadMostValuableItems() {
        val mostValuableItems = portfolioItemDAO.getAllItems()
            .mapNotNull { item ->
                val value = item.value.removePrefix("MYR ").toDoubleOrNull()
                if (value != null) {
                    item.copy(value = "MYR %.2f".format(value))
                } else {
                    null
                }
            }
            .sortedByDescending { it.value.removePrefix("MYR ").toDouble() }
            .take(3)

        // display the most valuable items
        if (mostValuableItems.isNotEmpty()) {
            mostValuable1Name.text = mostValuableItems[0].name
            mostValuable1Value.text = mostValuableItems[0].value
        }

        if (mostValuableItems.size > 1) {
            mostValuable2Name.text = mostValuableItems[1].name
            mostValuable2Value.text = mostValuableItems[1].value
        }

        if (mostValuableItems.size > 2) {
            mostValuable3Name.text = mostValuableItems[2].name
            mostValuable3Value.text = mostValuableItems[2].value
        }
    }
}
