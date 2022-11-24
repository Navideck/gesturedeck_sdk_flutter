package com.navideck.gesturedeck_android

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager

class SystemUiHandler(var activity: Activity) {
    /// To cover Camera Cutout
    fun coverCameraCutout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    // this method will make bottomBar transparent on Samsung Note20
    // but also this will remove the top status bar, and app will become full screen
    fun tryFullScreen() {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    /// this method can try to make navigation bar transparent ( needs more testing )
    /// this can change colour of navigation bar , and in few devices
    /// if we try to set colour to transparent , then device automatically converting that to white
    fun tryToMakeTransparentNavigationBar() {
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
    fun hideNavigationBar() {
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