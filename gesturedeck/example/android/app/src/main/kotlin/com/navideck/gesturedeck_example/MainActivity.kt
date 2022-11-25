package com.navideck.gesturedeck_example

import android.os.Bundle
import android.view.*
import com.navideck.gesturedeck.GesturedeckPlugin
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GesturedeckPlugin.instance?.extendAroundNotch(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        GesturedeckPlugin.instance?.dispatchTouchEvent(event, activity)
        return super.dispatchTouchEvent(event)
    }

    // add this to Take Control over VolumeKeys , and show our custom UI instead of Device VolumeDialog
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return GesturedeckPlugin.instance?.dispatchKeyEvent(event) ?: false
    }

}