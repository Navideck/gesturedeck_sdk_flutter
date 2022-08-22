package com.navideck.gesturedeck

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.navideck.gesturedeck.R


private const val DEBUG_TAG = "Gestures"

class MainActivity : GesturedeckActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = this.findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            Log.d(DEBUG_TAG, "Button tapped")
        }
    }
}