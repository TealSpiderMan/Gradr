package com.example.gradr_app

// data class representing content of portfolio item
data class PortfolioItem(
    val id: Int = 0,
    val name: String,
    val details: String,
    val value: String,
    val date: String,
    val time: String,
    val quantity: String,
    val imageUri: String?,
    val portfolioName: String
)
