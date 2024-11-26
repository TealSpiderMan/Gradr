package com.example.gradr_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter(
    private val context: Context,
    private val collections: List<CollectionItem>
) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    // create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    // bind data to a ViewHolder
    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collections[position]
        holder.collectionName.text = collection.name
        holder.collectionValue.text = collection.value
        holder.collectionImage.setImageURI(collection.imageUri)
    }

    // returns the total number of items in the data source to display in main activity
    override fun getItemCount(): Int {
        return collections.size
    }

    // hold and recycle views as they are scrolled off screen
    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val collectionImage: ImageView = itemView.findViewById(R.id.collectionImage)
        val collectionName: TextView = itemView.findViewById(R.id.collectionName)
        val collectionValue: TextView = itemView.findViewById(R.id.collectionValue)
    }
}

