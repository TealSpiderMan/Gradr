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

class PortfolioActivity : AppCompatActivity() {

    private lateinit var portfolioRecyclerView: RecyclerView
    private lateinit var portfolioTitle: TextView
    private lateinit var portfolioSpinner: Spinner
    private lateinit var portfolioItemDAO: PortfolioItemDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        portfolioItemDAO = PortfolioItemDAO(this)

        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView)
        portfolioTitle = findViewById(R.id.portfolioTitle)
        portfolioSpinner = findViewById(R.id.portfolioSpinner)
        val addItemButton: ImageButton = findViewById(R.id.addItemButton)

        addItemButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        val collectionName = intent.getStringExtra("collectionName") ?: "Pokemon Cards"
        Log.d("PortfolioActivity", "Received collectionName: $collectionName")
        portfolioTitle.text = "Portfolio: $collectionName"

        val portfolios = listOf("Pokemon Cards", "Coins")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, portfolios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        portfolioSpinner.adapter = adapter

        portfolioSpinner.setSelection(portfolios.indexOf(collectionName))

        portfolioSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPortfolio = portfolios[position]
                portfolioTitle.text = "Portfolio: $selectedPortfolio"

                // Update the RecyclerView with the selected portfolio's items
                val portfolioItems = portfolioItemDAO.getItemsByPortfolioName(selectedPortfolio)
                Log.d("PortfolioActivity", "Portfolio items count for $selectedPortfolio: ${portfolioItems.size}")
                val adapter = PortfolioAdapter(this@PortfolioActivity, portfolioItems)
                portfolioRecyclerView.adapter = adapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        portfolioRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initialize with the first portfolio
        val initialPortfolioItems = portfolioItemDAO.getItemsByPortfolioName(collectionName)
        Log.d("PortfolioActivity", "Initial items count: ${initialPortfolioItems.size}")
        val initialAdapter = PortfolioAdapter(this, initialPortfolioItems)
        portfolioRecyclerView.adapter = initialAdapter

        // Handle navigation
        val navHome: ImageView = findViewById(R.id.navHome)
        val navPortfolio: ImageView = findViewById(R.id.navPortfolio)

        navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        navPortfolio.setOnClickListener {
            // Current activity is PortfolioActivity, no action needed
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the RecyclerView when returning to PortfolioActivity
        val collectionName = portfolioSpinner.selectedItem.toString()
        val portfolioItems = portfolioItemDAO.getItemsByPortfolioName(collectionName)
        Log.d("PortfolioActivity", "Portfolio items count for $collectionName: ${portfolioItems.size}")
        val adapter = PortfolioAdapter(this, portfolioItems)
        portfolioRecyclerView.adapter = adapter
    }
}
