package com.navideck.gesturedeck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.media.AudioManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.gestures.*
import com.mapbox.android.gestures.ShoveGestureDetector.SimpleOnShoveGestureListener
import com.mapbox.android.gestures.SidewaysShoveGestureDetector.SimpleOnSidewaysShoveGestureListener
import com.mapbox.android.gestures.StandardGestureDetector.SimpleStandardOnGestureListener
import com.mapbox.android.gestures.StandardScaleGestureDetector.SimpleStandardOnScaleGestureListener
import java.util.*

class Gesturedeck(activity: Activity) {
    lateinit var androidGesturesManager: AndroidGesturesManager
    private lateinit var overlayView: View
    private lateinit var audioManager: AudioManager

    init {
        configureOverlay(activity)
        setupGesturesManager(activity)
    }

    private fun configureOverlay(activity: Activity) {
        var container = activity.window.decorView.rootView as ViewGroup

        overlayView = activity.layoutInflater.inflate(R.layout.sample_overlay_view, null)
        measureAndLayout(activity, overlayView, 0)
        overlayView.visibility = View.GONE
        container.overlay.add(overlayView)
        audioManager = activity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
    }

    private fun animateOverlay() {
        overlayView.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        overlayView.visibility = View.GONE
                    }
                })
        }
    }

    private fun measureAndLayout(activity: Activity, toMeasure: View, statusBarHeight: Int) {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        toMeasure.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels - statusBarHeight,
                View.MeasureSpec.EXACTLY
            )
        )
        toMeasure.layout(
            0,
            statusBarHeight,
            displayMetrics.widthPixels,
            displayMetrics.heightPixels
        )
    }

    private fun setupGesturesManager(activity: Activity) {
        androidGesturesManager = AndroidGesturesManager(activity)

        androidGesturesManager.setStandardScaleGestureListener(
            object : SimpleStandardOnScaleGestureListener() {
                override fun onScale(detector: StandardScaleGestureDetector): Boolean {
                    if (detector.pointersCount > 1) { return true }
                    Log.d("Scale","Scale")

                    return true
                }
            })

        androidGesturesManager.setStandardGestureListener(object :
            SimpleStandardOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d("DoubleTap","DoubleTap")
                animateOverlay()

                // Get the maximum media/music volume
                val maxVolume = audioManager.mediaMaxVolume

                // Get a random volume index in a range
                val randomIndex = Random().nextInt(((maxVolume - 0) + 1) + 0)

                // Set the media/music volume programmatically
//                audioManager.setMediaVolume(maxVolume)
                audioManager.setMediaVolume(randomIndex)
//                audioManager.adjustVolume(AudioManager.ADJUST_SAME, 0)

                return true
            }
        })

        androidGesturesManager.setMultiFingerTapGestureListener { detector, pointersCount ->
            if (pointersCount == 2) {
                Log.d("MultiFingerTap","2 Finger Tap")
            }
            true
        }

        androidGesturesManager.setShoveGestureListener(object : SimpleOnShoveGestureListener() {
            override fun onShove(
                detector: ShoveGestureDetector,
                deltaPixelsSinceLast: Float,
                deltaPixelsSinceStart: Float
            ): Boolean {
                Log.d("onShove","onShove")
                return true
            }
        })
        androidGesturesManager.sidewaysShoveGestureDetector.maxShoveAngle = 90F

        androidGesturesManager.setSidewaysShoveGestureListener(
            object : SimpleOnSidewaysShoveGestureListener() {
                override fun onSidewaysShove(
                    detector: SidewaysShoveGestureDetector, deltaPixelsSinceLast: Float,
                    deltaPixelsSinceStart: Float
                ): Boolean {
                    Log.d("onSidewaysShove", deltaPixelsSinceStart.toString())
                    return true
                }
            }
        )
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return androidGesturesManager.onTouchEvent(event);
    }
}

// Extension function to change media volume programmatically
fun AudioManager.setMediaVolume(volumeIndex:Int) {
    // Set media volume level
    this.setStreamVolume(
        AudioManager.STREAM_MUSIC, // Stream type
        volumeIndex, // Volume index
        0// Flags
    )
}


// Extension property to get media maximum volume index
val AudioManager.mediaMaxVolume:Int
    get() = this.getStreamMaxVolume(AudioManager.STREAM_MUSIC)


// Extension property to get media/music current volume index
val AudioManager.mediaCurrentVolume:Int
    get() = this.getStreamVolume(AudioManager.STREAM_MUSIC)

// TODO Fade in screen on touch
// TODO Fade out screen on release
// TODO Implement Window.Callback interface https://developer.android.com/reference/android/view/Window.Callback instead of overriding dispatchTouchEvent