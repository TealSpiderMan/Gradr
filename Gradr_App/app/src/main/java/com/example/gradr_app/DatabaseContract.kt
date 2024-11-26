package com.example.gradr_app

import android.provider.BaseColumns

object DatabaseContract {
    object PortfolioEntry : BaseColumns {
        const val TABLE_NAME = "portfolio_items"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DETAILS = "details"
        const val COLUMN_NAME_VALUE = "value"
        const val COLUMN_NAME_CHANGE = "change"
        const val COLUMN_NAME_QUANTITY = "quantity"
        const val COLUMN_NAME_IMAGE_RES_ID = "image_res_id"
        const val COLUMN_NAME_PORTFOLIO_NAME = "portfolio_name"
    }
}
