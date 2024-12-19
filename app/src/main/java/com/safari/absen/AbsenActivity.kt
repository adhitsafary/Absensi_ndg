package com.safari.absen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AbsenActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.absenactivity)

        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (permission in request.resources) {
                        when (permission) {
                            "android.webkit.resource.VIDEO_CAPTURE" -> {
                                handleCameraPermission(request)
                                return
                            }
                            "android.webkit.resource.AUDIO_CAPTURE" -> {
                                // Handle audio capture permission if needed
                                return
                            }
                            "android.webkit.resource.GEOLOCATION" -> {
                                handleLocationPermission(request)
                                return
                            }
                        }
                    }
                    request.deny()
                }
            }
        }

        webView.loadUrl("https://dashboard.netdigitalgroup.com/absensi/index")
    }

    private fun handleCameraPermission(request: PermissionRequest) {
        if (ContextCompat.checkSelfPermission(
                this@AbsenActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            request.grant(request.resources)
        } else {
            ActivityCompat.requestPermissions(
                this@AbsenActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun handleLocationPermission(request: PermissionRequest) {
        if (ContextCompat.checkSelfPermission(
                this@AbsenActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this@AbsenActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            request.grant(request.resources)
        } else {
            ActivityCompat.requestPermissions(
                this@AbsenActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    webView.reload()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                    webView.reload()
                }
            }
        }
    }
}