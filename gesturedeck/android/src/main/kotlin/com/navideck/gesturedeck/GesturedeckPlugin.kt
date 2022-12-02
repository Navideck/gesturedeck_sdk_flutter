package com.navideck.gesturedeck

import android.app.Activity
import android.os.Build
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.model.GesturedeckEvent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.renderer.FlutterRenderer
import io.flutter.plugin.common.*
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** GesturedeckPlugin */
class GesturedeckPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
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
            gestureCallbacks = { event ->
                when (event) {
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

    fun dispatchTouchEvent(event: MotionEvent, activity: Activity) {
        if (touchEventSink == null) return
        if (gesturedeck == null) initGesturedeck(activity)
        gesturedeck?.onTouchEvents(event)
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeck?.onKeyEvents(event) ?: false
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(
            flutterPluginBinding.binaryMessenger,
            "com.navideck.gesturedeck.method"
        )
        channel.setMethodCallHandler(this)
        touchEventResult =
            EventChannel(flutterPluginBinding.binaryMessenger, "com.navideck.gesturedeck")
        touchEventResult.setStreamHandler(this)
        instance = this
        renderer = flutterPluginBinding.flutterEngine.renderer
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        result.notImplemented()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        touchEventResult.setStreamHandler(null)
        instance = null
    }


    /// Call this method in onCreate of MainActivity, to extend the app's UI around the notch
    fun extendAroundNotch(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
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
            "touchEvent" -> {
                touchEventSink = null
                gesturedeck?.dispose()
                gesturedeck = null
            }
        }
    }
}
