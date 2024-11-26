package com.example.gradr_app

// category variables
data class Category(
    val id: Long = 0,   // id for category, defaults to 0 if not provided
    val name: String,   // name of category
    val imageUri: String?   //  URI for image representing the category
)
