package com.example.sanctum

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

data class SearchResponse(
    val results: List<SearchResult>,
    val nextTokens: Map<String, String>?
)

object SearchEngine {
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    suspend fun performSearch(query: String): SearchResponse = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val doc = Jsoup.connect("https://html.duckduckgo.com/html/?q=$encodedQuery")
                .userAgent(USER_AGENT)
                .timeout(5000)
                .get()
            parseDocument(doc)
        } catch (e: Exception) {
            e.printStackTrace()
            SearchResponse(emptyList(), null)
        }
    }

    suspend fun loadNextPage(tokens: Map<String, String>): SearchResponse = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect("https://html.duckduckgo.com/html/")
                .data(tokens)
                .userAgent(USER_AGENT)
                .timeout(5000)
                .post()
            parseDocument(doc)
        } catch (e: Exception) {
            e.printStackTrace()
            SearchResponse(emptyList(), null)
        }
    }

    private fun parseDocument(doc: org.jsoup.nodes.Document): SearchResponse {
        val results = mutableListOf<SearchResult>()
        
        val resultElements = doc.select(".result")
        for (element in resultElements) {
            val titleElement = element.select(".result__title > a.result__a").first()
            val snippetElement = element.select(".result__snippet").first()
            
            if (titleElement != null) {
                val title = titleElement.text()
                var url = titleElement.attr("href")
                if (url.startsWith("//")) {
                    url = "https:$url"
                }
                val snippet = snippetElement?.text() ?: ""
                
                // Only add valid results
                if (title.isNotEmpty() && url.isNotEmpty()) {
                    results.add(SearchResult(title, url, snippet))
                }
            }
        }

        // Parse pagination tokens from the 'Next' form
        var nextTokens: MutableMap<String, String>? = null
        // Find the form that handles the "Next" button pagination
        val navForms = doc.select("form[action=/html/]")
        val nextForm = if (navForms.size > 1) navForms.last() else navForms.first()
        
        if (nextForm != null && results.isNotEmpty()) {
            val inputs = nextForm.select("input[type=hidden]")
            if (inputs.isNotEmpty()) {
                nextTokens = mutableMapOf()
                for (input in inputs) {
                    val name = input.attr("name")
                    val value = input.attr("value")
                    if (name.isNotEmpty()) {
                        nextTokens[name] = value
                    }
                }
            }
        }

        return SearchResponse(results, nextTokens)
    }
}
