package com.navideck.gesturedeck

import android.app.Activity
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.NonNull
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.model.GesturedeckEvent
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

    private var gesturedeck: Gesturedeck? = null

    private fun initGesturedeck(activity: Activity) {
        gesturedeck = Gesturedeck(
            context = activity,
            bitmapCallback = { renderer.bitmap },
            gestureCallbacks = {
                when (it) {
                    GesturedeckEvent.SWIPE_RIGHT -> {
                        touchEventSink?.success("swipedRight")
                    }
                    GesturedeckEvent.SWIPE_LEFT -> {
                        touchEventSink?.success("swipedLeft")
                    }
                    GesturedeckEvent.TAP -> {
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

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeck?.onKeyEvents(event) ?: false
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
