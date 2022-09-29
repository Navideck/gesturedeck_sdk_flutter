package com.navideck.gesturedeck

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import io.flutter.embedding.engine.renderer.FlutterRenderer
import java.io.File
import java.io.FileOutputStream

// TODO : Delete this class Later after Testing
class FlutterScreenshot {

    // This Method will take Screenshot of Current Flutter Screen and Return Screenshot Path
    fun takeScreenshot(activity: Activity, renderer: FlutterRenderer): String? {
        Log.e("FlutterScreenshot", "Trying to take screenshot")
        try {
            val view = activity.window.decorView.rootView

            // Get Bitmap using Pixel Type 1
            getBitmapFromView(view, activity) {
                val path: String? = writeBitmap(it, activity)
                Log.e("FlutterScreenshot", "ScreenShot Saved to : $path")
            }

            // Get Bitmap using Pixel Type 2
            takeScreenshot(activity) {
                val path: String? = writeBitmap(it, activity, "test4")
                Log.e("FlutterScreenshot", "ScreenShot Saved to : $path")
            }

            // Get Bitmap Using renderer
            var bitmap: Bitmap = renderer.bitmap

            val path: String? = writeBitmap(bitmap, activity, "test2")

            if (path != null) {
                Log.e("FlutterScreenshot", "ScreenShot Saved to : $path")
                return path
            }
            return null
        } catch (ex: Exception) {
            Log.e("FlutterScreenshot", "Error taking screenshot: " + ex.message)
            return null
        }
    }

    private fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PixelCopy.request(
                        window,
                        Rect(
                            locationOfViewInWindow[0],
                            locationOfViewInWindow[1],
                            locationOfViewInWindow[0] + view.width,
                            locationOfViewInWindow[1] + view.height
                        ), bitmap, { copyResult ->
                            if (copyResult == PixelCopy.SUCCESS) {
                                callback(bitmap)
                            }
                        },
                        Handler()
                    )
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    private fun takeScreenshot(activity: Activity, callback: (Bitmap) -> Unit) {
        val window: Window = activity.window
        val view: View = window.decorView
        val bitmap: Bitmap
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val location = IntArray(2)
                view.getLocationInWindow(location)
                PixelCopy.request(
                    window, Rect(
                        location[0],
                        location[1], location[0] + view.width, location[1] + view.height
                    ), bitmap,
                    {
                        if (it == 0) {
                            callback(bitmap)
                        }
                    }, Handler(Looper.getMainLooper())
                )
            } else {
                bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                canvas.setBitmap(null as Bitmap?)
                // Intrinsics.checkNotNullExpressionValue(bitmap, "tBitmap")
                callback(bitmap)
            }
        } catch (e: java.lang.Exception) {
            Log.e("takeScreenshot", e.message!!)
        }
    }

    private fun writeBitmap(
        bitmap: Bitmap,
        activity: Activity,
        fileName: String = "test"
    ): String? {
        return try {
            val pathTemporary: String = activity.cacheDir.path
            val path = "$pathTemporary/$fileName.png"
            val imageFile = File(path)
            val oStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream)
            oStream.flush()
            oStream.close()
            path
        } catch (ex: Exception) {
            Log.e("FlutterScreenshot", "Error writing bitmap: " + ex.message)
            null
        }
    }

}