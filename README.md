# Sanctum Browser

**A premium, privacy-first Android browser with built-in DNS-over-HTTPS (DoH) safety filters, SafeSearch enforcement, URL keyword blocking, and a sleek iOS-inspired card UI.**

### 📥 [Download Sanctum APK (v2.4.0)](https://github.com/Aman-teach/Sanctum/releases/download/v2.4.0/Sanctum_Browser.apk)

---

## 🎨 Premium Design Aesthetics

Sanctum features a warm-light "Editorial" theme, tailored for legibility and visual hierarchy:
* **Branded Logo Top Bar**: Renders the custom multi-colored Sanctum emblem at a crisp `30.dp` scale for header spaces.
* **Premium Typography (Inter)**: Integrated with the **Inter** font family (Regular, Medium, SemiBold, Bold) locally in app resources, utilizing tight letter-spacing (`-0.6.sp` / `-0.2.sp`) for titles and wide letter-spacing (`1.5.sp`) for uppercase labels.
* **Subtle Micro-Animations**:
  * **Screen Switch Crossfade**: Elegant `220ms` screen-switching transitions.
  * **Bottom Navigation Pop**: Dynamic icon scaling (to `1.15x`) and smooth color-morph transitions.
  * **Pulsing Safety Dot**: Continuous pulsing dot (alpha sweeps between `0.4` and `1.0`) reflecting real-time traffic filtering.
  * **Slide-Up Bottom Sheet**: Slide-and-fade dialogue for site security checks.

---

## 🛡️ Core Functionality & Security Features

### 1. Multi-Layer Ad & Tracker Blocking
Strips intrusive advertisements, profiling scripts, and web trackers at the network request level using compiled local blocker lists (inspired by StevenBlack hosts list).

### 2. DNS-over-HTTPS (DoH) Filtering
Queries Cloudflare's Secure Family DNS (`1.1.1.3`) asynchronously via HTTPS tunnels to intercept and block adult-themed domains, malware, and phishing sites before the WebView processes the page request.

### 3. SafeSearch Enforcement
Rewrites outbound search engine URLs (including Google) to append active SafeSearch flags (`safe=active`), filtering out explicit content from search query result lists automatically.

### 4. Real-time Keyword Blocker
Scans target URLs for a library of explicit and custom keywords, immediately halting requests and redirecting users to a local, offline-cached safe page (`blocked.html`) when violations occur.

### 5. Custom User Blocklist
Empowers users to add or remove domains from a custom list. Selection states are saved persistently using Android's `SharedPreferences` framework.

---

## 🛠️ Technical Architecture

* **UI Framework**: Android Jetpack Compose (Material 3)
* **Language**: Kotlin
* **Typographic Engine**: Custom Type Scale inheriting local Font Resources
* **Web Rendering**: Optimized Android `WebView` running isolated from Compose redrawing cycles to maintain state and history performance.
* **DNS Resolution**: Coroutine-driven asynchronous network client querying secure DoH servers.

---

## 🚀 Building & Running

### Requirements
* Android Studio (Koala or newer)
* Android SDK (API Level 24+ supported, Target API Level 34)
* JDK 17 (recommended to use Android Studio's bundled runtime)

### Build Instructions
Open your terminal in the repository root and run:

```bash
# Set Java Home to JDK 17 (Windows example)
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"

# Assemble the debug APK
.\gradlew.bat assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
The typography files are distributed under the **SIL Open Font License**. The codebase itself is licensed under open-source standards. Developed with precision for private lives.
