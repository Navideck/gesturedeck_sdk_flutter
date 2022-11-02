package com.navideck.gesturedeckapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.navideck.gesturedeck_android.GesturedeckActivity

private const val TAG = "TestActivityData2"

class TestActivity2 : GesturedeckActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "TestActivity 2"

        val txtGestureEvent = this.findViewById<TextView>(R.id.txtGestureEvent)

        this.setGestureEventListener {
            Log.e(TAG, it.name)
            txtGestureEvent.text = it.name
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}