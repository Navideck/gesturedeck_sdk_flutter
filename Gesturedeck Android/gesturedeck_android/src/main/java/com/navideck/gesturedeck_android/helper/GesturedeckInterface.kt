package com.navideck.gesturedeck_android.helper


import android.view.MotionEvent
import com.navideck.gesturedeck_android.model.GestureEvent
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.SwipeDirection


interface GesturedeckInterface {
     fun onGestureEvent(gestureEvent: GestureEvent)

     fun onSwipeGestureAction(swipeDirection: SwipeDirection)

     fun onScaleGestureAction(
        event: MotionEvent,
        swipeDirection: SwipeDirection,
        state: GestureState
    )

     fun onPanGestureAction(
        event: MotionEvent,
        swipeDirection: SwipeDirection,
        state: GestureState
    )
}
