package com.example.sanctum

import android.net.Uri
import android.util.Log
import java.util.Locale

object SafeSearchEnforcer {
    private const val TAG = "SafeSearchEnforcer"

    fun enforce(urlStr: String): String? {
        val uri = try {
            Uri.parse(urlStr)
        } catch (e: Exception) {
            return null
        }

        val host = uri.host?.lowercase(Locale.US) ?: return null

        return when {
            host.contains("google.") && uri.path?.contains("/search") == true -> {
                if (uri.getQueryParameter("safe") != "active") {
                    Log.d(TAG, "Enforcing Google SafeSearch on $urlStr")
                    uri.buildUpon()
                        .appendQueryParameter("safe", "active")
                        .build()
                        .toString()
                } else null
            }
            host.contains("bing.com") && uri.path?.contains("/search") == true -> {
                if (uri.getQueryParameter("adlt") != "strict") {
                    Log.d(TAG, "Enforcing Bing SafeSearch on $urlStr")
                    uri.buildUpon()
                        .appendQueryParameter("adlt", "strict")
                        .build()
                        .toString()
                } else null
            }
            host.contains("duckduckgo.com") -> {
                if (uri.getQueryParameter("kp") != "1") {
                    Log.d(TAG, "Enforcing DuckDuckGo SafeSearch on $urlStr")
                    uri.buildUpon()
                        .appendQueryParameter("kp", "1")
                        .build()
                        .toString()
                } else null
            }
            host.contains("youtube.com") -> {
                if (uri.getQueryParameter("safe") != "active") {
                    Log.d(TAG, "Enforcing YouTube Restricted Mode on $urlStr")
                    uri.buildUpon()
                        .appendQueryParameter("safe", "active")
                        .build()
                        .toString()
                } else null
            }
            else -> null
        }
    }
}
