package com.navideck.gesturedeck

import android.app.Activity
import android.view.MotionEvent
import androidx.annotation.NonNull
import com.navideck.gesturedeck_android.gesturedeckVariants.GesturedeckMapbox
import com.navideck.gesturedeck_android.model.BackgroundMode
import com.navideck.gesturedeck_android.model.GestureEvent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.renderer.FlutterRenderer
import io.flutter.plugin.common.*
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** GesturedeckPlugin */
class GesturedeckPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler,
    ActivityAware {
    companion object {
        var instance: GesturedeckPlugin? = null
    }

    private var touchEventSink: EventChannel.EventSink? = null
    private lateinit var channel: MethodChannel
    private lateinit var touchEventResult: EventChannel
    private lateinit var renderer: FlutterRenderer

    private var gesturedeck: GesturedeckMapbox? = null

    private fun initGesturedeck(activity: Activity) {
        gesturedeck = GesturedeckMapbox(
            activity,
            canUseRenderEffect = false,
            backgroundMode = BackgroundMode.BLUR,
            setBitmapUpdater = {
                // Return the Latest Bitmap of Flutter View , to Pass in Blur View
                // Ignore for Dim View
                renderer.bitmap
            },
            gestureCallbacks = {
                when (it) {
                    GestureEvent.SWIPE_RIGHT -> {
                        touchEventSink?.success("swipedRight")
                    }
                    GestureEvent.SWIPE_LEFT -> {
                        touchEventSink?.success("swipedLeft")
                    }
                    GestureEvent.TWO_FINGER_TAP -> {
                        touchEventSink?.success("tap")
                    }
                    else -> {}
                }
            }
        )
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        initGesturedeck(binding.activity)
    }

    fun dispatchTouchEvent(event: MotionEvent, activity: Activity) {
        gesturedeck?.onTouchEvents(event)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gesturedeck")
        channel.setMethodCallHandler(this)
        touchEventResult =
            EventChannel(flutterPluginBinding.binaryMessenger, "com.navideck.gesturedeck")
        touchEventResult.setStreamHandler(this)
        instance = this
        renderer = flutterPluginBinding.flutterEngine.renderer
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        result.notImplemented()
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        touchEventResult.setStreamHandler(null)
        instance = null
    }


    override fun onListen(args: Any?, eventSink: EventChannel.EventSink?) {
        val map = args as? Map<*, *> ?: return
        when (map["name"]) {
            "touchEvent" -> {
                touchEventSink = eventSink
            }
        }
    }

    override fun onCancel(args: Any?) {
        val map = args as? Map<*, *> ?: return
        when (map["name"]) {
            "touchEvent" -> touchEventSink = null
        }
    }


    // TODO : implement all theses
    override fun onDetachedFromActivity() {}
    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
}
