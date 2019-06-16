package com.example.ntduc.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity
    : AppCompatActivity() {

    private lateinit var webview: WebView

    companion object {
        val WEBVIEW_JS = "WebViewJS"
    }


    private val webViewClient: WebViewClient = object : WebViewClient() {
        override
        fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            webview.loadUrl("javascript:$WEBVIEW_JS.onHeight(document.body.getBoundingClientRect().height)")
            changeHintInput()
        }
    }


    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webview = findViewById(R.id.web_view)

        setupWebView(webview)

        webview.loadUrl("https://www.google.com.vn/")
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setupWebView(webView: WebView) {
        webView.webViewClient = webViewClient
        webview.addJavascriptInterface(this, WEBVIEW_JS)
        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            builtInZoomControls = true
            pluginState = WebSettings.PluginState.ON
            displayZoomControls = false
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            blockNetworkImage = false
            defaultFontSize = 15
            displayZoomControls = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportZoom(true)
            setAppCacheEnabled(false)
        }
    }

    @JavascriptInterface
    fun onHeight(height: Float) {
        runOnUiThread {
            printHeightOnWebView(height)
            changeHintInput()
        }
    }

    private fun changeHintInput() {
        webview.loadUrl("javascript:" +
                "var input = document.getElementsByClassName('gLFyf')[0];" +
                "input.style.border = 'thick solid #1234FF';" +
                "var placeholder = document.createAttribute('placeholder');" +
                "placeholder.value = 'Search gi di';" +
                "input.setAttributeNode(placeholder);")
    }

    private fun printHeightOnWebView(height: Float) {
        webview.loadUrl("javascript:" +
                "var div = document.createElement('div');" +
                "div.style.border = 'thick solid #0000FF';" +
                "div.id = 'size-height';" +
                "div.className = 'block';" +
                "div.innerHTML='&nbsp;&nbsp;height = $height';" +
                "document.getElementById('main').appendChild(div);")
    }

}
