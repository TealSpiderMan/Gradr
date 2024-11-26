package com.example.gradr_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var collectionsRecyclerView: RecyclerView
    private lateinit var viewAll: TextView
    private lateinit var navHome: ImageView
    private lateinit var navPortfolio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collectionsRecyclerView = findViewById(R.id.collectionsRecyclerView)
        viewAll = findViewById(R.id.viewAll)
        navHome = findViewById(R.id.navHome)
        navPortfolio = findViewById(R.id.navPortfolio)

        // Sample data for collections
        val collections = listOf(
            CollectionItem("Pokemon Cards", "MYR 144.38", "+ MYR 2.17 (1.50%)", R.drawable.pokemon_logo),
            CollectionItem("Coins", "MYR 5000.00", "+ MYR 0.00 (0.00%)", R.drawable.coins_logo)
        )

        collectionsRecyclerView.layoutManager = LinearLayoutManager(this)
        collectionsRecyclerView.adapter = CollectionAdapter(this, collections)

        viewAll.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            intent.putExtra("collectionName", "Main")
            startActivity(intent)
        }

        navHome.setOnClickListener {
            // Navigate to Home
        }

        navPortfolio.setOnClickListener {
            // Navigate to Portfolio
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
        }
    }
}
