package com.example.gradr_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PortfolioItemDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertItem(item: PortfolioItem): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, item.name)
            put(DatabaseHelper.COLUMN_DETAILS, item.details)
            put(DatabaseHelper.COLUMN_VALUE, item.value)
            put(DatabaseHelper.COLUMN_DATE, item.date)
            put(DatabaseHelper.COLUMN_TIME, item.time)
            put(DatabaseHelper.COLUMN_QUANTITY, item.quantity)
            put(DatabaseHelper.COLUMN_IMAGE_RES_ID, item.imageResId)
            put(DatabaseHelper.COLUMN_PORTFOLIO_NAME, item.portfolioName)
        }
        return db.insert(DatabaseHelper.TABLE_PORTFOLIO_ITEMS, null, values)
    }

    fun getAllItems(): List<PortfolioItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PORTFOLIO_ITEMS,
            null, null, null, null, null, null
        )
        return cursorToList(cursor)
    }

    fun getItemsByPortfolioName(portfolioName: String): List<PortfolioItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PORTFOLIO_ITEMS,
            null, "${DatabaseHelper.COLUMN_PORTFOLIO_NAME} = ?", arrayOf(portfolioName),
            null, null, null
        )
        return cursorToList(cursor)
    }

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
                imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_RES_ID)),
                portfolioName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PORTFOLIO_NAME))
            )
            items.add(item)
        }
        cursor.close()
        return items
    }
}
