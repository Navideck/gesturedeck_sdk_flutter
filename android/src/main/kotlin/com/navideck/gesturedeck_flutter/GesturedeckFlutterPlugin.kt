package com.navideck.gesturedeck_flutter

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.GesturedeckMedia
import com.navideck.gesturedeck_android.GesturedeckMediaOverlay
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
    private var gesturedeckMedia: GesturedeckMedia? = null
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
        autoStart: Boolean,
        reverseHorizontalSwipes: Boolean,
        enableGesturedeckMedia: Boolean,
        overlayConfig: Map<*, *>,
    ) {
        if (enableGesturedeckMedia) {
            var tintColor: Int? = null
            overlayConfig["tintColor"]?.let {
                tintColor = Color.parseColor("#$it")
            }
            gesturedeckMedia = GesturedeckMedia(
                context = activity,
                reverseHorizontalSwipes = reverseHorizontalSwipes,
                activationKey = activationKey,
                autoStart = autoStart,
                gesturedeckMediaOverlay = GesturedeckMediaOverlay(
                    activity = activity,
                    tintColor = tintColor,
                    iconTap = argsToDrawable(overlayConfig["iconTap"]),
                    iconTapToggled = argsToDrawable(overlayConfig["iconTapToggled"]),
                    iconSwipeLeft = argsToDrawable(overlayConfig["iconSwipeLeft"]),
                    iconSwipeRight = argsToDrawable(overlayConfig["iconSwipeRight"]),
                    topIcon = argsToDrawable(overlayConfig["topIcon"]),
                    bitmapCallback = { renderer.bitmap },
                ),
                tapAction = {
                    touchEventSink?.success(GestureAction.TAP.value)
                },
                swipeRightAction = {
                    touchEventSink?.success(GestureAction.SWIPE_RIGHT.value)
                },
                swipeLeftAction = {
                    touchEventSink?.success(GestureAction.SWIPE_LEFT.value)
                },
            )
            universalVolume?.let {
                gesturedeckMedia?.setUniversalVolumeInstance(it)
            }
            gesturedeck = null
        } else {
            gesturedeck = Gesturedeck(
                context = activity,
                autoStart = autoStart,
                activationKey = activationKey,
                tapAction = {
                    touchEventSink?.success(GestureAction.TAP.value)
                },
                swipeRightAction = {
                    touchEventSink?.success(GestureAction.SWIPE_RIGHT.value)
                },
                swipeLeftAction = {
                    touchEventSink?.success(GestureAction.SWIPE_LEFT.value)
                },
            )
            gesturedeckMedia?.dispose()
            gesturedeckMedia = null
        }
    }

    private fun argsToDrawable(args: Any?): Drawable? {
        if (args == null || args !is ByteArray) return null
        return BitmapDrawable(
            activityBinding?.activity?.resources,
            BitmapFactory.decodeByteArray(args, 0, args.size)
        )
    }

    fun dispatchTouchEvent(event: MotionEvent, activity: Activity) {
        gesturedeckMedia?.onTouchEvent(event)
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeckMedia?.onKeyEvent(event) ?: false
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
                val reverseHorizontalSwipes = args["reverseHorizontalSwipes"] as Boolean
                val enableGesturedeckMedia = args["enableGesturedeckMedia"] as Boolean
                val autoStart = args["autoStart"] as Boolean
                val overlayConfig = args["overlayConfig"] as Map<*, *>? ?: mapOf<String, Any>()
                if (activity != null) {
                    initGesturedeck(
                        activity = activity,
                        activationKey = activationKey,
                        autoStart = autoStart,
                        reverseHorizontalSwipes = reverseHorizontalSwipes,
                        enableGesturedeckMedia = enableGesturedeckMedia,
                        overlayConfig = overlayConfig
                    )
                    result.success(null)
                } else {
                    result.error("ActivityError", "Null activity", null)
                }
            }
            // Only supported in GesturedeckMedia
            "reverseHorizontalSwipes" -> {
                val args = call.arguments as Map<*, *>
                gesturedeckMedia?.reverseHorizontalSwipes = args["value"] as Boolean
                result.success(null)
            }

            "start" -> {
                gesturedeck?.start()
                gesturedeckMedia?.start()
                result.success(null)
            }

            "stop" -> {
                gesturedeck?.stop()
                gesturedeckMedia?.stop()
                result.success(null)
            }

            "dispose" -> {
                gesturedeckMedia?.dispose()
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

enum class GestureAction(val value: String) {
    SWIPE_RIGHT("swipedRight"),
    SWIPE_LEFT("swipedLeft"),
    TAP("tap"),
    PAN("pan"),
}