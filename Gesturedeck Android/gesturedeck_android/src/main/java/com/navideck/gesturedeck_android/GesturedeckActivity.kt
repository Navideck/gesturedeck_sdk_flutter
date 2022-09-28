package com.navideck.gesturedeck_android

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.navideck.gesturedeck_android.gesturedeckVariants.GesturedeckMapbox
import com.navideck.gesturedeck_android.model.GestureEvent


open class GesturedeckActivity : AppCompatActivity() {

    private lateinit var gesturedeck: GesturedeckMapbox

    private var gestureCallbacks: ((gestureEvent: GestureEvent) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gesturedeck = GesturedeckMapbox(
            this,
            canUseRenderEffect = true,
            gestureCallbacks = { gestureCallbacks?.invoke(it) })
    }

    // To access GestureEvents in Activity which is overriding [GesturedeckActivity]
    fun setGestureEventListener(gestureListener: ((gestureEvent: GestureEvent) -> Unit)? = null) {
        this.gestureCallbacks = gestureListener
    }

    fun removeGestureEventListener() {
        this.gestureCallbacks = null
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        gesturedeck.onTouchEvents(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        // TODO : work more on Dispose Part
        gesturedeck.dispose()
        removeGestureEventListener()
        super.onDestroy()
    }
}