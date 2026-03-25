package com.example.fascience

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebsiteScreen("https://sites.google.com/view/fa-science/home")
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebsiteScreen(url: String) {

    var webView by remember { mutableStateOf<WebView?>(null) }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            val root = FrameLayout(context)

            val myWebView = WebView(context)
            val progressBar = ProgressBar(context)

            val errorLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                visibility = View.GONE
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val errorText = TextView(context).apply {
                text = "ইন্টারনেট সংযোগ নেই\n\nদয়া করে আবার চেষ্টা করুন"
                textSize = 20f
                gravity = Gravity.CENTER
            }

            val retryButton = Button(context).apply {
                text = "রিফ্রেশ / আবার চেষ্টা করুন"
            }

            errorLayout.addView(errorText)
            errorLayout.addView(retryButton)

            myWebView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            progressBar.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )

            myWebView.settings.javaScriptEnabled = true
            myWebView.settings.domStorageEnabled = true

            myWebView.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    myWebView.visibility = View.VISIBLE
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        progressBar.visibility = View.GONE
                        myWebView.visibility = View.GONE
                        errorLayout.visibility = View.VISIBLE
                    }
                    super.onReceivedError(view, request, error)
                }
            }

            retryButton.setOnClickListener {
                errorLayout.visibility = View.GONE
                myWebView.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                myWebView.reload()
            }

            myWebView.loadUrl(url)
            webView = myWebView

            root.addView(myWebView)
            root.addView(progressBar)
            root.addView(errorLayout)

            root
        }
    )
}