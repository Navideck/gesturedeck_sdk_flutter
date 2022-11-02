package com.navideck.gesturedeckapp


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.navideck.gesturedeck_android.Gesturedeck
import com.navideck.gesturedeck_android.helper.OverlayHelper
import com.navideck.gesturedeck_android.model.BackgroundMode
import com.navideck.gesturedeck_android.model.GestureEvent
import com.navideck.gesturedeck_android.model.GesturedeckEvent


private const val DEBUG_TAG = "Gestures"

class MainActivity : AppCompatActivity() {
    private lateinit var gestureText: TextView
    private lateinit var topLayout: LinearLayout
    private lateinit var dragView: View

    // Gesturedeck Variables
    private var gesturedeckMapbox: Gesturedeck? = null
    private var backgroundMode: BackgroundMode = BackgroundMode.BLUR
    private var blurSample = 5
    private var blurRadius = 25
    private var dimRadius = 100
    private var canUseRenderEffect: Boolean = false

    // Toggle Theme
    private var isDarkModeOn = true
    private var forceLightMode = false

    // Test Layout With Some Movable Background UI's
    private var enableDragViewMovement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // initialize UI Elements
        initialiseUI()

        // Gesturedeck SDK's
        initGesturedeck()
    }

    private fun initGesturedeck() {
        gesturedeckMapbox?.dispose()
        gesturedeckMapbox = Gesturedeck(
            this,
            // tintColor = Color.RED,
            gestureCallbacks = { gesture: GesturedeckEvent ->
                Log.e(DEBUG_TAG, gesture.name)
                gestureText.text = "GesturedeckMapbox : " + gesture.name
            }
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) gesturedeckMapbox?.onTouchEvents(ev);
        if (enableDragViewMovement) dragView(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event != null) return gesturedeckMapbox?.onKeyEvents(event) ?: false
        return false
    }

    private fun dragView(ev: MotionEvent?) {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {
                val statusBarHeight = 50
                val topLayoutHeight = topLayout.height + statusBarHeight
                dragView.x = ev.x
                dragView.y = ev.y - topLayoutHeight
            }
            else -> {}
        }
    }

    private fun initialiseUI() {
        gestureText = this.findViewById(R.id.txtGestureType)
        topLayout = this.findViewById(R.id.topLayout)
        dragView = this.findViewById(R.id.draggable_view);
        val btnTestOverlay: Button = this.findViewById(R.id.btnTestOverlay)
        val btnChangeTheme: Button = this.findViewById(R.id.btnChangeTheme)
        val btnDim: Button = this.findViewById(R.id.btnDIm)
        val btnBlur: Button = this.findViewById(R.id.btnBlur)
        val btnEnableScrollView: Button = this.findViewById(R.id.btnEnableScrollView)
        val blurSeekBar: SeekBar = this.findViewById(R.id.blurSeekBar)
        val blurSampleSeekbar: SeekBar = this.findViewById(R.id.blurSampleSeekbar)
        val btnAllowRenderEffect: Button = this.findViewById(R.id.btnAllowRenderEffect)
        val btnNavigateTest: Button = this.findViewById(R.id.btnNavigateTest)

        btnNavigateTest.setOnClickListener {
            val k = Intent(this@MainActivity, TestActivity::class.java)
            startActivity(k)
        }


        btnAllowRenderEffect.setOnClickListener {
            canUseRenderEffect = !canUseRenderEffect
            Toast.makeText(
                this@MainActivity,
                "Api12 RenderEffect : $canUseRenderEffect",
                Toast.LENGTH_SHORT
            ).show()
            initGesturedeck()
        }

        blurSampleSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                var progress = seek.progress
                if (progress == 0) progress = 1
                blurSample = progress
                Toast.makeText(this@MainActivity, "BlurSample $blurSample", Toast.LENGTH_SHORT)
                    .show()
                initGesturedeck()
            }
        })

        blurSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                var progress = seek.progress
                if (progress == 0) progress = 1
                blurRadius = progress
                dimRadius = progress + 100
                Toast.makeText(
                    this@MainActivity,
                    "BlurRadius $blurRadius | DimRadius $dimRadius",
                    Toast.LENGTH_SHORT
                ).show()
                initGesturedeck()
            }
        })

        btnEnableScrollView.setOnClickListener {
            if (enableDragViewMovement) {
                this.findViewById<ScrollView>(R.id.scrollView).visibility = View.GONE
                enableDragViewMovement = false
            } else {
                this.findViewById<ScrollView>(R.id.scrollView).visibility = View.VISIBLE
                enableDragViewMovement = true
            }
        }

        if (forceLightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        btnChangeTheme.setOnClickListener {
            if (forceLightMode) {
                Toast.makeText(this, "ForcedLightMode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                var mode = AppCompatDelegate.MODE_NIGHT_NO
                if (isDarkModeOn) mode = AppCompatDelegate.MODE_NIGHT_YES
                AppCompatDelegate.setDefaultNightMode(mode)
                isDarkModeOn = !isDarkModeOn
            }
        }

        btnTestOverlay.setOnClickListener {
            OverlayHelper(this).testOverlay()
        }

        btnDim.setOnClickListener {
            backgroundMode = BackgroundMode.DIM
            initGesturedeck()
            Toast.makeText(this, "BackgroundMode : DIM", Toast.LENGTH_SHORT).show()
        }
        btnBlur.setOnClickListener {
            backgroundMode = BackgroundMode.BLUR
            initGesturedeck()
            Toast.makeText(this, "BackgroundMode : BLUR", Toast.LENGTH_SHORT).show()
        }
    }

}

