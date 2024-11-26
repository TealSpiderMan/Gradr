package com.example.gradr_app

data class PortfolioItem(
    val id: Int = 0,
    val name: String,
    val details: String,
    val value: String,
    val date: String,
    val time: String,
    val quantity: String,
    val imageResId: Int,
    val portfolioName: String
)
