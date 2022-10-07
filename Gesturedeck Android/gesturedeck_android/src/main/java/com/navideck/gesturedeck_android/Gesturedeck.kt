package com.navideck.gesturedeck_android

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.navideck.gesturedeck_android.engine.GesturedeckMapboxEngine
import com.navideck.gesturedeck_android.globalActivity.GlobalApplication
import com.navideck.gesturedeck_android.helper.GesturedeckInterface
import com.navideck.gesturedeck_android.helper.KeyEventTimer
import com.navideck.gesturedeck_android.helper.OverlayHelper
import com.navideck.gesturedeck_android.model.*

private const val TAG = "GesturedeckMapbox"

class Gesturedeck(
    activity: Activity? = null,
    tintColor: Int? = null,
    volumeIconDrawable: Drawable? = null,
    iconSwipeLeftDrawable: Drawable? = null,
    iconSwipeRightDrawable: Drawable? = null,
    iconTapDrawable: Drawable? = null,
    iconTapToggledDrawable: Drawable? = null,
    bitmapCallback: (() -> Bitmap?)? = null,
    var gestureCallbacks: ((gestureEvent: GestureEvent) -> Unit)? = null,
) {
    private var overlayHelper: OverlayHelper
    private var previousKeyEvent: KeyEvent? = null
    private var keyEventTimer: KeyEventTimer = KeyEventTimer()
    private var gesturedeckMapboxEngine: GesturedeckMapboxEngine? = null


    init {
        var currentActivity: Activity? = activity ?: GlobalApplication.currentActivity()
        if (currentActivity != null) {
            overlayHelper = OverlayHelper(
                currentActivity,
                bitmapCallback,
                tintColor,
                volumeIconDrawable,
                iconSwipeLeftDrawable,
                iconSwipeRightDrawable,
                iconTapDrawable,
                iconTapToggledDrawable,
            )

            val gesturedeckInterface: GesturedeckInterface = getGesturedeckInterface()

            gesturedeckMapboxEngine = GesturedeckMapboxEngine(currentActivity, gesturedeckInterface)

            //TODO : Implement without Mapbox
            // GesturedeckEngine(activity,gesturedeckInterface)
        } else {
            throw Exception("Either pass activity in constructor or add application name (check docs )")
        }
    }

    fun onTouchEvents(event: MotionEvent) {
        gesturedeckMapboxEngine?.onTouchEvents(event)
    }

    fun dispose() {
        this.gestureCallbacks = null
        overlayHelper.dispose()
    }

    fun onKeyEvents(event: KeyEvent): Boolean {
        val swipeDirection: SwipeDirection? = when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                SwipeDirection.DOWN
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                SwipeDirection.UP
            }
            else -> null
        }

        var gestureState: GestureState? = when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.repeatCount == 0) {
                    GestureState.BEGAN
                } else {
                    GestureState.CHANGED
                }
            }
            KeyEvent.ACTION_UP -> {
                GestureState.ENDED
            }
            else -> {
                null
            }
        }

        if (gestureState == null || swipeDirection == null) return false

        if (gestureState == GestureState.BEGAN && keyEventTimer.isActive) {
            gestureState = GestureState.CHANGED
        }

        val isSingleKeyPressed = gestureState == GestureState.ENDED &&
                previousKeyEvent?.action == KeyEvent.ACTION_DOWN && previousKeyEvent?.repeatCount == 0
        if (isSingleKeyPressed) {
            keyEventTimer.start(duration = 500) {
                overlayHelper.updateVolumeViewFromKeyEvents(
                    gestureState, swipeDirection
                )
            }
        } else {
            keyEventTimer.cancel()
            overlayHelper.updateVolumeViewFromKeyEvents(gestureState, swipeDirection)
        }

        previousKeyEvent = event
        return true
    }

    private fun getGesturedeckInterface(): GesturedeckInterface {
        return object : GesturedeckInterface {
            override fun onGestureEvent(gestureEvent: GestureEvent) {
                var recognizedGestureEvent = gestureEvent
                when (gestureEvent) {
                    GestureEvent.TWO_FINGER_TAP -> {
                        overlayHelper.onTwoFingerTap()
                    }
                    GestureEvent.DOUBLE_TAP_LIFT -> {
                        overlayHelper.onTwoFingerTap()
                        recognizedGestureEvent = GestureEvent.TWO_FINGER_TAP
                    }
                    GestureEvent.DOUBLE_TAP_HOLD -> {
                        overlayHelper.showEmptyBlurView()
                    }
                    else -> {}
                }
                gestureCallbacks?.invoke(recognizedGestureEvent)
            }

            override fun onSwipeGestureAction(swipeDirection: SwipeDirection) {
                when (swipeDirection) {
                    SwipeDirection.LEFT -> {
                        overlayHelper.showSwipeLeft()
                    }
                    SwipeDirection.RIGHT -> {
                        overlayHelper.showSwipeRight()
                    }
                    else -> {}
                }
            }

            override fun onScaleGestureAction(
                event: MotionEvent,
                swipeDirection: SwipeDirection,
                state: GestureState
            ) {
                overlayHelper.updateVolumeView(state, event, swipeDirection)
            }

            override fun onPanGestureAction(
                event: MotionEvent,
                swipeDirection: SwipeDirection,
                state: GestureState
            ) {
                overlayHelper.updateVolumeView(state, event, swipeDirection)
            }
        }
    }
}

