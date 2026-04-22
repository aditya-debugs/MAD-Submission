package com.example.exp6

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exp6.databinding.ActivityMainBinding
import com.example.exp6.news.adapter.NewsAdapter
import com.example.exp6.news.model.NewsResponse
import com.example.exp6.news.network.ApiService
import com.example.exp6.news.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // NewsAPI key provided by you
    private val apiKey = "b46e07e5185a48ce9e4a24615895d70f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchBtn.setOnClickListener {
            val query = binding.searchInput.text.toString().trim()
            searchNews(query)
        }

        binding.bookmarkBtn.setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))
        }

        binding.buttonRefresh.setOnClickListener {
            val query = binding.searchInput.text.toString().trim()
            searchNews(query.ifBlank { "india" })
        }

        // Default load
        binding.searchInput.setText("india")
        searchNews("india")
    }

    private fun searchNews(query: String) {
        if (query.isBlank()) {
            Toast.makeText(this, "Enter a search term", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.textError.visibility = View.GONE

        val api = RetrofitClient.instance.create(ApiService::class.java)
        api.searchNews(query = query, apiKey = apiKey).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val articles = response.body()!!.articles
                    if (articles.isEmpty()) {
                        showError("No results found for: $query")
                        return
                    }
                    binding.recyclerView.adapter = NewsAdapter(articles, this@MainActivity)
                    binding.recyclerView.visibility = View.VISIBLE
                    return
                }

                // Parse NewsAPI error JSON: {"status":"error","code":"...","message":"..."}
                val errBody = try {
                    response.errorBody()?.string()
                } catch (_: Exception) {
                    null
                }

                val apiMsg = errBody?.let {
                    try {
                        JSONObject(it).optString("message").takeIf { msg -> msg.isNotBlank() }
                    } catch (_: Exception) {
                        null
                    }
                }

                val msg = buildString {
                    append("Failed to load news (HTTP ${response.code()})")
                    if (!apiMsg.isNullOrBlank()) {
                        append("\n")
                        append(apiMsg)
                    } else if (!errBody.isNullOrBlank()) {
                        append("\n\n")
                        append(errBody)
                    }
                }

                showError(msg)
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showError(msg: String) {
        binding.recyclerView.visibility = View.GONE
        binding.textError.visibility = View.VISIBLE
        binding.textError.text = msg
        Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show()
    }
}
