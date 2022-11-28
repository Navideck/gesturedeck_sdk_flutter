package com.navideck.gesturedeck_android.helper

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.*
import androidx.annotation.RequiresApi

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
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display
            } else {
                @Suppress("DEPRECATION")
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
}

enum class OrientationMode {
    PORTRAIT,
    REVERSE_PORTRAIT,
    LANDSCAPE,
    REVERSE_LANDSCAPE,
}

