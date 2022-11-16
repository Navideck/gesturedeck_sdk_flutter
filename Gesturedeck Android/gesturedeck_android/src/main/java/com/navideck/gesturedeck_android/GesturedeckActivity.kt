package com.navideck.gesturedeck_android

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.navideck.gesturedeck_android.model.GesturedeckEvent

open class GesturedeckActivity : AppCompatActivity() {

    private lateinit var gesturedeck: Gesturedeck

    private var gesturedeckCallbacks: ((gesturedeckEvent: GesturedeckEvent) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gesturedeck = Gesturedeck(this) { gesturedeckCallbacks?.invoke(it) }
    }

    // To access GestureEvents in Activity which is overriding [GesturedeckActivity]
    fun setGestureEventListener(gestureListener: ((gesturedeckEvent: GesturedeckEvent) -> Unit)? = null) {
        this.gesturedeckCallbacks = gestureListener
    }

    fun removeGestureEventListener() {
        this.gesturedeckCallbacks = null
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