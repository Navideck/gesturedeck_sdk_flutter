package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.view.MotionEvent
import com.navideck.gesturedeck_android.Gesturedeck
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
            tapAction = if (gestureActionConfig.enableTapAction) {
                { gestureCallback?.onTap { } }
            } else null,
            swipeLeftAction = if (gestureActionConfig.enableSwipeLeftAction) {
                { gestureCallback?.onSwipeLeft { } }
            } else null,
            swipeRightAction = if (gestureActionConfig.enableSwipeRightAction) {
                { gestureCallback?.onSwipeRight { } }
            } else null,
            panAction = if (gestureActionConfig.enablePanAction) {
                { _, _, _ -> gestureCallback?.onPan { } }
            } else null,
            longPressAction = if (gestureActionConfig.enableLongPressAction) {
                { gestureCallback?.onLongPress { } }
            } else null,
        )
    }


    override fun start() {
        gesturedeck?.start()
    }

    override fun stop() {
        gesturedeck?.stop()
    }

}
