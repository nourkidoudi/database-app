package com.example.myapplication.data

import android.provider.BaseColumns

object FeedReaderContract {
    // FeedEntry is the class for defining the table's schema
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }
}
