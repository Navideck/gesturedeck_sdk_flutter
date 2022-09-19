package com.navideck.gesturedeck

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.navideck.gesturedeck.gesturedeckVariants.GesturedeckMapbox
import com.navideck.gesturedeck.helper.OverlayHelper
import com.navideck.gesturedeck.model.GestureEvent

private const val DEBUG_TAG = "Gestures"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var gestureText : TextView=  this.findViewById(R.id.txtGestureType)
        var btnTestOverlay : Button =  this.findViewById(R.id.btnTestOverlay)

        btnTestOverlay.setOnClickListener{OverlayHelper(this).testOverlay()}

        //  GesturedeckIOS(this) { gesture: GestureEvent ->
        //      Log.e(DEBUG_TAG,gesture.name)
        //      gestureText.text = "GesturedeckIOS : "+gesture.name
        //  }.start()

        GesturedeckMapbox(this) { gesture: GestureEvent ->
            Log.e(DEBUG_TAG,gesture.name)
             gestureText.text = "GesturedeckMapbox : "+gesture.name
        }

    }

}

