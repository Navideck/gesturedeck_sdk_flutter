package com.navideck.gesturedeckapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.navideck.gesturedeck_android.GesturedeckActivity

private const val TAG = "TestActivityData"

class TestActivity : GesturedeckActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "TestActivity"
        setContentView(R.layout.activity_test)

        var btnNavigate: Button = this.findViewById(R.id.btnNavigate)

        btnNavigate.setOnClickListener{
            startActivity(Intent(this@TestActivity, TestActivity2::class.java))
        }

        // To get All GestureEvents from `GesturedeckActivity`
        this.setGestureEventListener { Log.e(TAG, it.name) }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}