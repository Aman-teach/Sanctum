package com.example.sanctum

import android.net.Uri
import android.util.Log
import java.util.Locale

object KeywordFilter {
    private const val TAG = "KeywordFilter"

    // Curated list of high-signal explicit keywords
    private val explicitKeywords = hashSetOf(
        "porn", "pornhub", "xvideos", "xnxx", "xhamster", 
        "redtube", "youporn", "stripchat", "chaturbate",
        "nsfw", "hentai", "erotic", "sex", "xxx", "adult", 
        "nude", "nudity", "playboy", "onlyfans", "webcam"
    )

    /**
     * Checks if a URL contains explicit keywords.
     * Ignore query parameters of search engines (since SafeSearch handles those).
     */
    fun containsExplicitKeyword(urlStr: String): Boolean {
        val uri = try {
            Uri.parse(urlStr)
        } catch (e: Exception) {
            return false
        }

        val host = uri.host?.lowercase(Locale.US) ?: ""
        val path = uri.path?.lowercase(Locale.US) ?: ""
        
        // 1. Check if the host name contains any explicit keyword
        for (keyword in explicitKeywords) {
            if (host.contains(keyword)) {
                Log.w(TAG, "Blocked host containing explicit keyword: $keyword (Host: $host)")
                return true
            }
        }

        // 2. Check if the path contains any explicit keywords (excluding search queries)
        val isSearchEngine = host.contains("google.") || host.contains("bing.com") || 
                             host.contains("duckduckgo.com") || host.contains("yahoo.com")
        
        if (!isSearchEngine) {
            for (keyword in explicitKeywords) {
                if (path.contains(keyword)) {
                    Log.w(TAG, "Blocked path containing explicit keyword: $keyword (Path: $path)")
                    return true
                }
            }
        }

        return false
    }
}
