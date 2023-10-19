package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.navideck.gesturedeck_android.GesturedeckMedia
import com.navideck.gesturedeck_android.GesturedeckMediaOverlay
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.PanSensitivity
import com.navideck.gesturedeck_android.model.SwipeDirection
import com.navideck.gesturedeck_flutter.GestureActionConfig
import com.navideck.gesturedeck_flutter.GesturedeckMediaCallback
import com.navideck.gesturedeck_flutter.GesturedeckMediaChannel
import com.navideck.gesturedeck_flutter.OverlayConfig
import com.navideck.universal_volume.UniversalVolume
import io.flutter.embedding.engine.renderer.FlutterRenderer

internal class GesturedeckMediaHandler(
    private val activity: Activity,
    private val universalVolume: UniversalVolume? = null,
    private val gesturedeckMediaCallback: GesturedeckMediaCallback? = null,
    private val flutterRenderer: FlutterRenderer,
) : GesturedeckMediaChannel {
    private var gesturedeckMedia: GesturedeckMedia? = null

    fun onTouchEvent(event: MotionEvent) {
        gesturedeckMedia?.onTouchEvent(event)
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return gesturedeckMedia?.onKeyEvent(event) ?: false
    }

    override fun initialize(
        androidActivationKey: String?,
        iOSActivationKey: String?,
        autoStart: Boolean,
        reverseHorizontalSwipes: Boolean,
        panSensitivity: Long?,
        gestureActionConfig: GestureActionConfig,
        overlayConfig: OverlayConfig?,
    ) {
        gesturedeckMedia = GesturedeckMedia(
            context = activity,
            reverseHorizontalSwipes = reverseHorizontalSwipes,
            activationKey = androidActivationKey,
            autoStart = autoStart,
            panSensitivity = panSensitivity?.toPanSensitivity(),
            gesturedeckMediaOverlay = overlayConfig?.toGesturedeckMediaOverlay(),
            tapAction = gestureActionConfig.tapAction(),
            swipeLeftAction = gestureActionConfig.swipeLeftAction(),
            swipeRightAction = gestureActionConfig.swipeRightAction(),
            panAction = gestureActionConfig.panAction(),
            longPressAction = gestureActionConfig.longPressAction(),
        )
        universalVolume?.let {
            gesturedeckMedia?.setUniversalVolumeInstance(it)
        }
    }

    override fun updateActionConfig(gestureActionConfig: GestureActionConfig) {
        if (gestureActionConfig.enableTapAction != null) {
            gesturedeckMedia?.tapAction = gestureActionConfig.tapAction()
        }
        if (gestureActionConfig.enableSwipeLeftAction != null) {
            gesturedeckMedia?.swipeLeftAction = gestureActionConfig.swipeLeftAction()
        }
        if (gestureActionConfig.enableSwipeRightAction != null) {
            gesturedeckMedia?.swipeRightAction = gestureActionConfig.swipeRightAction()
        }
        if (gestureActionConfig.enablePanAction != null) {
            gesturedeckMedia?.panAction = gestureActionConfig.panAction()
        }
        if (gestureActionConfig.enableLongPressAction != null) {
            gesturedeckMedia?.longPressAction = gestureActionConfig.longPressAction()
        }
    }

    override fun dispose() {
        gesturedeckMedia?.dispose()
    }


    override fun start() {
        gesturedeckMedia?.start()
    }

    override fun stop() {
        gesturedeckMedia?.stop()
    }


    override fun reverseHorizontalSwipes(value: Boolean) {
        gesturedeckMedia?.reverseHorizontalSwipes = value
    }

    override fun setGesturedeckMediaOverlay(overlayConfig: OverlayConfig?) {
        gesturedeckMedia?.gesturedeckMediaOverlay?.dispose()
        gesturedeckMedia?.gesturedeckMediaOverlay = overlayConfig?.toGesturedeckMediaOverlay()
    }

    private fun GestureActionConfig.tapAction(): (() -> Unit)? {
        return if (enableTapAction != false) {
            { gesturedeckMediaCallback?.onTap { } }
        } else null
    }

    private fun GestureActionConfig.swipeLeftAction(): (() -> Unit)? {
        return if (enableSwipeLeftAction != false) {
            { gesturedeckMediaCallback?.onSwipeLeft { } }
        } else null
    }

    private fun GestureActionConfig.swipeRightAction(): (() -> Unit)? {
        return if (enableSwipeRightAction != false) {
            { gesturedeckMediaCallback?.onSwipeRight { } }
        } else null
    }

    private fun GestureActionConfig.panAction(): ((MotionEvent, SwipeDirection, GestureState) -> Unit)? {
        return if (enablePanAction != false) {
            { _, _, _ -> gesturedeckMediaCallback?.onPan { } }
        } else null
    }

    private fun GestureActionConfig.longPressAction(): ((GestureState) -> Unit)? {
        return if (enableLongPressAction != false) {
            { gesturedeckMediaCallback?.onLongPress { } }
        } else null
    }

    private fun OverlayConfig.toGesturedeckMediaOverlay(): GesturedeckMediaOverlay {
        val gesturedeckMediaOverlay = GesturedeckMediaOverlay(
            activity = activity,
            tintColor = tintColor?.let { Color.parseColor("#$it") },
            backgroundColor = backgroundColor?.let { Color.parseColor("#$it") },
            iconTapToggled = argsToDrawable(iconTapToggled),
            iconSwipeLeft = argsToDrawable(iconSwipeLeft),
            iconSwipeRight = argsToDrawable(iconSwipeRight),
            topIcon = argsToDrawable(topIcon),
        )
        gesturedeckMediaOverlay.setBitmapCallback { flutterRenderer.bitmap }
        return gesturedeckMediaOverlay
    }

    private fun argsToDrawable(args: Any?): Drawable? {
        if (args == null || args !is ByteArray) return null
        return BitmapDrawable(
            activity.resources,
            BitmapFactory.decodeByteArray(args, 0, args.size)
        )
    }

    private fun Long.toPanSensitivity(): PanSensitivity? {
        return when (this) {
            0L -> PanSensitivity.LOW
            1L -> PanSensitivity.MEDIUM
            2L -> PanSensitivity.HIGH
            else -> null
        }
    }
}

