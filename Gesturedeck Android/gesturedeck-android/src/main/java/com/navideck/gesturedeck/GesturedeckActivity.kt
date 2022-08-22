package com.navideck.gesturedeck

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

open class GesturedeckActivity: AppCompatActivity() {

    private lateinit var gesturedeck: Gesturedeck

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gesturedeck = Gesturedeck(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        gesturedeck.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}