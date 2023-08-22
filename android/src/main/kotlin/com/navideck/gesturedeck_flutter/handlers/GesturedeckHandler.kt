package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.view.MotionEvent
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_flutter.GesturedeckCallback
import com.navideck.gesturedeck_flutter.GesturedeckChannel

internal class GesturedeckHandler(
    private val activity: Activity,
    private val gestureCallback: GesturedeckCallback? = null
) : GesturedeckChannel {
    var gesturedeck: Gesturedeck? = null

    fun onTouchEvent(event: MotionEvent) {
        gesturedeck?.onTouchEvent(event)
    }

    override fun initialize(
        androidActivationKey: String?,
        iOSActivationKey: String?,
        autoStart: Boolean
    ) {
        gesturedeck = Gesturedeck(
            context = activity,
            activationKey = androidActivationKey,
            autoStart = autoStart,
            tapAction = {
                gestureCallback?.onTap { }
            },
            swipeLeftAction = {
                gestureCallback?.onSwipeRight { }
            },
            swipeRightAction = {
                gestureCallback?.onSwipeLeft { }
            },
            panAction = { _, _, _ ->
                gestureCallback?.onPan { }
            },
            longPressAction = {
                gestureCallback?.onLongPress { }
            }
        )
    }


    override fun start() {
        gesturedeck?.start()
    }

    override fun stop() {
        gesturedeck?.stop()
    }

}
