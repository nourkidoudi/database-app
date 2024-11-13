package com.example.myapplication

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.data.FeedReaderContract
import com.example.myapplication.data.FeedReaderDbHelper
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: FeedReaderDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the database helper
        dbHelper = FeedReaderDbHelper(this)

        // Insert sample data
        val newRowId = insertData("Sample Title", "Sample Subtitle")
        println("Inserted row ID: $newRowId")

        // Query data with title "Sample Title"
        val cursor = queryData("Sample Title")
        cursor?.let {
            val itemIds = mutableListOf<Long>() // List to hold the item IDs

            // Iterate through the cursor and collect item IDs
            while (it.moveToNext()) {
                val itemId = it.getLong(it.getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)
            }

            // Close the cursor
            it.close()

            // Print the collected item IDs
            println("Item IDs: $itemIds")
        }

        // Example of delete operation
        deleteData("Sample Title")

        // Example of update operation
        updateData("Sample Title", "MyNewTitle")

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Function to insert data into the database
    private fun insertData(title: String, subtitle: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title)
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle)
        }
        return db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
    }

    // Function to query data from the database
    private fun queryData(title: String): Cursor? {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE  // Corrected this line
        )
        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selectionArgs = arrayOf(title)
        val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        return db.query(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    // Function to delete data from the database
    private fun deleteData(title: String) {
        val db = dbHelper.writableDatabase
        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
        val selectionArgs = arrayOf(title)

        val deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)
        println("Deleted rows: $deletedRows")
    }

    // Function to update data in the database
    private fun updateData(oldTitle: String, newTitle: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, newTitle)
        }

        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
        val selectionArgs = arrayOf(oldTitle)

        val count = db.update(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        println("Updated rows: $count")
    }

    // Close database resources in onDestroy
    override fun onDestroy() {
        dbHelper.close() // Close the database connection to avoid memory leaks
        super.onDestroy() // Call superclass onDestroy
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {
            Greeting("Android")
        }
    }
}
