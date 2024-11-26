package com.example.gradr_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_PORTFOLIO_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_DETAILS TEXT,
                $COLUMN_VALUE TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_QUANTITY TEXT,
                $COLUMN_IMAGE_RES_ID INTEGER,
                $COLUMN_PORTFOLIO_NAME TEXT
            )
        """
        db.execSQL(createTableStatement)
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PORTFOLIO_ITEMS")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val sampleItems = listOf(
            PortfolioItem(name = "Mew ex", details = "Paldean Fates • Special Illustration Rare • 232/091", value = "MYR 90.66", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageResId = R.drawable.mew_ex_sir, portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Mew EX (Full Art)", details = "Legendary Treasures: Radiant Collection • Ultra Rare • RC24", value = "MYR 48.26", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageResId = R.drawable.mew_ex_rc, portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Mew ex - 053", details = "Scarlet & Violet Promo • 053", value = "MYR 5.46", date = "2024-01-01", time = "12:00", quantity = "Qty: 1", imageResId = R.drawable.mew_151, portfolioName = "Pokemon Cards"),
            PortfolioItem(name = "Coin 1", details = "Details about Coin 1", value = "MYR 100.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 10", imageResId = R.drawable.coin_1968, portfolioName = "Coins"),
            PortfolioItem(name = "Coin 2", details = "Details about Coin 2", value = "MYR 200.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 5", imageResId = R.drawable.coin_1971, portfolioName = "Coins"),
            PortfolioItem(name = "Coin 3", details = "Details about Coin 3", value = "MYR 300.00", date = "2024-01-01", time = "12:00", quantity = "Qty: 2", imageResId = R.drawable.coin_1981, portfolioName = "Coins")
        )

        for (item in sampleItems) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, item.name)
                put(COLUMN_DETAILS, item.details)
                put(COLUMN_VALUE, item.value)
                put(COLUMN_DATE, item.date)
                put(COLUMN_TIME, item.time)
                put(COLUMN_QUANTITY, item.quantity)
                put(COLUMN_IMAGE_RES_ID, item.imageResId)
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
        const val COLUMN_IMAGE_RES_ID = "image_res_id"
        const val COLUMN_PORTFOLIO_NAME = "portfolio_name"
    }
}

