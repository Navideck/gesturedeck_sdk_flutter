package com.navideck.gesturedeck_flutter

import android.app.Activity
import android.os.Build
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.model.GesturedeckEvent
import com.navideck.universal_volume.UniversalVolume
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.renderer.FlutterRenderer
import io.flutter.plugin.common.*
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** GesturedeckFlutterPlugin */
class GesturedeckFlutterPlugin : FlutterPlugin, EventChannel.StreamHandler, MethodCallHandler,
    ActivityAware {

    companion object {
        var instance: GesturedeckFlutterPlugin? = null
    }

    private var activityBinding: ActivityPluginBinding? = null
    private var touchEventSink: EventChannel.EventSink? = null
    private lateinit var channel: MethodChannel
    private lateinit var touchEventResult: EventChannel
    private lateinit var renderer: FlutterRenderer
    private var gesturedeck: Gesturedeck? = null
    private var universalVolume: UniversalVolume? = null

    /// It allows specifying the `UniversalVolume` instance, which can be used to share the same instance between
    /// multiple plugins. This can be useful to save on resources and also prevent unexpected behavior on devices
    /// that do not handle concurrency properly.
    fun setUniversalVolumeInstance(universalVolume: UniversalVolume) {
        this.universalVolume = universalVolume
    }

    private fun initGesturedeck(
        activity: Activity,
        activationKey: String?,
        reverseHorizontalSwipes: Boolean
    ) {
        gesturedeck = Gesturedeck(
            context = activity,
            universalVolume = universalVolume,
            activationKey = activationKey,
            bitmapCallback = { renderer.bitmap },
            autoStart = false,
            reverseHorizontalSwipes = reverseHorizontalSwipes,
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
        gesturedeck?.onTouchEvents(event)
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeck?.onKeyEvents(event) ?: false
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "com.navideck.gesturedeck.method")
        channel.setMethodCallHandler(this)
        touchEventResult =
            EventChannel(flutterPluginBinding.binaryMessenger, "com.navideck.gesturedeck")
        touchEventResult.setStreamHandler(this)
        instance = this
        renderer = flutterPluginBinding.flutterEngine.renderer
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        val activity: Activity? = activityBinding?.activity
        when (call.method) {
            "initialize" -> {
                val args = call.arguments as Map<*, *>
                val activationKey: String? = args["activationKey"] as String?
                val reverseHorizontalSwipes: Boolean =
                    args["reverseHorizontalSwipes"] as Boolean
                if (activity != null) {
                    initGesturedeck(activity, activationKey, reverseHorizontalSwipes)
                    result.success(null)
                } else {
                    result.error("ActivityError", "Null activity", null)
                }
            }

            "reverseHorizontalSwipes" -> {
                val args = call.arguments as Map<*, *>
                gesturedeck?.reverseHorizontalSwipes = args["value"] as Boolean
                result.success(null)
            }

            "start" -> {
                gesturedeck?.start()
                result.success(null)
            }

            "stop" -> {
                gesturedeck?.stop()
                result.success(null)
            }

            else -> {
                result.notImplemented()
            }
        }
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
            }
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activityBinding = binding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activityBinding = null
    }

    override fun onDetachedFromActivity() {
        activityBinding = null
    }
}