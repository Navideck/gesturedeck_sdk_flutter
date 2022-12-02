package com.navideck.gesturedeck_android.helper

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
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

    fun getLandscapeModeUncoveredSpace(context: Context): Int {
        val orientationMode: OrientationMode = this.getOrientationMode(context)
        val isLandscapeMode =
            orientationMode == OrientationMode.LANDSCAPE || orientationMode == OrientationMode.REVERSE_LANDSCAPE
        if (!isLandscapeMode) return 0

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cutoutSpace = landscapeCutoutWidthInApiLevel30(context)
            val navigationSpace = landscapeNavigationBarWidthInApiLevel30(context)
            return navigationSpace + cutoutSpace
        } else {
            val currentDisplay = windowManager.defaultDisplay
            val appUsableSize = Point()
            val realScreenSize = Point()
            currentDisplay?.apply {
                getSize(appUsableSize)
                getRealSize(realScreenSize)
            }
            return if (appUsableSize.x < realScreenSize.x) {
                realScreenSize.x - appUsableSize.x
            } else if (appUsableSize.y < realScreenSize.y) {
                realScreenSize.y - appUsableSize.y
            } else 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun landscapeCutoutWidthInApiLevel30(context: Context): Int {
        val orientationMode: OrientationMode = this.getOrientationMode(context)
        val isLandscapeMode =
            orientationMode == OrientationMode.LANDSCAPE || orientationMode == OrientationMode.REVERSE_LANDSCAPE
        if (!isLandscapeMode || orientationMode == OrientationMode.REVERSE_LANDSCAPE) return 0
        val isCutoutModeCovered: Boolean? = isCutoutModeCovered(context)
        if (isCutoutModeCovered == false) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val cutoutInsets = windowManager
                .currentWindowMetrics
                .windowInsets
                .getInsets(WindowInsets.Type.displayCutout())
            return if (cutoutInsets.left != 0) {
                cutoutInsets.left
            } else if (cutoutInsets.right != 0) {
                cutoutInsets.right
            } else 0
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun landscapeNavigationBarWidthInApiLevel30(context: Context): Int {
        val orientationMode: OrientationMode = this.getOrientationMode(context)
        val isLandscapeMode =
            orientationMode == OrientationMode.LANDSCAPE || orientationMode == OrientationMode.REVERSE_LANDSCAPE
        if (!isLandscapeMode) return 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val navigationBars = windowManager
            .currentWindowMetrics
            .windowInsets
            .getInsets(WindowInsets.Type.navigationBars())
        return if (navigationBars.left != 0) {
            navigationBars.left
        } else if (navigationBars.right != 0) {
            navigationBars.right
        } else 0
    }

    // This will only work if cutout mode set before initializing gesturedeck
    @RequiresApi(Build.VERSION_CODES.P)
    private fun isCutoutModeCovered(context: Context): Boolean? {
        return try {
            val cutoutMode: Int =
                (context as Activity).window.attributes.layoutInDisplayCutoutMode
            cutoutMode == 1 || cutoutMode == 3
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

