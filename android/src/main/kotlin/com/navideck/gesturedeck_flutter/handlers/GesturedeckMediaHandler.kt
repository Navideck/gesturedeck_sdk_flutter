package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.MotionEvent
import com.navideck.gesturedeck_android.gesturedeck_media.GesturedeckMedia
import com.navideck.gesturedeck_android.gesturedeck_media.GesturedeckMediaOverlay
import com.navideck.gesturedeck_android.model.PanSensitivity
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
        activationKey: String?,
        autoStart: Boolean,
        reverseHorizontalSwipes: Boolean,
        panSensitivity: Long?,
        overlayConfig: OverlayConfig?
    ) {
        var tintColor: Int? = null
        overlayConfig?.tintColor?.let {
            tintColor = Color.parseColor("#$it")
        }
        gesturedeckMedia = GesturedeckMedia(
            context = activity,
            reverseHorizontalSwipes = reverseHorizontalSwipes,
            activationKey = activationKey,
            autoStart = autoStart,
            panSensitivity = panSensitivity?.toPanSensitivity(),
            gesturedeckMediaOverlay = overlayConfig?.toGesturedeckMediaOverlay(),
            tapAction = {
                gesturedeckMediaCallback?.onTap { }
            },
            swipeLeftAction = {
                gesturedeckMediaCallback?.onSwipeRight { }
            },
            swipeRightAction = {
                gesturedeckMediaCallback?.onSwipeLeft { }
            },
            panAction = { _, _, _ ->
                gesturedeckMediaCallback?.onPan { }
            },
            longPressAction = {
                gesturedeckMediaCallback?.onLongPress { }
            }
        )

        universalVolume?.let {
            gesturedeckMedia?.setUniversalVolumeInstance(it)
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

    private fun argsToDrawable(args: Any?): Drawable? {
        if (args == null || args !is ByteArray) return null
        return BitmapDrawable(
            activity.resources,
            BitmapFactory.decodeByteArray(args, 0, args.size)
        )
    }

    private fun OverlayConfig.toGesturedeckMediaOverlay(): GesturedeckMediaOverlay {
        return GesturedeckMediaOverlay(
            activity = activity,
            tintColor = tintColor?.let { Color.parseColor("#$it") },
            overlayBackgroundColor = overlayBackgroundColor?.let { Color.parseColor("#$it") },
            iconTapToggled = argsToDrawable(iconTapToggled),
            iconSwipeLeft = argsToDrawable(iconSwipeLeft),
            iconSwipeRight = argsToDrawable(iconSwipeRight),
            topIcon = argsToDrawable(topIcon),
            bitmapCallback = { flutterRenderer.bitmap }
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

