package com.example.gradr_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter(private val context: Context, private val dataSource: List<CollectionItem>) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val collectionImage: ImageView = view.findViewById(R.id.collectionImage)
        val collectionName: TextView = view.findViewById(R.id.collectionName)
        val collectionValue: TextView = view.findViewById(R.id.collectionValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.collection_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSource[position]
        holder.collectionImage.setImageResource(item.imageResId)
        holder.collectionName.text = item.name
        holder.collectionValue.text = item.value

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PortfolioActivity::class.java).apply {
                putExtra("collectionName", item.name)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataSource.size
}
