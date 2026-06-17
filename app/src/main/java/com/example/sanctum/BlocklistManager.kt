package com.example.sanctum

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.Locale

object BlocklistManager {
    private const val TAG = "BlocklistManager"
    private val blockedDomains = HashSet<String>()
    private val customBlockedDomains = HashSet<String>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        synchronized(this) {
            if (isInitialized) return
            try {
                var count = 0
                context.assets.open("blocklist.txt").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            val trimmed = line?.trim() ?: continue
                            // Skip comments and empty lines
                            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                                continue
                            }
                            
                            // StevenBlack format: "0.0.0.0 domain.com"
                            var firstSpace = trimmed.indexOf(' ')
                            if (firstSpace == -1) {
                                firstSpace = trimmed.indexOf('\t')
                            }
                            if (firstSpace != -1) {
                                val domain = trimmed.substring(firstSpace + 1).trim().lowercase(Locale.US)
                                if (domain.isNotEmpty() && domain != "localhost") {
                                    blockedDomains.add(domain)
                                    count++
                                }
                            }
                        }
                    }
                }
                
                // Load custom domains from SharedPreferences
                val prefs = context.getSharedPreferences("sanctum_prefs", Context.MODE_PRIVATE)
                val savedCustom = prefs.getStringSet("custom_blocked_domains", null)
                if (savedCustom != null) {
                    customBlockedDomains.addAll(savedCustom.map { it.lowercase(Locale.US).trim() })
                    Log.d(TAG, "Loaded ${customBlockedDomains.size} custom blocked domains.")
                }
                
                isInitialized = true
                Log.d(TAG, "Successfully loaded $count domains from StevenBlack hosts file.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load blocklist: ${e.message}", e)
            }
        }
    }

    fun isBlocked(urlStr: String): Boolean {
        if (!isInitialized) {
            Log.d(TAG, "Blocklist not initialized yet. Waiting for loading thread...")
            synchronized(this) {
                // Thread will block here until init() synchronized block releases this lock
            }
        }

        val host = try {
            val uri = URI(urlStr)
            var h = uri.host?.lowercase(Locale.US) ?: ""
            if (h.startsWith("www.")) {
                h = h.substring(4)
            }
            h
        } catch (e: Exception) {
            var h = urlStr.substringAfter("://").substringBefore("/")
            if (h.startsWith("www.")) {
                h = h.substring(4)
            }
            h.lowercase(Locale.US)
        }

        if (host.isEmpty()) return false

        var currentDomain = host
        while (currentDomain.contains(".")) {
            if (blockedDomains.contains(currentDomain) || customBlockedDomains.contains(currentDomain)) {
                Log.w(TAG, "Blocked domain matched: $currentDomain (URL: $urlStr)")
                return true
            }
            currentDomain = currentDomain.substringAfter(".", "")
        }
        return false
    }

    fun getCustomDomains(context: Context): List<String> {
        val prefs = context.getSharedPreferences("sanctum_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("custom_blocked_domains", emptySet())?.toList()?.sorted() ?: emptyList()
    }

    fun addCustomDomain(context: Context, domain: String): Boolean {
        val cleanDomain = domain.trim().lowercase(Locale.US)
        if (cleanDomain.isEmpty() || !cleanDomain.contains(".")) return false
        val prefs = context.getSharedPreferences("sanctum_prefs", Context.MODE_PRIVATE)
        val savedCustom = prefs.getStringSet("custom_blocked_domains", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (savedCustom.add(cleanDomain)) {
            prefs.edit().putStringSet("custom_blocked_domains", savedCustom).apply()
            synchronized(this) {
                customBlockedDomains.add(cleanDomain)
            }
            return true
        }
        return false
    }

    fun removeCustomDomain(context: Context, domain: String): Boolean {
        val cleanDomain = domain.trim().lowercase(Locale.US)
        val prefs = context.getSharedPreferences("sanctum_prefs", Context.MODE_PRIVATE)
        val savedCustom = prefs.getStringSet("custom_blocked_domains", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (savedCustom.remove(cleanDomain)) {
            prefs.edit().putStringSet("custom_blocked_domains", savedCustom).apply()
            synchronized(this) {
                customBlockedDomains.remove(cleanDomain)
            }
            return true
        }
        return false
    }
}
