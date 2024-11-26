package com.example.gradr_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CategoryDAO(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT,
                $COLUMN_CATEGORY_IMAGE_URI TEXT
            )
        """
        db.execSQL(createCategoriesTable)
    }

    // called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    // insert category into the database
    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
            put(COLUMN_CATEGORY_IMAGE_URI, category.imageUri)
        }
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    // retrieve all categories from the database
    fun getAllCategories(): List<Category> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            arrayOf(COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_IMAGE_URI),
            null, null, null, null, null
        )

        val categories = mutableListOf<Category>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
                val imageUri = getString(getColumnIndexOrThrow(COLUMN_CATEGORY_IMAGE_URI))
                categories.add(Category(id, name, imageUri))
            }
        }
        cursor.close()
        return categories
    }

    // update an existing category in the database
    fun updateCategory(category: Category): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
            put(COLUMN_CATEGORY_IMAGE_URI, category.imageUri)
        }
        return db.update(TABLE_CATEGORIES, values, "$COLUMN_CATEGORY_ID = ?", arrayOf(category.id.toString()))
    }

    // delete a category from the database by its ID
    fun deleteCategory(categoryId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CATEGORIES, "$COLUMN_CATEGORY_ID = ?", arrayOf(categoryId.toString()))
    }

    // function to check if a category with a given name already exists in the database
    fun isCategoryExists(name: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            arrayOf(COLUMN_CATEGORY_ID),
            "$COLUMN_CATEGORY_NAME = ?",
            arrayOf(name),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    companion object {
        const val DATABASE_NAME = "categories.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_CATEGORY_NAME = "category_name"
        const val COLUMN_CATEGORY_IMAGE_URI = "category_image_uri"
    }
}
