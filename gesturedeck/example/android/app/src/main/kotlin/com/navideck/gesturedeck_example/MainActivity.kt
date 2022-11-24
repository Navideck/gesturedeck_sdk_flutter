package com.navideck.gesturedeck_example

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.*
import com.navideck.gesturedeck.GesturedeckPlugin
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        GesturedeckPlugin.instance?.dispatchTouchEvent(event, activity)
        return super.dispatchTouchEvent(event)
    }

    // add this to Take Control over VolumeKeys , and show our custom UI instead of Device VolumeDialog
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return GesturedeckPlugin.instance?.dispatchKeyEvent(event) ?: false
    }

    /// Call these methods in onCreate of a native android project
    override fun onStart() {
        super.onStart()
        // Works in Android Sdk > P (28 ) only
        // In Samsung Note ultra (Android 12), only this method was enough , to make navigation bar transparent
        coverCameraCutout(this)

        // One plus (Android 11 ) requires this as well to change color of navigation bar
        // still navigation bar is not transparent
        tryToMakeTransparentNavigationBar(this)

        // uncomment this to completely hide the navigation bars
        // hideNavigationBar(this)
    }

    /// To cover Camera Cutout
    private fun coverCameraCutout(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    /// this method can try to make navigation bar transparent ( needs more testing )
    /// this can change colour of navigation bar , and in few devices
    /// if we try to set colour to transparent , then device automatically converting that to white
    private fun tryToMakeTransparentNavigationBar(activity: Activity) {
        var systemUiVisibility = 0
        var navigationBarColor = Color.parseColor("#40000000")
        val winParams = activity.window.attributes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            navigationBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            activity.window.decorView.systemUiVisibility = systemUiVisibility
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            winParams.flags =
                winParams.flags and (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION).inv()
            activity.window.navigationBarColor = navigationBarColor
        }
        activity.window.attributes = winParams
    }

    /// To completely hide navigation bar
    private fun hideNavigationBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // call WindowInsets.Type.systemBars to completely hide top and bottom bars
            activity.window.insetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            val decorView = activity.window.decorView
            var uiVisibility = decorView.systemUiVisibility
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            decorView.systemUiVisibility = uiVisibility
        }
    }

}