package com.navideck.gesturedeck_flutter

import android.os.Bundle
import android.view.KeyEvent
import io.flutter.embedding.android.FlutterActivity

open class GesturedeckFlutterActivity : FlutterActivity() {
    private var gesturedeckFlutterPlugin: GesturedeckFlutterPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gesturedeckFlutterPlugin = GesturedeckFlutterPlugin.instance
        gesturedeckFlutterPlugin?.extendAroundNotch(this)
    }

    // To Take Control over VolumeKeys , and show our custom UI instead of Device VolumeDialog
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeckFlutterPlugin?.dispatchKeyEvent(event) ?: false
    }
}