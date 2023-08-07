package com.navideck.gesturedeck_flutter.handlers

import android.app.Activity
import android.view.MotionEvent
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_flutter.GesturedeckCallback
import com.navideck.gesturedeck_flutter.GesturedeckFlutter

internal class GesturedeckHandler(
    private val activity: Activity,
    private val gestureCallback: GesturedeckCallback? = null
) : GesturedeckFlutter {
    var gesturedeck: Gesturedeck? = null

    fun onTouchEvent(event: MotionEvent) {
        gesturedeck?.onTouchEvent(event)
    }

    override fun initialize(activationKey: String?, autoStart: Boolean) {
        gesturedeck = Gesturedeck(
            context = activity,
            activationKey = activationKey,
            autoStart = autoStart,
            tapAction = {
                gestureCallback?.onTap { }
            },
            swipeRightAction = {
                gestureCallback?.onSwipeLeft { }
            },
            swipeLeftAction = {
                gestureCallback?.onSwipeRight { }
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
