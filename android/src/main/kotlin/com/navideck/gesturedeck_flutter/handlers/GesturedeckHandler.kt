package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.view.MotionEvent
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.SwipeDirection
import com.navideck.gesturedeck_flutter.GestureActionConfig
import com.navideck.gesturedeck_flutter.GesturedeckCallback
import com.navideck.gesturedeck_flutter.GesturedeckChannel

internal class GesturedeckHandler(
    private val activity: Activity,
    private val gestureCallback: GesturedeckCallback? = null,
) : GesturedeckChannel {
    private var gesturedeck: Gesturedeck? = null

    fun onTouchEvent(event: MotionEvent) {
        gesturedeck?.onTouchEvent(event)
    }

    override fun initialize(
        androidActivationKey: String?,
        iOSActivationKey: String?,
        autoStart: Boolean,
        gestureActionConfig: GestureActionConfig,
    ) {
        gesturedeck = Gesturedeck(
            context = activity,
            activationKey = androidActivationKey,
            autoStart = autoStart,
            observingRootView = false,
            tapAction = gestureActionConfig.tapAction(),
            swipeLeftAction = gestureActionConfig.swipeLeftAction(),
            swipeRightAction = gestureActionConfig.swipeRightAction(),
            panAction = gestureActionConfig.panAction(),
            longPressAction = gestureActionConfig.longPressAction(),
        )
    }


    override fun start() {
        gesturedeck?.start()
    }

    override fun stop() {
        gesturedeck?.stop()
    }

    override fun updateActionConfig(gestureActionConfig: GestureActionConfig) {
        if (gestureActionConfig.enableTapAction != null) {
            gesturedeck?.tapAction = gestureActionConfig.tapAction()
        }
        if (gestureActionConfig.enableSwipeLeftAction != null) {
            gesturedeck?.swipeLeftAction = gestureActionConfig.swipeLeftAction()
        }
        if (gestureActionConfig.enableSwipeRightAction != null) {
            gesturedeck?.swipeRightAction = gestureActionConfig.swipeRightAction()
        }
        if (gestureActionConfig.enablePanAction != null) {
            gesturedeck?.panAction = gestureActionConfig.panAction()
        }
        if (gestureActionConfig.enableLongPressAction != null) {
            gesturedeck?.longPressAction = gestureActionConfig.longPressAction()
        }
    }

    private fun GestureActionConfig.tapAction(): (() -> Unit)? {
        return if (enableTapAction != false) {
            { gestureCallback?.onTap { } }
        } else null
    }

    private fun GestureActionConfig.swipeLeftAction(): (() -> Unit)? {
        return if (enableSwipeLeftAction != false) {
            { gestureCallback?.onSwipeLeft { } }
        } else null
    }

    private fun GestureActionConfig.swipeRightAction(): (() -> Unit)? {
        return if (enableSwipeRightAction != false) {
            { gestureCallback?.onSwipeRight { } }
        } else null
    }

    private fun GestureActionConfig.panAction(): ((MotionEvent, SwipeDirection, GestureState) -> Unit)? {
        return if (enablePanAction != false) {
            { _, _, _ -> gestureCallback?.onPan { } }
        } else null
    }

    private fun GestureActionConfig.longPressAction(): ((GestureState) -> Unit)? {
        return if (enableLongPressAction != false) {
            { gestureCallback?.onLongPress { } }
        } else null
    }
}
