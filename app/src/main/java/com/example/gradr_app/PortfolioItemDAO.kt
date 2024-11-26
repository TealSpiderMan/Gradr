package com.example.gradr_app

import android.content.Context
import android.database.Cursor
import android.util.Log

// DAO class for managing portfolio items in the database
class PortfolioItemDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // function to get the total value of items in a specific portfolio
    fun getTotalValueByPortfolioName(portfolioName: String): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_VALUE} FROM ${DatabaseHelper.TABLE_PORTFOLIO_ITEMS} WHERE ${DatabaseHelper.COLUMN_PORTFOLIO_NAME} = ?",
            arrayOf(portfolioName)
        )
        var totalValue = 0.0
        // iterate through the cursor to calculate the total value
        while (cursor.moveToNext()) {
            val valueText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALUE))
            totalValue += extractNumericValue(valueText)
        }
        cursor.close()
        return totalValue
    }

    // helper function to extract numeric value from a string
    private fun extractNumericValue(text: String): Double {
        val regex = Regex("[+-]?([0-9]*[.])?[0-9]+")
        val matchResult = regex.find(text)
        return matchResult?.value?.toDouble() ?: 0.0
    }

    // function to get all portfolio items from the database
    fun getAllItems(): List<PortfolioItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PORTFOLIO_ITEMS,
            null, null, null, null, null, null
        )
        // convert the result to a list of PortfolioItem objects
        return cursorToList(cursor)
    }

    // function to get items from a specific portfolio by name
    fun getItemsByPortfolioName(portfolioName: String): List<PortfolioItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PORTFOLIO_ITEMS,
            null, "${DatabaseHelper.COLUMN_PORTFOLIO_NAME} = ?", arrayOf(portfolioName),
            null, null, null
        )
        Log.d("PortfolioItemDAO", "Querying items for portfolio: $portfolioName")
        return cursorToList(cursor)
    }

    // function to delete a portfolio item by its ID
    fun deleteItem(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_PORTFOLIO_ITEMS, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    // helper function to convert a cursor to a list of PortfolioItem objects
    private fun cursorToList(cursor: Cursor): List<PortfolioItem> {
        val items = mutableListOf<PortfolioItem>()
        while (cursor.moveToNext()) {
            val item = PortfolioItem(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                details = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DETAILS)),
                value = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALUE)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                quantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY)),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URI)),
                portfolioName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PORTFOLIO_NAME))
            )
            items.add(item)
        }
        cursor.close()
        return items
    }
}
