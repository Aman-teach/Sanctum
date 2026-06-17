package com.example.sanctum

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

enum class DnsCheckResult {
    SAFE,
    BLOCKED,
    ERROR
}

object DnsFilter {
    private const val TAG = "DnsFilter"
    
    // 10 minutes cache TTL (Time to Live)
    private const val CACHE_TTL_MS = 10 * 60 * 1000 
    
    private data class CacheEntry(val result: DnsCheckResult, val timestamp: Long)
    
    // Thread-safe in-memory cache
    private val dnsCache = ConcurrentHashMap<String, CacheEntry>()

    fun checkDomain(urlStr: String): DnsCheckResult {
        val host = try {
            val uri = URI(urlStr)
            uri.host?.lowercase(Locale.US) ?: ""
        } catch (e: Exception) {
            return DnsCheckResult.SAFE
        }

        if (host.isEmpty()) return DnsCheckResult.SAFE

        if (host.contains("cloudflare-dns.com") || host.contains("cloudflare.com")) {
            return DnsCheckResult.SAFE
        }

        // Bypass DNS checks for trusted search engines and learning tiles to prevent startup/redirection delays
        val isTrusted = host == "google.com" || host.endsWith(".google.com") ||
                        host == "google.co.in" || host.endsWith(".google.co.in") ||
                        host == "bing.com" || host.endsWith(".bing.com") ||
                        host == "duckduckgo.com" || host.endsWith(".duckduckgo.com") ||
                        host == "youtube.com" || host.endsWith(".youtube.com") ||
                        host == "youtu.be" || host.endsWith(".youtu.be") ||
                        host == "wikipedia.org" || host.endsWith(".wikipedia.org") ||
                        host == "duolingo.com" || host.endsWith(".duolingo.com") ||
                        host == "khanacademy.org" || host.endsWith(".khanacademy.org") ||
                        host == "ted.com" || host.endsWith(".ted.com")
        if (isTrusted) {
            return DnsCheckResult.SAFE
        }

        // 1. Check local cache first
        val cached = dnsCache[host]
        if (cached != null) {
            val age = System.currentTimeMillis() - cached.timestamp
            if (age < CACHE_TTL_MS) {
                Log.d(TAG, "DNS Cache Hit: $host is ${cached.result} (Age: ${age / 1000}s)")
                return cached.result
            } else {
                dnsCache.remove(host) // Evict expired entry
            }
        }

        // 2. Perform network check
        try {
            val url = URL("https://family.cloudflare-dns.com/dns-query?name=$host&type=A")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/dns-json")
            connection.connectTimeout = 2000
            connection.readTimeout = 2000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseBody = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                val json = JSONObject(responseBody)
                
                val status = json.optInt("Status", 0)
                val finalResult = if (status == 3) {
                    Log.d(TAG, "DNS Filter flagged NXDOMAIN for host: $host")
                    DnsCheckResult.BLOCKED
                } else {
                    var isBlockedIp = false
                    val answerArray = json.optJSONArray("Answer")
                    if (answerArray != null) {
                        for (i in 0 until answerArray.length()) {
                            val answer = answerArray.optJSONObject(i) ?: continue
                            val data = answer.optString("data", "")
                            if (data == "0.0.0.0") {
                                isBlockedIp = true
                                break
                            }
                        }
                    }
                    if (isBlockedIp) {
                        Log.d(TAG, "DNS Filter flagged 0.0.0.0 IP address for host: $host")
                        DnsCheckResult.BLOCKED
                    } else {
                        DnsCheckResult.SAFE
                    }
                }

                // Cache successful checks (both SAFE and BLOCKED)
                dnsCache[host] = CacheEntry(finalResult, System.currentTimeMillis())
                return finalResult
            } else {
                Log.e(TAG, "DNS request returned error code: $responseCode")
                return DnsCheckResult.ERROR
            }
        } catch (e: Exception) {
            Log.e(TAG, "DNS-over-HTTPS query failed for $host: ${e.message}")
            return DnsCheckResult.ERROR
        }
    }
}
