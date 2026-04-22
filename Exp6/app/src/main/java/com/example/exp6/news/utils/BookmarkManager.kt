package com.example.exp6.news.utils

import android.content.Context
import com.example.exp6.news.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BookmarkManager {

    private const val PREF_NAME = "bookmarks"
    private const val KEY = "articles"

    fun saveArticle(context: Context, article: Article) {
        val list = getBookmarks(context).toMutableList()

        // Avoid duplicates by URL (best unique field we have)
        val url = article.url
        if (!url.isNullOrBlank() && list.any { it.url == url }) return

        list.add(article)

        val json = Gson().toJson(list)
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putString(KEY, json).apply()
    }

    fun getBookmarks(context: Context): List<Article> {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = pref.getString(KEY, null) ?: return emptyList()

        val type = object : TypeToken<List<Article>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }
}

