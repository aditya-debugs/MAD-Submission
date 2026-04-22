package com.example.exp6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exp6.news.adapter.NewsAdapter
import com.example.exp6.news.utils.BookmarkManager

class BookmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val textEmpty = findViewById<TextView>(R.id.textEmpty)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val bookmarks = BookmarkManager.getBookmarks(this)
        recyclerView.adapter = NewsAdapter(bookmarks, this)

        textEmpty.visibility = if (bookmarks.isEmpty()) View.VISIBLE else View.GONE
        buttonBack.setOnClickListener { finish() }
    }
}
