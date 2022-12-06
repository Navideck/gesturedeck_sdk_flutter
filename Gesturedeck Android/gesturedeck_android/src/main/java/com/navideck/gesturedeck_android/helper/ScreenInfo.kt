package com.navideck.gesturedeck_android.helper

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.doOnAttach
import android.graphics.Insets
import android.graphics.Rect

object ScreenInfo {

    @Suppress("DEPRECATION")
    fun getScreenSize(context: Context): Size {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return getScreenSizeFromApiLevel23(context)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return getScreenSizeFromApiLevel30(context)
        }
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        return Size(display.width, display.height)
    }

    fun getOrientationMode(context: Context): OrientationMode {
        try {
            @Suppress("DEPRECATION")
            val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    context.display
                } catch (_: Exception) {
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.defaultDisplay
                }
            } else {
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)?.defaultDisplay
            }
            return when (display?.rotation) {
                Surface.ROTATION_0 -> OrientationMode.PORTRAIT
                Surface.ROTATION_90 -> OrientationMode.LANDSCAPE
                Surface.ROTATION_180 -> OrientationMode.REVERSE_PORTRAIT
                Surface.ROTATION_270 -> OrientationMode.REVERSE_LANDSCAPE
                else -> OrientationMode.PORTRAIT
            }
        } catch (_: Exception) {
        }
        return OrientationMode.PORTRAIT
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getScreenSizeFromApiLevel30(context: Context): Size {
        val metrics: WindowMetrics =
            context.getSystemService(WindowManager::class.java).currentWindowMetrics
        return Size(metrics.bounds.width(), metrics.bounds.height())
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @Suppress("DEPRECATION")
    private fun getScreenSizeFromApiLevel23(context: Context): Size {
        val display = context.getSystemService(WindowManager::class.java).defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getRealMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        return Size(metrics.widthPixels, metrics.heightPixels)
    }


    // This will return an Rect with the Gap on Top,left,right,bottom
    // this gap can be , status bar , navigation bar , or notch
    fun getDisplayInsets(view: View? = null, onRect: (Rect?) -> Unit) {
        if (view == null) onRect.invoke(null)
        view?.doOnAttach {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val systemBars: Insets? =
                    it.rootWindowInsets?.getInsetsIgnoringVisibility(
                        WindowInsets.Type.systemBars()
                    )
                val displayCutout: Insets? =
                    it.rootWindowInsets?.getInsetsIgnoringVisibility(
                        WindowInsets.Type.displayCutout()
                    )

                val systemTop = systemBars?.top ?: 0
                val systemBottom = systemBars?.bottom ?: 0
                val systemLeft = systemBars?.left ?: 0
                val systemRight = systemBars?.right ?: 0

                val cutoutTop = displayCutout?.top ?: 0
                val cutoutBottom = displayCutout?.bottom ?: 0
                val cutoutLeft = displayCutout?.left ?: 0
                val cutoutRight = displayCutout?.right ?: 0

                val top = if (systemTop != 0) systemTop else cutoutTop
                val bottom = if (systemBottom != 0) systemBottom else cutoutBottom
                val left = if (systemLeft != 0) systemLeft else cutoutLeft
                val right = if (systemRight != 0) systemRight else cutoutRight

                onRect.invoke(Rect(left, top, right, bottom))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val top = it.rootWindowInsets?.stableInsetTop
                val bottom = it.rootWindowInsets?.stableInsetBottom
                val left = it.rootWindowInsets?.stableInsetLeft
                val right = it.rootWindowInsets?.stableInsetRight
                onRect.invoke(Rect(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0))
            }
        }
    }

    // This will only work if cutout mode set before initializing gesturedeck
    fun isCutoutModeCovered(context: Context): Boolean? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val cutoutMode: Int =
                    (context as Activity).window.attributes.layoutInDisplayCutoutMode
                return cutoutMode == 1 || cutoutMode == 3
            } else {
                return null
            }
        } catch (_: Exception) {
            null
        }
    }

}

enum class OrientationMode {
    PORTRAIT,
    REVERSE_PORTRAIT,
    LANDSCAPE,
    REVERSE_LANDSCAPE,
}

