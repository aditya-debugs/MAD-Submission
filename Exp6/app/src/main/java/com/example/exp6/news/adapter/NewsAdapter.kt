package com.example.exp6.news.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.exp6.R
import com.example.exp6.news.model.Article
import com.example.exp6.news.utils.BookmarkManager

class NewsAdapter(
    private val articles: List<Article>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.title)
        val desc: TextView = view.findViewById(R.id.desc)
        val bookmark: ImageView = view.findViewById(R.id.bookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        holder.title.text = article.title ?: "No title"
        holder.desc.text = article.description ?: ""

        Glide.with(context)
            .load(article.urlToImage)
            .centerCrop()
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val url = article.url
            if (!url.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }

        holder.bookmark.setOnClickListener {
            BookmarkManager.saveArticle(context, article)
            Toast.makeText(context, "Bookmarked!", Toast.LENGTH_SHORT).show()
        }
    }
}
