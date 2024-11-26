package com.example.gradr_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // database creation
    override fun onCreate(db: SQLiteDatabase) {
        // create portfolio items table
        val createPortfolioItemsTable = """
            CREATE TABLE $TABLE_PORTFOLIO_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_DETAILS TEXT,
                $COLUMN_VALUE TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_QUANTITY TEXT,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_PORTFOLIO_NAME TEXT
            )
        """
        db.execSQL(createPortfolioItemsTable)

        // create categories table
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT,
                $COLUMN_CATEGORY_IMAGE_URI TEXT
            )
        """
        db.execSQL(createCategoriesTable)

        // insert initial data into the tables
        insertInitialData(db)
    }

    // called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PORTFOLIO_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }
    // function to insert initial data into the tables
    private fun insertInitialData(db: SQLiteDatabase) {
        val sampleItems = listOf(
            PortfolioItem(name = "Mew ex", details = "Paldean Fates • Special Illustration Rare • 232/091", value = "MYR 90.66", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageUri = "android.resource://${context.packageName}/${R.drawable.mew_ex_sir}", portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Mew EX (Full Art)", details = "Legendary Treasures: Radiant Collection • Ultra Rare • RC24", value = "MYR 48.26", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageUri = "android.resource://${context.packageName}/${R.drawable.mew_ex_rc}", portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Mew ex - 053", details = "Scarlet & Violet Promo • 053", value = "MYR 5.46", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageUri = "android.resource://${context.packageName}/${R.drawable.mew_151}", portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Coin 1", details = "Details about Coin 1", value = "MYR 100.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 10", imageUri = "android.resource://${context.packageName}/${R.drawable.coin_1968}", portfolioName = "Coins"),
            PortfolioItem(name = "Coin 2", details = "Details about Coin 2", value = "MYR 200.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 5", imageUri = "android.resource://${context.packageName}/${R.drawable.coin_1971}", portfolioName = "Coins"),
            PortfolioItem(name = "Coin 3", details = "Details about Coin 3", value = "MYR 300.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 2", imageUri = "android.resource://${context.packageName}/${R.drawable.coin_1981}", portfolioName = "Coins")
        )

        // add sample portfolio items into the database
        for (item in sampleItems) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, item.name)
                put(COLUMN_DETAILS, item.details)
                put(COLUMN_VALUE, item.value)
                put(COLUMN_DATE, item.date)
                put(COLUMN_TIME, item.time)
                put(COLUMN_QUANTITY, item.quantity)
                put(COLUMN_IMAGE_URI, item.imageUri)
                put(COLUMN_PORTFOLIO_NAME, item.portfolioName)
            }
            db.insert(TABLE_PORTFOLIO_ITEMS, null, values)
        }
    }

    companion object {
        const val DATABASE_NAME = "portfolio.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PORTFOLIO_ITEMS = "portfolio_items"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DETAILS = "details"
        const val COLUMN_VALUE = "value"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_PORTFOLIO_NAME = "portfolio_name"

        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_CATEGORY_NAME = "category_name"
        const val COLUMN_CATEGORY_IMAGE_URI = "category_image_uri"
    }
}
