package com.example.sanctum

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.Image
import com.example.sanctum.R
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder

// Premium Inter Font Family loaded from resources
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val defaultTypography = Typography()
val AppTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = Inter),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = Inter),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = Inter),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = Inter),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = Inter),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = Inter),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = Inter),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = Inter),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = Inter),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = Inter),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = Inter),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = Inter),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = Inter),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = Inter)
)

// Premium Slate-Fintech Color Palette
val EditorialPaper = Color(0xFFF8FAFC)      // Slate-50 background
val EditorialSurface = Color(0xFFF1F5F9)    // Slate-100 surface
val EditorialInk = Color(0xFF0F172A)        // Slate-900 primary text/charcoal
val EditorialMutedInk = Color(0xFF64748B)   // Slate-500 secondary text
val EditorialForest = Color(0xFF0F172A)     // Primary slate color
val EditorialBorder = Color(0xFFE2E8F0)     // Slate-200 border/lines
val SecondaryAccent = Color(0xFFBAE6FD)     // Sky-200 light blue

class MainActivity : ComponentActivity() {

    var filePathCallback: ValueCallback<Array<Uri>>? = null

    val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val results = if (result.resultCode == RESULT_OK && data != null) {
            val dataString = data.dataString
            val clipData = data.clipData
            if (clipData != null) {
                val count = clipData.itemCount
                Array(count) { i -> clipData.getItemAt(i).uri }
            } else if (dataString != null) {
                arrayOf(Uri.parse(dataString))
            } else null
        } else null
        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize blocklist
        CoroutineScope(Dispatchers.IO).launch {
            BlocklistManager.init(applicationContext)
        }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    background = EditorialPaper,
                    surface = EditorialSurface,
                    primary = EditorialForest,
                    onBackground = EditorialInk,
                    onSurface = EditorialInk
                ),
                typography = AppTypography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrowserScreen(activity = this@MainActivity)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(activity: MainActivity) {
    val context = LocalContext.current
    val tabManager = remember { 
        TabManager().apply { 
            loadState(context)
            if (tabs.isEmpty()) addTab()
        } 
    }
    val activeTab = tabManager.activeTab ?: return
    
    var currentUrl by activeTab.url
    var inputText by remember { mutableStateOf("") }
    
    LaunchedEffect(tabManager.tabs.size, activeTab.url.value, tabManager.activeTabIndex.value) {
        tabManager.saveState(context)
    }
    var isLoading by activeTab.isLoading
    var progress by activeTab.progress
    var canGoBack by activeTab.canGoBack
    var canGoForward by activeTab.canGoForward
    var webViewRef = activeTab.webView
    
    // Sync inputText with active tab's URL
    LaunchedEffect(currentUrl) {
        if (!currentUrl.startsWith("file:///android_asset/")) {
            if (currentUrl.contains("duckduckgo.com")) {
                try {
                    val uri = android.net.Uri.parse(currentUrl)
                    val q = uri.getQueryParameter("q")
                    if (!q.isNullOrEmpty()) {
                        inputText = q
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {}
            }
            inputText = currentUrl
        }
    }
    val focusManager = LocalFocusManager.current

    // Screen State
    var activeScreen by remember { mutableStateOf(ActiveScreen.BROWSER) }

    // Shield Features State
    var adBlocking by remember { mutableStateOf(true) }
    var antiFingerprint by remember { mutableStateOf(true) }
    var httpsOnly by remember { mutableStateOf(false) }
    var shieldMode by remember { mutableStateOf(ShieldMode.STANDARD) }
    var familyMode by remember { mutableStateOf(true) }
    
    // Native Search State
    var nativeSearchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isNativeSearchLoading by remember { mutableStateOf(false) }
    var currentNativeQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Instantiated once per BrowserScreen lifecycle and reused to prevent startup blank screen flash
    fun createWebViewForTab(): WebView {
        return WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            
            // Set background transparent to prevent white flash
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val urlStr = request?.url?.toString() ?: return false
                    val enforced = SafeSearchEnforcer.enforce(urlStr)
                    if (enforced != null) {
                        view?.loadUrl(enforced)
                        return true
                    }
                    return false
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val urlStr = request?.url?.toString() ?: return null
                    if (request.isForMainFrame) {
                        val isLocalBlocked = BlocklistManager.isBlocked(urlStr)
                        val isKeywordBlocked = KeywordFilter.containsExplicitKeyword(urlStr)
                        
                        if (isLocalBlocked || isKeywordBlocked) {
                            return getBlockedResponse(context)
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(
                    view: WebView?,
                    urlStr: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, urlStr, favicon)
                    isLoading = true
                    
                    val url = urlStr ?: return
                    if (url.startsWith("file:///android_asset/")) return
                    
                    // Direct SafeSearch check on start load as fallback
                    val enforced = SafeSearchEnforce(url)
                    if (enforced != null) {
                        view?.stopLoading()
                        view?.loadUrl(enforced)
                        return
                    }
                    
                    // Asynchronous DoH safety verification
                    CoroutineScope(Dispatchers.IO).launch {
                        when (DnsFilter.checkDomain(url)) {
                            DnsCheckResult.BLOCKED -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    view?.stopLoading()
                                    view?.loadUrl("file:///android_asset/blocked.html")
                                }
                            }
                            DnsCheckResult.ERROR -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    view?.stopLoading()
                                    view?.loadUrl("file:///android_asset/blocked.html")
                                }
                            }
                            DnsCheckResult.SAFE -> {}
                        }
                    }
                    
                    urlStr.let {
                        if (!it.startsWith("file:///android_asset/")) {
                            inputText = it
                        }
                    }
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }

                private fun SafeSearchEnforce(url: String): String? {
                    return SafeSearchEnforcer.enforce(url)
                }

                override fun onPageFinished(view: WebView?, urlStr: String?) {

                    // Inject Custom CSS to hide DuckDuckGo branding and DuckAI
                    if (urlStr?.contains("duckduckgo.com") == true) {
                        val js = "(function() {" +
                                "var style = document.createElement('style');" +
                                "style.innerHTML = '" +
                                ".header__logo-wrap, .logo-wrap--home, .header__logo, a[href=\"/\"] { display: none !important; visibility: hidden !important; opacity: 0 !important; width: 0 !important; }" +
                                ".duckchat-button, .js-duckchat-chat-btn, .duckchat-header, .js-duckchat-prompts, .duckai-btn, a[href*=\"/chat\"], .header__button--menu, .js-header-menu-btn, #duckbar_chat, .header__button--chat { display: none !important; visibility: hidden !important; opacity: 0 !important; width: 0 !important; pointer-events: none !important; }" +
                                ".badge-link, .js-badge-main-msg, .js-install-prompt, .install-prompt, .app-smart-banner { display: none !important; }" +
                                "'; document.head.appendChild(style);" +
                                "})();"
                        view?.evaluateJavascript(js, null)
                    }
                    super.onPageFinished(view, urlStr)
                    isLoading = false
                    canGoBack = view?.canGoBack() == true
                    canGoForward = view?.canGoForward() == true
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progress = newProgress / 100f
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    activity.filePathCallback?.onReceiveValue(null)
                    activity.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    try {
                        activity.fileChooserLauncher.launch(intent)
                    } catch (e: Exception) {
                        activity.filePathCallback?.onReceiveValue(null)
                        activity.filePathCallback = null
                        return false
                    }
                    return true
                }
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                try {
                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                        setMimeType(mimetype)
                        val cookies = android.webkit.CookieManager.getInstance().getCookie(url)
                        addRequestHeader("cookie", cookies)
                        addRequestHeader("User-Agent", userAgent)
                        setDescription("Downloading file...")
                        setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(url, contentDisposition, mimetype)
                        )
                    }
                    val dm = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (activeTab.webView == null) {
        activeTab.webView = createWebViewForTab()
    }
    webViewRef = activeTab.webView

    var isUrlDetailsOpen by remember { mutableStateOf(false) }

    val showWebView = activeScreen == ActiveScreen.BROWSER && currentUrl != "sanctum://home"

    // Handle system back navigation
    BackHandler(enabled = activeScreen != ActiveScreen.BROWSER || isUrlDetailsOpen || (canGoBack && showWebView)) {
        when {
            isUrlDetailsOpen -> isUrlDetailsOpen = false
            activeScreen != ActiveScreen.BROWSER -> activeScreen = ActiveScreen.BROWSER
            canGoBack && showWebView -> webViewRef?.goBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(EditorialPaper)
        ) {
            // Top Navigation Bar (Only active when browsing a webpage)
            if (showWebView) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EditorialPaper)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Elegant Shield Logo / Home trigger
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EditorialSurface)
                            .clickable {
                                currentUrl = "sanctum://home"
                                inputText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Home",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Address Bar
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .border(1.dp, EditorialBorder, RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(4.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EditorialSurface,
                            unfocusedContainerColor = EditorialSurface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = EditorialInk,
                            unfocusedTextColor = EditorialInk
                        ),
                        textStyle = TextStyle(
                            fontFamily = Inter,
                            fontSize = 13.sp,
                            color = EditorialInk
                        ),
                        placeholder = {
                            Text(
                                text = "Search or type URL",
                                color = EditorialMutedInk,
                                fontFamily = Inter,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                                tint = EditorialMutedInk,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { inputText = "" }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_clear),
                                        contentDescription = "Clear",
                                        tint = EditorialMutedInk,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                focusManager.clearFocus()
                                var query = inputText.trim()
                                if (query.isNotEmpty()) {
                                    if (!query.contains(".") || query.contains(" ")) {
                                        currentNativeQuery = query
                                        activeScreen = ActiveScreen.NATIVE_SEARCH
                                        isNativeSearchLoading = true
                                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                            nativeSearchResults = SearchEngine.performSearch(query)
                                            isNativeSearchLoading = false
                                        }
                                        return@KeyboardActions
                                    } else if (!query.startsWith("http://") && !query.startsWith("https://")) {
                                        query = "https://$query"
                                    }
                                    isLoading = true
                                    progress = 0.1f
                                    currentUrl = query
                                }
                            }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Shield status indicator
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EditorialSurface)
                            .clickable { isUrlDetailsOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = "Shield",
                            tint = EditorialForest,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EditorialSurface)
                            .clickable { webViewRef?.reload() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Refresh",
                            tint = EditorialInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Understated loading line
                AnimatedVisibility(visible = isLoading) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = EditorialForest,
                        trackColor = EditorialPaper
                    )
                }

                // Separator line
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(EditorialBorder)
                )
            }

            // Content Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(EditorialPaper)
            ) {
                // WebView integration (always active to maintain state)
                AndroidView(
                    factory = { webViewRef!! },
                    update = { webViewInstance ->
                        if (currentUrl == "sanctum://home") {
                            if (webViewInstance.url != "about:blank") {
                                webViewInstance.loadUrl("about:blank")
                                webViewInstance.clearHistory()
                            }
                        } else if (webViewInstance.url != currentUrl) {
                            webViewInstance.loadUrl(currentUrl)
                        }
                    },
                    modifier = if (showWebView) Modifier.fillMaxSize() else Modifier.size(0.dp)
                )

                Crossfade(
                    targetState = activeScreen,
                    animationSpec = tween(durationMillis = 220),
                    modifier = Modifier.fillMaxSize(),
                    label = "screenSwitch"
                ) { screen ->
                    when (screen) {
                        ActiveScreen.BROWSER -> {
                            if (currentUrl == "sanctum://home") {
                                HomeScreen(
                                    onSearchSubmit = { query ->
                                        var formattedQuery = query.trim()
                                        if (formattedQuery.isNotEmpty()) {
                                            if (!formattedQuery.contains(".") || formattedQuery.contains(" ")) {
                                                formattedQuery = "https://duckduckgo.com/?q=" + URLEncoder.encode(formattedQuery, "UTF-8") + "&safe=active"
                                            } else if (!formattedQuery.startsWith("http://") && !formattedQuery.startsWith("https://")) {
                                                formattedQuery = "https://$formattedQuery"
                                            }
                                            isLoading = true
                                            progress = 0.1f
                                            currentUrl = formattedQuery
                                            inputText = formattedQuery
                                        }
                                    },
                                    onProfileClick = { activeScreen = ActiveScreen.SETTINGS },
                                    onShieldClick = { activeScreen = ActiveScreen.SAFETY_SHIELD },
                                    onSettingsClick = { activeScreen = ActiveScreen.SETTINGS }
                                )
                            }
                        }
                        ActiveScreen.SAFETY_SHIELD -> {
                            SafetyShieldScreen(
                                context = context,
                                adBlocking = adBlocking,
                                onAdBlockingChange = { adBlocking = it },
                                antiFingerprint = antiFingerprint,
                                onAntiFingerprintChange = { antiFingerprint = it },
                                httpsOnly = httpsOnly,
                                onHttpsOnlyChange = { httpsOnly = it },
                                shieldMode = shieldMode,
                                onShieldModeChange = { shieldMode = it }
                            )
                        }
                        ActiveScreen.SETTINGS -> {
                            SettingsScreen(
                                context = context,
                                familyMode = familyMode,
                                onFamilyModeChange = { familyMode = it },
                                onBack = { activeScreen = ActiveScreen.BROWSER }
                            )
                        }
                        ActiveScreen.NATIVE_SEARCH -> {
                            NativeSearchScreen(
                                query = currentNativeQuery,
                                results = nativeSearchResults,
                                isLoading = isNativeSearchLoading,
                                onResultClick = { url ->
                                    activeTab.url.value = url
                                    activeScreen = ActiveScreen.BROWSER
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar animated states
            val backTint by animateColorAsState(
                targetValue = if (showWebView && canGoBack) EditorialInk else EditorialMutedInk.copy(alpha = 0.3f),
                animationSpec = tween(durationMillis = 150),
                label = "backTint"
            )
            val forwardTint by animateColorAsState(
                targetValue = if (showWebView && canGoForward) EditorialInk else EditorialMutedInk.copy(alpha = 0.3f),
                animationSpec = tween(durationMillis = 150),
                label = "forwardTint"
            )
            val searchTint by animateColorAsState(
                targetValue = if (activeScreen == ActiveScreen.BROWSER) EditorialForest else EditorialMutedInk.copy(alpha = 0.5f),
                animationSpec = tween(durationMillis = 150),
                label = "searchTint"
            )
            val shieldTint by animateColorAsState(
                targetValue = if (activeScreen == ActiveScreen.SAFETY_SHIELD) EditorialForest else EditorialMutedInk.copy(alpha = 0.5f),
                animationSpec = tween(durationMillis = 150),
                label = "shieldTint"
            )
            val settingsTint by animateColorAsState(
                targetValue = if (activeScreen == ActiveScreen.SETTINGS) EditorialForest else EditorialMutedInk.copy(alpha = 0.5f),
                animationSpec = tween(durationMillis = 150),
                label = "settingsTint"
            )

            val searchScale by animateFloatAsState(
                targetValue = if (activeScreen == ActiveScreen.BROWSER) 1.15f else 1.0f,
                animationSpec = tween(durationMillis = 150),
                label = "searchScale"
            )
            val shieldScale by animateFloatAsState(
                targetValue = if (activeScreen == ActiveScreen.SAFETY_SHIELD) 1.15f else 1.0f,
                animationSpec = tween(durationMillis = 150),
                label = "shieldScale"
            )
            val settingsScale by animateFloatAsState(
                targetValue = if (activeScreen == ActiveScreen.SETTINGS) 1.15f else 1.0f,
                animationSpec = tween(durationMillis = 150),
                label = "settingsScale"
            )

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White)
                    .border(1.dp, EditorialBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tab 1: Back Chevron
                IconButton(
                    onClick = { webViewRef?.goBack() },
                    enabled = showWebView && canGoBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = backTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tab 2: Forward Chevron
                IconButton(
                    onClick = { webViewRef?.goForward() },
                    enabled = showWebView && canGoForward,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_forward),
                        contentDescription = "Forward",
                        tint = forwardTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tab 3: Search / Home
                IconButton(
                    onClick = {
                        activeScreen = ActiveScreen.BROWSER
                        currentUrl = "sanctum://home"
                        inputText = ""
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = searchTint,
                        modifier = Modifier.size(20.dp).graphicsLayer(scaleX = searchScale, scaleY = searchScale)
                    )
                }

                // Tab 4: Safety Shield / Tabs
                IconButton(
                    onClick = { activeScreen = ActiveScreen.SAFETY_SHIELD },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tabs),
                        contentDescription = "Safety Shield",
                        tint = shieldTint,
                        modifier = Modifier.size(20.dp).graphicsLayer(scaleX = shieldScale, scaleY = shieldScale)
                    )
                }

                // Tab 5: Settings / Menu dots
                IconButton(
                    onClick = { activeScreen = ActiveScreen.SETTINGS },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu_dots),
                        contentDescription = "Settings",
                        tint = settingsTint,
                        modifier = Modifier.size(20.dp).graphicsLayer(scaleX = settingsScale, scaleY = settingsScale)
                    )
                }
            }
        }

        // Shield Details dialog
        AnimatedVisibility(
            visible = isUrlDetailsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(animationSpec = tween(250))
        ) {
            UrlDetailsDialog(context = context, urlStr = currentUrl, onClose = { isUrlDetailsOpen = false })
        }
    }
}

enum class ActiveScreen {
    BROWSER, SAFETY_SHIELD, SETTINGS, NATIVE_SEARCH
}

enum class ShieldMode {
    STANDARD,
    STRICT,
    STEALTH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchSubmit: (String) -> Unit,
    onProfileClick: () -> Unit,
    onShieldClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var homeSearchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Premium Fintech Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu_dots),
                contentDescription = "Menu",
                tint = EditorialInk,
                modifier = Modifier.size(20.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EditorialSurface)
                        .border(1.dp, EditorialBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Profile",
                        tint = EditorialInk,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Aman",
                        color = EditorialInk,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Inter
                    )
                    Text(
                        text = "amanxmedia@gmail.com",
                        color = EditorialMutedInk,
                        fontSize = 11.sp,
                        fontFamily = Inter
                    )
                }
            }

            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shield),
                    contentDescription = "Notification",
                    tint = EditorialInk,
                    modifier = Modifier.size(22.dp).clickable { onProfileClick() }
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shielded Threats Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Shielded threats",
                    color = EditorialMutedInk,
                    fontSize = 13.sp,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "25,291",
                    color = EditorialInk,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    letterSpacing = (-0.8).sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+100% Secure",
                    color = Color(0xFF10B981),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+ 1,284 today",
                    color = EditorialMutedInk,
                    fontSize = 11.sp,
                    fontFamily = Inter
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .clickable { onShieldClick() }
                    .padding(16.dp)
                    .height(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Shield Details",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(rotationZ = 135f)
                )
                Text(
                    text = "Security Shield",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SecondaryAccent)
                    .clickable { onSettingsClick() }
                    .padding(16.dp)
                    .height(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Filter Settings",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(rotationZ = -45f)
                )
                Text(
                    text = "Custom Filters",
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar Capsule
        TextField(
            value = homeSearchText,
            onValueChange = { homeSearchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .border(1.dp, EditorialBorder, RoundedCornerShape(25.dp))
                .clip(RoundedCornerShape(25.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = EditorialInk,
                unfocusedTextColor = EditorialInk
            ),
            textStyle = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp
            ),
            placeholder = {
                Text(
                    text = "Search or enter website",
                    color = EditorialMutedInk.copy(alpha = 0.7f),
                    fontFamily = Inter,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = EditorialMutedInk,
                    modifier = Modifier.size(18.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    if (homeSearchText.trim().isNotEmpty()) {
                        onSearchSubmit(homeSearchText)
                    }
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Links Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Links",
                color = EditorialInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                letterSpacing = (-0.2).sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* edit links */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = EditorialMutedInk,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Edit",
                    color = EditorialMutedInk,
                    fontSize = 12.sp,
                    fontFamily = Inter
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val quickLinks = listOf(
                Triple("Web", R.drawable.ic_globe, "https://google.com"),
                Triple("Inbox", R.drawable.ic_mail, "https://mail.google.com"),
                Triple("Recents", R.drawable.ic_clock, "sanctum://recents"),
                Triple("Saved", R.drawable.ic_star, "sanctum://saved")
            )

            quickLinks.forEach { link ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSearchSubmit(link.third) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EditorialBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = link.second),
                            contentDescription = link.first,
                            tint = EditorialInk,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = link.first,
                        color = EditorialInk,
                        fontSize = 12.sp,
                        fontFamily = Inter
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Active Protections Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Active Protections",
                color = EditorialInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                letterSpacing = (-0.2).sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
        ) {
            ProtectionListItem(
                title = "Ad & Tracker Blocker",
                source = "StevenBlack Host Filters",
                status = "1,284 blocked",
                indicator = "Active",
                onClick = { onShieldClick() }
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            ProtectionListItem(
                title = "DNS Guard",
                source = "Cloudflare Family DNS (1.1.1.3)",
                status = "Secure Resolved",
                indicator = "Active",
                onClick = { onShieldClick() }
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            ProtectionListItem(
                title = "Keyword Scanner",
                source = "Explicit Blocker Engine",
                status = "Scanning Active",
                indicator = "Active",
                onClick = { onSettingsClick() }
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            ProtectionListItem(
                title = "SafeSearch Enforcer",
                source = "Google Search Safeguard",
                status = "Redirect Enforced",
                indicator = "Active",
                onClick = { onSettingsClick() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Discover Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discover",
                color = EditorialInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                letterSpacing = (-0.2).sp
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_sparkles),
                contentDescription = "Discover",
                tint = EditorialMutedInk,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.discover_forest_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ENVIRONMENT",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "5 MIN READ",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Inter,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The Quiet Power of Sanctum Spaces",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TrendingListItem(
    title: String,
    source: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(EditorialSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_globe),
                    contentDescription = null,
                    tint = EditorialForest,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = EditorialInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = source,
                    color = EditorialMutedInk,
                    fontSize = 11.sp,
                    fontFamily = Inter
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "Chevron",
            tint = EditorialMutedInk.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun ProtectionListItem(
    title: String,
    source: String,
    status: String,
    indicator: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EditorialSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shield),
                    contentDescription = null,
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = EditorialInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = source,
                    color = EditorialMutedInk,
                    fontSize = 11.sp,
                    fontFamily = Inter
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = status,
                color = EditorialInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = indicator,
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter
            )
        }
    }
}

private fun getBlockedResponse(context: android.content.Context): WebResourceResponse {
    return try {
        val inputStream = context.assets.open("blocked.html")
        WebResourceResponse("text/html", "UTF-8", inputStream)
    } catch (e: Exception) {
        WebResourceResponse("text/html", "UTF-8", "Access Denied by Sanctum.".byteInputStream())
    }
}

private fun getErrorResponse(): WebResourceResponse {
    val errorHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Security Error</title>
            <style>
                body { background-color: #FCFCFA; color: #1E1E1E; font-family: serif; text-align: center; padding: 50px; }
                .box { background: #F6F6F2; border: 1px solid #E6E6E1; padding: 30px; border-radius: 4px; display: inline-block; max-width: 400px; }
                h1 { color: #2E4035; font-family: serif; font-size: 20px; margin-bottom: 12px; }
                p { color: #6B6B67; font-family: sans-serif; font-size: 13px; line-height: 1.6; }
                button { background: #2E4035; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; margin-top: 15px; }
            </style>
        </head>
        <body>
            <div class="box">
                <h1>Security Shield Active</h1>
                <p>Verification failed due to a network disruption. To protect your browsing environment, page load has been restricted.</p>
                <button onclick="location.reload();">Retry Connection</button>
            </div>
        </body>
        </html>
    """.trimIndent()
    return WebResourceResponse("text/html", "UTF-8", errorHtml.byteInputStream())
}

@Composable
fun SafetyShieldScreen(
    context: android.content.Context,
    adBlocking: Boolean,
    onAdBlockingChange: (Boolean) -> Unit,
    antiFingerprint: Boolean,
    onAntiFingerprintChange: (Boolean) -> Unit,
    httpsOnly: Boolean,
    onHttpsOnlyChange: (Boolean) -> Unit,
    shieldMode: ShieldMode,
    onShieldModeChange: (ShieldMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header Row matching AVA details style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = EditorialInk,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Shield Details",
                color = EditorialInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_shield),
                contentDescription = "Alerts",
                tint = EditorialInk,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Shield status layout (formerly AVA info card)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EditorialSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield),
                        contentDescription = null,
                        tint = EditorialInk,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Active Blocker Shield",
                    color = EditorialInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "100% Active",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large Counter
        Text(
            text = "1,284",
            color = EditorialInk,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = (-0.8).sp
        )
        Text(
            text = "Trackers blocked this week",
            color = EditorialMutedInk,
            fontSize = 12.sp,
            fontFamily = Inter
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Buy & Sell style inside details)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Charcoal button (Strict Mode)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .clickable { onShieldModeChange(ShieldMode.STRICT) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Strict Mode",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
            }

            // Light blue button (Standard Mode)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryAccent)
                    .clickable { onShieldModeChange(ShieldMode.STANDARD) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Standard Mode",
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Inter
                )
            }

            // Small Alert Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, EditorialBorder, RoundedCornerShape(12.dp))
                    .clickable { /* alert toggle */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Alert Alert",
                    tint = EditorialInk,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Blocker Trend Line Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(vertical = 8.dp)
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Grid lines
                drawLine(
                    color = EditorialBorder,
                    start = androidx.compose.ui.geometry.Offset(0f, height * 0.5f),
                    end = androidx.compose.ui.geometry.Offset(width, height * 0.5f),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Chart path
                val points = listOf(
                    androidx.compose.ui.geometry.Offset(0f, height * 0.8f),
                    androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.6f),
                    androidx.compose.ui.geometry.Offset(width * 0.3f, height * 0.75f),
                    androidx.compose.ui.geometry.Offset(width * 0.45f, height * 0.5f),
                    androidx.compose.ui.geometry.Offset(width * 0.6f, height * 0.3f),
                    androidx.compose.ui.geometry.Offset(width * 0.75f, height * 0.45f),
                    androidx.compose.ui.geometry.Offset(width * 0.9f, height * 0.15f),
                    androidx.compose.ui.geometry.Offset(width, height * 0.25f)
                )
                
                val chartPath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                
                drawPath(
                    path = chartPath,
                    color = Color(0xFF8B5CF6),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Highlight point
                drawCircle(
                    color = Color(0xFF8B5CF6),
                    radius = 4.dp.toPx(),
                    center = points[4]
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = points[4]
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Time Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val intervals = listOf("D", "W", "M", "6M", "Y", "All")
            intervals.forEach { interval ->
                val isSelected = interval == "M"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) EditorialSurface else Color.Transparent)
                        .clickable { /* select */ }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = interval,
                        color = if (isSelected) EditorialInk else EditorialMutedInk,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = Inter
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Blocker toggles list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
        ) {
            ShieldToggleRow(
                title = "Ad Blocking",
                description = "Prevent intrusive advertisements and pop-ups from loading during your session.",
                checked = adBlocking,
                onCheckedChange = onAdBlockingChange
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            ShieldToggleRow(
                title = "Anti-Fingerprint",
                description = "Obfuscate your browser signature to prevent cross-site tracking and profiling.",
                checked = antiFingerprint,
                onCheckedChange = onAntiFingerprintChange
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            ShieldToggleRow(
                title = "HTTPS Only",
                description = "Automatically upgrade connections to secure HTTPS whenever possible.",
                checked = httpsOnly,
                onCheckedChange = onHttpsOnlyChange
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    valueColor: Color = EditorialInk,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = EditorialMutedInk,
            fontSize = 11.sp,
            fontFamily = Inter
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter
        )
    }
}

@Composable
fun ShieldToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = EditorialInk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = EditorialMutedInk,
                fontSize = 11.sp,
                fontFamily = Inter,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EditorialForest,
                uncheckedThumbColor = EditorialMutedInk,
                uncheckedTrackColor = EditorialBorder
            )
        )
    }
}

@Composable
fun ShieldModeItem(
    mode: ShieldMode,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = EditorialInk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = EditorialMutedInk,
                fontSize = 11.sp,
                fontFamily = Inter,
                lineHeight = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) EditorialForest else EditorialBorder, CircleShape)
                .background(Color.White)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EditorialForest)
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    context: android.content.Context,
    familyMode: Boolean,
    onFamilyModeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var newCustomDomain by remember { mutableStateOf("") }
    var customDomainsList by remember { mutableStateOf(BlocklistManager.getCustomDomains(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Sanctum Logo",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sanctum",
                    color = EditorialInk,
                    fontSize = 20.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                tint = EditorialInk,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Screen Title
        Text(
            text = "Settings",
            color = EditorialInk,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = (-0.6).sp,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Configure your private browsing experience.",
            color = EditorialMutedInk,
            fontSize = 12.sp,
            fontFamily = Inter,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Family Mode Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EditorialSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = null,
                            tint = EditorialForest,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Family Mode",
                            color = EditorialInk,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Inter
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Block adult content & trackers",
                            color = EditorialMutedInk,
                            fontSize = 11.sp,
                            fontFamily = Inter
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = familyMode,
                    onCheckedChange = onFamilyModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EditorialForest,
                        uncheckedThumbColor = EditorialMutedInk,
                        uncheckedTrackColor = EditorialBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GENERAL Section
        Text(
            text = "GENERAL",
            color = EditorialMutedInk,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
        ) {
            SettingsNavigationRow(
                title = "Default Search Engine",
                value = "DuckDuckGo",
                onClick = {}
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            SettingsNavigationRow(
                title = "Appearance",
                value = "System",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PRIVACY & SECURITY Section
        Text(
            text = "PRIVACY & SECURITY",
            color = EditorialMutedInk,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
        ) {
            SettingsNavigationRow(
                title = "Passwords & Autofill",
                onClick = {}
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            SettingsNavigationRow(
                title = "Ad & Tracker Blocker",
                value = "Strict",
                onClick = {}
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
            SettingsNavigationRow(
                title = "Clear Browsing Data",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // USER BLOCKLIST (CUSTOM) Section
        Text(
            text = "USER BLOCKLIST (CUSTOM)",
            color = EditorialMutedInk,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Add Custom Blocked Domain",
                color = EditorialInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newCustomDomain,
                    onValueChange = { newCustomDomain = it },
                    placeholder = { Text("e.g. facebook.com", fontSize = 12.sp, fontStyle = FontStyle.Italic) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, EditorialBorder, RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = EditorialSurface,
                        unfocusedContainerColor = EditorialSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = EditorialInk,
                        unfocusedTextColor = EditorialInk
                    ),
                    textStyle = TextStyle(fontSize = 13.sp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EditorialForest)
                        .clickable {
                            if (BlocklistManager.addCustomDomain(context, newCustomDomain)) {
                                customDomainsList = BlocklistManager.getCustomDomains(context)
                                newCustomDomain = ""
                                Toast.makeText(context, "Domain added successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid domain name", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(text = "Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (customDomainsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Custom Blocked Sites:",
                    color = EditorialMutedInk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    customDomainsList.forEach { domain ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = domain, color = EditorialInk, fontSize = 13.sp, fontFamily = Inter)
                            Text(
                                text = "Remove",
                                color = Color.Red.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    if (BlocklistManager.removeCustomDomain(context, domain)) {
                                        customDomainsList = BlocklistManager.getCustomDomains(context)
                                        Toast.makeText(context, "Domain removed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(EditorialBorder))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Center Logo and Version Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sanctum Browser",
                color = EditorialInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter
            )
            Text(
                text = "Version 2.4.0 (Stable Build)",
                color = EditorialMutedInk,
                fontSize = 11.sp,
                fontFamily = Inter
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EditorialSurface)
                        .clickable {}
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "Terms of Service", color = EditorialInk, fontSize = 11.sp, fontFamily = Inter)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EditorialSurface)
                        .clickable {}
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "Privacy Policy", color = EditorialInk, fontSize = 11.sp, fontFamily = Inter)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer Text
        Text(
            text = "CRAFTED WITH PRECISION FOR PRIVATE LIVES.",
            color = EditorialMutedInk.copy(alpha = 0.5f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = EditorialInk,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Inter
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(
                    text = value,
                    color = EditorialMutedInk,
                    fontSize = 13.sp,
                    fontFamily = Inter,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = EditorialMutedInk.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun UrlDetailsDialog(
    context: android.content.Context,
    urlStr: String,
    onClose: () -> Unit
) {
    val cleanUrl = urlStr.replace("about:blank", "Home Screen")
    val domain = try {
        java.net.URI(urlStr).host ?: cleanUrl
    } catch (e: Exception) {
        cleanUrl
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(EditorialPaper)
                .clickable(enabled = false, onClick = {}) // prevent close click inside
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shield),
                        contentDescription = null,
                        tint = EditorialForest,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Security Shield Active",
                        color = EditorialForest,
                        fontSize = 16.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    )
                }
                Text(
                    text = "Close",
                    color = EditorialMutedInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onClose)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Domain indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EditorialSurface)
                    .padding(14.dp)
            ) {
                Text(text = "DOMAIN", color = EditorialMutedInk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(text = domain ?: "Unknown", color = EditorialInk, fontSize = 13.sp, fontFamily = Inter)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "FULL URL", color = EditorialMutedInk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(text = cleanUrl, color = EditorialInk, fontSize = 11.sp, fontFamily = Inter)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Block site
            if (urlStr.startsWith("http")) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EditorialSurface)
                        .border(1.dp, EditorialBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            if (domain != null && BlocklistManager.addCustomDomain(context, domain)) {
                                Toast.makeText(context, "$domain added to blocklist", Toast.LENGTH_SHORT).show()
                                onClose()
                            }
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = null,
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Block this domain ($domain)", color = EditorialInk, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TabOverviewScreen(
    tabManager: TabManager,
    onClose: () -> Unit,
    onNewTab: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialPaper)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Open Tabs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter,
                color = EditorialInk
            )
            Text(
                text = "Done",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                color = SecondaryAccent,
                modifier = Modifier.clickable { onClose() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabManager.tabs.forEachIndexed { index, tab ->
                val isSelected = tabManager.activeTabIndex.value == index
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EditorialSurface)
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) SecondaryAccent else EditorialBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            tabManager.switchTab(index)
                            onClose()
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tab.title.value.ifEmpty { "New Tab" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EditorialInk,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tab.url.value,
                                fontSize = 12.sp,
                                color = EditorialMutedInk,
                                maxLines = 1
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2))
                                .clickable {
                                    tabManager.closeTab(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("X", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // New Tab Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EditorialInk)
                    .clickable { onNewTab() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ New Tab",
                    color = EditorialPaper,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter
                )
            }
        }
    }
}
