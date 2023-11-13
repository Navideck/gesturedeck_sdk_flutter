package com.navideck.gesturedeck_flutter

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import androidx.core.view.allViews
import com.navideck.gesturedeck_flutter.handlers.GesturedeckHandler
import com.navideck.gesturedeck_flutter.handlers.GesturedeckMediaHandler
import com.navideck.universal_volume.UniversalVolume
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener


class GesturedeckFlutterPlugin : FlutterPlugin, ActivityAware {

    companion object {
        var instance: GesturedeckFlutterPlugin? = null
    }

    private lateinit var flutterPluginBinding: FlutterPluginBinding
    private var activityBinding: ActivityPluginBinding? = null
    private var universalVolume: UniversalVolume? = null
    private var gesturedeckHandler: GesturedeckHandler? = null
    private var gesturedeckMediaHandler: GesturedeckMediaHandler? = null

    /// It allows specifying the `UniversalVolume` instance, which can be used to share the same instance between
    /// multiple plugins. This can be useful to save on resources and also prevent unexpected behavior on devices
    /// that do not handle concurrency properly.
    fun setUniversalVolumeInstance(universalVolume: UniversalVolume) {
        this.universalVolume = universalVolume
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return gesturedeckMediaHandler?.onKeyEvent(event) ?: false
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activityBinding = binding
        val activity = binding.activity
        val flutterRender = flutterPluginBinding.flutterEngine.renderer
        flutterRender.addIsDisplayingFlutterUiListener(flutterUiListener)

        // Set up Gesturedeck
        gesturedeckHandler = GesturedeckHandler(
            activity = activity,
            gestureCallback = GesturedeckCallback(flutterPluginBinding.binaryMessenger)
        )
        gesturedeckMediaHandler = GesturedeckMediaHandler(
            activity = activity,
            universalVolume = universalVolume,
            flutterRenderer = flutterRender,
            gesturedeckMediaCallback = GesturedeckMediaCallback(flutterPluginBinding.binaryMessenger),
        )
        GesturedeckChannel.setUp(
            flutterPluginBinding.binaryMessenger,
            gesturedeckHandler
        )
        GesturedeckMediaChannel.setUp(
            flutterPluginBinding.binaryMessenger,
            gesturedeckMediaHandler
        )
    }


    private val flutterUiListener = object : FlutterUiDisplayListener {
        override fun onFlutterUiDisplayed() {
            val activity = activityBinding?.activity ?: return
            var flutterView: FlutterView? = null
            activity.window.decorView.rootView.allViews.forEach {
                if (it is FlutterView) flutterView = it
            }
            if (flutterView == null) throw Exception("FlutterView not found")
            // Set up touch listeners
            flutterView?.setOnTouchListener { v, event ->
                gesturedeckMediaHandler?.onTouchEvent(event)
                gesturedeckHandler?.onTouchEvent(event)
                v.performClick()
                false
            }
        }

        override fun onFlutterUiNoLongerDisplayed() {}
    }


    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        instance = this
        this.flutterPluginBinding = flutterPluginBinding
    }

    /// Call this method in onCreate of MainActivity, to extend the app's UI around the notch
    fun extendAroundNotch(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        instance = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onDetachedFromActivity() {}
}

