package com.example.vpnstarter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.falconvpn.app.vpn.platform.FalconVpnService
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.view.View
import android.view.Gravity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import android.webkit.JavascriptInterface
import org.json.JSONObject
import com.falconvpn.app.vpn.engine.ShadowsocksLinkParser
import com.falconvpn.app.vpn.engine.VlessLinkParser
import com.falconvpn.app.vpn.engine.SecureProfileStore
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

class MainActivity : ComponentActivity() {
    private var appWebView: WebView? = null
    private lateinit var rootFrame: FrameLayout
    private var bannerAd: AdView? = null
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var adBusy = false
    private var bannerLoaded = false
    private val useTestAds = true // Switch to false only after AdMob review and production validation.
    private val productionBannerId = "ca-app-pub-3570954109106643/2601126416"
    private val productionInterstitialId = "ca-app-pub-3570954109106643/1398679202"
    private val productionRewardedId = "ca-app-pub-3570954109106643/3806396875"
    private val testBannerId = "ca-app-pub-3940256099942544/6300978111"
    private val testInterstitialId = "ca-app-pub-3940256099942544/1033173712"
    private val testRewardedId = "ca-app-pub-3940256099942544/5224354917"
    private val bannerUnitId get() = if (useTestAds) testBannerId else productionBannerId
    private val interstitialUnitId get() = if (useTestAds) testInterstitialId else productionInterstitialId
    private val rewardedUnitId get() = if (useTestAds) testRewardedId else productionRewardedId
    private val adPrefs by lazy { getSharedPreferences("falcon_ad_rewards", MODE_PRIVATE) }
    private var lastInterstitialAt = 0L
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val filePickerRequest = 4102
    private val profileStore by lazy { SecureProfileStore(applicationContext) }
    private var pendingConnectAfterPermission = false
    private val vpnPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && pendingConnectAfterPermission) {
            pendingConnectAfterPermission = false
            startVpnService()
        } else {
            pendingConnectAfterPermission = false
            saveRuntimeState(FalconVpnService.STATE_NEEDS_PERMISSION, "لم يتم منح إذن VPN؛ لم يبدأ الاتصال.")
        }
    }
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        // Android allows VPN consent separately. Continue only after handling notification permission.
        if (pendingConnectAfterPermission) requestVpnPermissionOrStart()
        if (!granted) saveRuntimeState(FalconVpnService.STATE_ERROR, "إذن الإشعارات مرفوض؛ قد لا تظهر إشعارات حالة VPN على Android 13+.")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootFrame = FrameLayout(this)
        val webView = WebView(this)
        appWebView = webView
        webView.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        // The bridge is attached only to the bundled local asset, not remote pages.
        webView.addJavascriptInterface(FalconNativeBridge(), "FalconNative")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                if (uri.scheme == "http" || uri.scheme == "https") {
                    return try {
                        startActivity(Intent(Intent.ACTION_VIEW, uri)); true
                    } catch (_: Exception) { false }
                }
                return false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.fileChooserCallback?.onReceiveValue(null)
                this@MainActivity.fileChooserCallback = filePathCallback
                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
                return try { startActivityForResult(intent, filePickerRequest); true }
                catch (_: Exception) { this@MainActivity.fileChooserCallback = null; false }
            }
        }
        rootFrame.addView(webView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        ))
        setContentView(rootFrame)
        requestAdConsent()
        webView.loadUrl("file:///android_asset/index.html")
        // Handle files/links opened from Instagram, a browser, or Android's share sheet.
        handleIncomingIntent(intent)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })
    }

    private inner class FalconNativeBridge {
        @JavascriptInterface
        fun validateShadowsocksLink(raw: String): String {
            return try {
                val profile = ShadowsocksLinkParser.parse(raw)
                JSONObject()
                    .put("ok", true)
                    .put("protocol", "Shadowsocks")
                    .put("name", profile.name)
                    .put("host", profile.host)
                    .put("port", profile.port)
                    .put("method", profile.method)
                    // Never return the password to JavaScript or write it to logs.
                    .put("message", "تم التحقق من صيغة الرابط. لم يتم حفظه أو الاتصال بالخادم بعد.")
                    .toString()
            } catch (e: Exception) {
                JSONObject()
                    .put("ok", false)
                    .put("message", e.message ?: "رابط Shadowsocks غير صالح")
                    .toString()
            }
        }

        @JavascriptInterface
        fun validateVlessLink(raw: String): String {
            return try {
                val profile = VlessLinkParser.parse(raw)
                JSONObject().put("ok", true).put("protocol", "VLESS")
                    .put("name", profile.name).put("host", profile.host).put("port", profile.port)
                    .put("security", profile.security).put("transport", profile.transport)
                    .put("sni", profile.sni)
                    .put("message", "تم تحليل رابط VLESS. لم يتم الاتصال بالخادم بعد.").toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("message", e.message ?: "رابط VLESS غير صالح").toString()
            }
        }

        @JavascriptInterface
        fun saveVlessLink(raw: String): String {
            return try {
                val profile = VlessLinkParser.parse(raw)
                profileStore.saveVless(profile).put("ok", true)
                    .put("message", "تم حفظ كونفج VLESS مشفرًا وتحديده. الاتصال الفعلي يحتاج محرك VPN مكتملًا.").toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("message", e.message ?: "تعذر حفظ كونفج VLESS").toString()
            }
        }

        @JavascriptInterface
        fun saveShadowsocksLink(raw: String): String {
            return try {
                val profile = ShadowsocksLinkParser.parse(raw)
                profileStore.save(profile)
                    .put("ok", true)
                    .put("message", "تم حفظ الكونفج مشفرًا على هذا الجهاز. لم يتم الاتصال بالخادم.")
                    .toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false)
                    .put("message", e.message ?: "تعذر حفظ الكونفج")
                    .toString()
            }
        }

        /** Imports newline-separated SIP002 Shadowsocks links. Invalid lines are reported by line number only. */
        @JavascriptInterface
        fun importShadowsocksLinks(text: String): String {
            return try {
                require(text.length <= 200_000) { "حجم قائمة الروابط كبير جدًا." }
                val lines = text.lineSequence().map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }.toList()
                require(lines.size <= 100) { "يمكن استيراد 100 رابط كحد أقصى في المرة الواحدة." }
                val imported = org.json.JSONArray()
                val rejected = org.json.JSONArray()
                lines.forEachIndexed { index, line ->
                    try {
                        val profile = ShadowsocksLinkParser.parse(line)
                        imported.put(profileStore.save(profile))
                    } catch (_: Exception) {
                        rejected.put(index + 1)
                    }
                }
                JSONObject()
                    .put("ok", rejected.length() == 0 && imported.length() > 0)
                    .put("imported", imported.length())
                    .put("rejectedCount", rejected.length())
                    .put("rejectedLines", rejected)
                    .put("profiles", profileStore.list())
                    .put("message", "تم استيراد ${imported.length()} رابط صالح. الروابط المرفوضة لم تُحفظ.")
                    .toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false)
                    .put("message", e.message ?: "تعذر استيراد الروابط")
                    .toString()
            }
        }

        @JavascriptInterface
        fun saveShadowsocksFields(name: String, host: String, port: Int, method: String, password: String): String {
            return try {
                require(host.isNotBlank() && !host.contains("/") && !host.contains("@")) { "عنوان الخادم غير صالح." }
                require(port in 1..65535) { "المنفذ يجب أن يكون بين 1 و65535." }
                require(method.matches(Regex("[A-Za-z0-9._-]+"))) { "طريقة التشفير غير صالحة." }
                require(password.isNotBlank()) { "كلمة مرور الخادم مطلوبة." }
                val profile = com.falconvpn.app.vpn.engine.ShadowsocksProfile(
                    name.trim().ifBlank { "Shadowsocks server" }, host.trim(), port, method.trim(), password
                )
                profileStore.save(profile).put("ok", true)
                    .put("message", "تم حفظ إعداد Shadowsocks مشفرًا على الجهاز.").toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("message", e.message ?: "تعذر حفظ إعداد السيرفر.").toString()
            }
        }

        @JavascriptInterface
        fun listSavedProfiles(): String {
            return try { JSONObject().put("ok", true).put("profiles", profileStore.list()).toString() }
            catch (e: Exception) { JSONObject().put("ok", false).put("message", e.message ?: "تعذر قراءة الكونفجات").toString() }
        }

        @JavascriptInterface
        fun deleteSavedProfile(id: String): String {
            return try { JSONObject().put("ok", profileStore.delete(id)).toString() }
            catch (e: Exception) { JSONObject().put("ok", false).put("message", e.message ?: "تعذر حذف الكونفج").toString() }
        }

        @JavascriptInterface
        fun selectSavedProfile(id: String): String {
            return try {
                val selected = profileStore.select(id)
                JSONObject().put("ok", selected)
                    .put("message", if (selected) "تم تحديد الكونفج للاستخدام. لم يبدأ الاتصال بعد." else "الكونفج غير موجود أو تعذر تحديده.")
                    .toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("message", e.message ?: "تعذر تحديد الكونفج").toString()
            }
        }

        @JavascriptInterface
        fun selectedSavedProfile(): String {
            return try {
                val selected = profileStore.selectedMetadata()
                JSONObject().put("ok", true).put("profile", selected ?: JSONObject.NULL).toString()
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("message", e.message ?: "تعذر قراءة الكونفج المحدد").toString()
            }
        }

        @JavascriptInterface
        fun positionBannerAd(top: Double, left: Double, width: Double, height: Double) {
            runOnUiThread {
                val web = appWebView ?: return@runOnUiThread
                if (isFinishing || width <= 0 || height <= 0) return@runOnUiThread
                if (bannerAd == null) {
                    bannerAd = AdView(this@MainActivity).apply {
                        adUnitId = bannerUnitId
                        setAdSize(AdSize.BANNER)
                        adListener = object : com.google.android.gms.ads.AdListener() {
                            override fun onAdLoaded() { bannerLoaded = true; visibility = View.VISIBLE }
                            override fun onAdFailedToLoad(error: LoadAdError) { bannerLoaded = false; visibility = View.GONE }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                    rootFrame.addView(bannerAd, FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
                    ))
                }
                val scale = web.scale.toFloat()
                val webLoc = IntArray(2); val rootLoc = IntArray(2)
                web.getLocationOnScreen(webLoc); rootFrame.getLocationOnScreen(rootLoc)
                val x = webLoc[0] - rootLoc[0] + (left * scale).toInt()
                val y = webLoc[1] - rootLoc[1] + (top * scale).toInt()
                val params = bannerAd?.layoutParams as? FrameLayout.LayoutParams ?: return@runOnUiThread
                params.gravity = Gravity.TOP or Gravity.LEFT
                params.leftMargin = x
                params.topMargin = y
                bannerAd?.layoutParams = params
                bannerAd?.visibility = View.VISIBLE
            }
        }

        @JavascriptInterface
        fun hideBannerAd() { runOnUiThread { bannerAd?.visibility = View.GONE } }

        @JavascriptInterface
        fun showRewardedAd(): String {
            runOnUiThread { showRewardedAdNative() }
            return JSONObject().put("ok", true).put("message", "جارٍ تجهيز إعلان المكافأة.").toString()
        }

        @JavascriptInterface
        fun bonusSeconds(): Long = adPrefs.getLong("bonus_seconds", 0L)

        @JavascriptInterface
        fun showConnectionInterstitial() {
            runOnUiThread { showInterstitialIfEligible() }
        }

        @JavascriptInterface
        fun connectVpn(): String {
            val selected = runCatching { profileStore.getSelectedForEngine() }.getOrNull()
            if (selected == null) return JSONObject().put("ok", false).put("state", "ERROR").put("message", "حدد كونفج محفوظًا أولًا.").toString()
            runOnUiThread { beginVpnPermissionFlow() }
            return JSONObject().put("ok", true).put("state", "CONNECTING").put("message", "جارٍ فحص إذن VPN وتجهيز الخدمة.").toString()
        }

        @JavascriptInterface
        fun disconnectVpn(): String {
            runOnUiThread {
                startService(Intent(this@MainActivity, FalconVpnService::class.java).setAction(FalconVpnService.ACTION_STOP))
            }
            return JSONObject().put("ok", true).put("state", "DISCONNECTED").put("message", "تم طلب إيقاف VPN.").toString()
        }

        @JavascriptInterface
        fun engineStatus(): String {
            val prefs = getSharedPreferences(FalconVpnService.STATUS_PREFS, MODE_PRIVATE)
            val state = prefs.getString(FalconVpnService.KEY_STATE, FalconVpnService.STATE_DISCONNECTED) ?: FalconVpnService.STATE_DISCONNECTED
            val message = prefs.getString(FalconVpnService.KEY_MESSAGE, "لم يبدأ الاتصال.") ?: "لم يبدأ الاتصال."
            val engineIntegrated = false // Kept false until CommandServer + PlatformInterface + TUN are implemented and verified.
            return JSONObject()
                .put("ready", engineIntegrated && state == FalconVpnService.STATE_CONNECTED)
                .put("state", state)
                .put("message", message)
                .put("updatedAt", prefs.getLong(FalconVpnService.KEY_UPDATED_AT, 0L))
                .put("remainingSeconds", prefs.getLong(FalconVpnService.KEY_REMAINING_SECONDS, 0L))
                .put("sessionEndsAt", prefs.getLong(FalconVpnService.KEY_SESSION_ENDS_AT, 0L))
                .put("engineIntegrated", engineIntegrated)
                .toString()
        }
    }


    private var adsInitialized = false

    private fun requestAdConsent() {
        val consentInfo = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()
        consentInfo.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) {
                    initializeAdsIfAllowed(consentInfo.canRequestAds())
                }
            },
            {
                initializeAdsIfAllowed(consentInfo.canRequestAds())
            }
        )
    }

    private fun initializeAdsIfAllowed(allowed: Boolean) {
        if (!allowed || adsInitialized || isFinishing) return
        adsInitialized = true
        MobileAds.initialize(this) {
            runOnUiThread { loadRewardedAd(); loadInterstitialAd() }
        }
    }

    private fun loadRewardedAd() {
        RewardedAd.load(this, rewardedUnitId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
        })
    }

    private fun showRewardedAdNative() {
        if (adBusy) return
        val ad = rewardedAd
        if (ad == null) {
            notifyWeb("falconAdUnavailable", "الإعلان غير متاح حاليًا. حاول بعد قليل.")
            loadRewardedAd()
            return
        }
        adBusy = true
        rewardedAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { adBusy = false; loadRewardedAd() }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                adBusy = false; notifyWeb("falconAdUnavailable", "تعذر عرض الإعلان. حاول مرة أخرى."); loadRewardedAd()
            }
        }
        ad.show(this) {
            val total = adPrefs.getLong("bonus_seconds", 0L) + 900L
            adPrefs.edit().putLong("bonus_seconds", total).apply()
            notifyWeb("falconOnRewardEarned", total.toString())
            // If a verified VPN session is active, extend its native timer as well.
            startService(Intent(this, FalconVpnService::class.java).setAction(FalconVpnService.ACTION_ADD_BONUS_TIME))
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(this, interstitialUnitId, AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { interstitialAd = null }
        })
    }

    private fun showInterstitialIfEligible() {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialAt < 30 * 60 * 1000L) return
        val ad = interstitialAd ?: return
        lastInterstitialAt = now
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { loadInterstitialAd() }
            override fun onAdFailedToShowFullScreenContent(error: AdError) { loadInterstitialAd() }
        }
        ad.show(this)
    }

    private fun notifyWeb(function: String, value: String) {
        val safe = JSONObject.quote(value)
        runOnUiThread { appWebView?.evaluateJavascript("window.$function && window.$function($safe)", null) }
    }

    override fun onDestroy() {
        bannerAd?.destroy(); bannerAd = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    @Suppress("DEPRECATION")
    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        if (action != Intent.ACTION_VIEW && action != Intent.ACTION_SEND) return
        try {
            val raw = when (action) {
                Intent.ACTION_VIEW -> {
                    val uri = intent.data ?: return
                    if (uri.scheme.equals("content", true) || uri.scheme.equals("file", true)) {
                        contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    } else uri.toString()
                }
                Intent.ACTION_SEND -> {
                    intent.getStringExtra(Intent.EXTRA_TEXT)
                        ?: intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                            ?.let { uri -> contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } }
                }
                else -> null
            } ?: return
            importIncomingContent(raw)
        } catch (e: Exception) {
            showImportMessage("تعذر قراءة الملف. تأكد أنه ملف نصي أو رابط مشاركة مدعوم.")
        }
    }

    private fun importIncomingContent(raw: String) {
        require(raw.isNotBlank() && raw.length <= 200_000) { "الملف فارغ أو حجمه أكبر من المسموح." }
        // Currently normalize common Shadowsocks share links into Falcon's saved-profile model.
        // Unknown formats are rejected rather than silently converted incorrectly.
        val candidates = raw.lineSequence().map { it.trim() }
            .filter { it.startsWith("ss://", ignoreCase = true) || it.startsWith("vless://", ignoreCase = true) }.toList()
        if (candidates.isEmpty()) {
            showImportMessage("الملف وصل إلى Falcon VPN، لكن صيغته غير مدعومة حاليًا. الصيغ المدعومة في هذا الإصدار: ss:// وvless://.")
            return
        }
        var imported = 0
        var lastId: String? = null
        candidates.forEach { line ->
            runCatching {
                val saved = if (line.startsWith("vless://", ignoreCase = true)) {
                    profileStore.saveVless(VlessLinkParser.parse(line))
                } else {
                    profileStore.save(ShadowsocksLinkParser.parse(line))
                }
                lastId = saved.optString("id").takeIf { it.isNotBlank() }
                imported++
            }
        }
        lastId?.let { profileStore.select(it) }
        if (imported > 0) {
            showImportMessage("تم استيراد $imported كونفج صالح وحفظه وتحديد آخر كونفج. الاتصال الفعلي يحتاج محرك VPN مكتملًا.")
        } else {
            showImportMessage("تعذر استيراد الكونفج: صيغة الرابط غير صحيحة أو غير مدعومة.")
        }
    }

    private fun showImportMessage(message: String) {
        val safe = org.json.JSONObject.quote(message)
        runOnUiThread {
            appWebView?.post {
                appWebView?.evaluateJavascript("if (window.falconImportedConfig) { window.falconImportedConfig($safe); } else { window.__falconImportNotice = $safe; }", null)
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun beginVpnPermissionFlow() {
        pendingConnectAfterPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        requestVpnPermissionOrStart()
    }

    private fun requestVpnPermissionOrStart() {
        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            saveRuntimeState(FalconVpnService.STATE_NEEDS_PERMISSION, "مطلوب السماح لاتصال VPN من نافذة أندرويد.")
            vpnPermissionLauncher.launch(prepareIntent)
        } else if (pendingConnectAfterPermission) {
            pendingConnectAfterPermission = false
            startVpnService()
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, FalconVpnService::class.java).setAction(FalconVpnService.ACTION_START)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
        } catch (e: Exception) {
            saveRuntimeState(FalconVpnService.STATE_ERROR, e.message ?: "تعذر تشغيل خدمة VPN.")
        }
    }

    private fun saveRuntimeState(state: String, message: String) {
        getSharedPreferences(FalconVpnService.STATUS_PREFS, MODE_PRIVATE).edit()
            .putString(FalconVpnService.KEY_STATE, state)
            .putString(FalconVpnService.KEY_MESSAGE, message)
            .putLong(FalconVpnService.KEY_UPDATED_AT, System.currentTimeMillis())
            .apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filePickerRequest) {
            val result = if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { arrayOf(it) }
            } else null
            fileChooserCallback?.onReceiveValue(result)
            fileChooserCallback = null
        }
    }

    override fun onDestroy() {
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = null
        appWebView = null
        super.onDestroy()
    }
}
