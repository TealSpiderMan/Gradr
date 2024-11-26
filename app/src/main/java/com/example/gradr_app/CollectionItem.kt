package com.example.gradr_app

import android.net.Uri

// data class representing a collection item
data class CollectionItem(
    val name: String,   // name of the collection item
    val value: String,  // value of the collection item, currency is set as string here (only numbers are extracted for calculations)
    val imageUri: Uri   // URI for image representing items in the collection
)
