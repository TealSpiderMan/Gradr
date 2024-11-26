package com.example.gradr_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// adapter class for managing portfolio items in RecyclerView
class PortfolioAdapter(private val context: Context, private val dataSource: List<PortfolioItem>) :
    RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {

    // hold and recycle views when they are scrolled off screen
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemDetails: TextView = view.findViewById(R.id.itemDetails)
        val itemValue: TextView = view.findViewById(R.id.itemValue)
        val itemDate: TextView = view.findViewById(R.id.itemDate)
        val itemTime: TextView = view.findViewById(R.id.itemTime)
        val itemQuantity: TextView = view.findViewById(R.id.itemQuantity)
    }

    // inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.portfolio_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the item views in each ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSource[position]
        item.imageUri?.let {
            holder.itemImage.setImageURI(Uri.parse(it))
        }
        holder.itemName.text = item.name
        holder.itemDetails.text = item.details
        holder.itemValue.text = item.value
        holder.itemDate.text = item.date
        holder.itemTime.text = item.time
        holder.itemQuantity.text = item.quantity

        // Sets an clicker on each item view to open the AppraiseItemActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, AppraiseItemActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("name", item.name)
                putExtra("details", item.details)
                putExtra("value", item.value)
                putExtra("date", item.date)
                putExtra("time", item.time)
                putExtra("quantity", item.quantity)
                putExtra("imageUri", item.imageUri)
                putExtra("collectionName", item.portfolioName) // Pass collection name
            }
            context.startActivity(intent)
        }
    }

    // returns the total number of items in the data source
    override fun getItemCount(): Int {
        return dataSource.size
    }
}
