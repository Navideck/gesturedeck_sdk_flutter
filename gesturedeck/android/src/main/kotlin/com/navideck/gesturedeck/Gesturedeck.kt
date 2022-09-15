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
import com.mapbox.android.gestures.AndroidGesturesManager
import com.mapbox.android.gestures.ShoveGestureDetector
import com.mapbox.android.gestures.ShoveGestureDetector.SimpleOnShoveGestureListener
import com.mapbox.android.gestures.SidewaysShoveGestureDetector
import com.mapbox.android.gestures.SidewaysShoveGestureDetector.SimpleOnSidewaysShoveGestureListener
import com.mapbox.android.gestures.StandardGestureDetector.SimpleStandardOnGestureListener
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.android.gestures.StandardScaleGestureDetector.SimpleStandardOnScaleGestureListener
import java.util.*

class Gesturedeck(activity: Activity) {
    private lateinit var gestureCallback: (gestureEvent: GestureEvent) -> Unit
    constructor(activity: Activity, gestureCallbacks: (gestureEvent: GestureEvent) -> Unit) : this(activity) {
        this.gestureCallback = gestureCallbacks
    }

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

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onGestureEventReceived(GestureEvent.SingleTapConfirmed)
                return super.onSingleTapConfirmed(e)
            }

            override fun onLongPress(e: MotionEvent) {
                onGestureEventReceived(GestureEvent.LongPress)
                super.onLongPress(e)
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onGestureEventReceived(GestureEvent.SingleTapUp)
                return super.onSingleTapUp(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d("DoubleTap","DoubleTap")
                onGestureEventReceived(GestureEvent.DoubleTap)
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

        androidGesturesManager.setMultiFingerTapGestureListener { _, pointersCount ->
            if (pointersCount == 2) {
                Log.d("MultiFingerTap","2 Finger Tap")
                onGestureEventReceived(GestureEvent.TwoFingerTap)
            }
            true
        }

        androidGesturesManager.setShoveGestureListener(object : SimpleOnShoveGestureListener() {
            override fun onShove(
                detector: ShoveGestureDetector,
                deltaPixelsSinceLast: Float,
                deltaPixelsSinceStart: Float
            ): Boolean {
//                if(detector.deltaPixelsSinceStart < 0){
//                    onGestureEventReceived(GestureEvent.SWIPING_UP)
//                }else{
//                    onGestureEventReceived(GestureEvent.SWIPING_DOWN)
//                }
                return true
            }

            override fun onShoveBegin(detector: ShoveGestureDetector): Boolean {
                if(detector.deltaPixelsSinceStart < 0){
                    onGestureEventReceived(GestureEvent.SWIPING_UP)
                }else{
                    onGestureEventReceived(GestureEvent.SWIPING_DOWN)
                }
                return super.onShoveBegin(detector)
            }

            override fun onShoveEnd(
                detector: ShoveGestureDetector,
                velocityX: Float,
                velocityY: Float
            ) {
                if(detector.deltaPixelSinceLast < 0){
                    onGestureEventReceived(GestureEvent.SWIPED_UP)
                }else{
                    onGestureEventReceived(GestureEvent.SWIPED_DOWN)
                }
                super.onShoveEnd(detector, velocityX, velocityY)
            }
        })

        androidGesturesManager.sidewaysShoveGestureDetector.maxShoveAngle = 90F

        androidGesturesManager.setSidewaysShoveGestureListener(
            object : SimpleOnSidewaysShoveGestureListener() {
                override fun onSidewaysShove(
                    detector: SidewaysShoveGestureDetector, deltaPixelsSinceLast: Float,
                    deltaPixelsSinceStart: Float
                ): Boolean {
//                    if(detector.deltaPixelsSinceStart > 0){
//                        onGestureEventReceived(GestureEvent.SWIPING_RIGHT)
//                    }else{
//                        onGestureEventReceived(GestureEvent.SWIPING_LEFT);
//                    }
                    return true
                }

                override fun onSidewaysShoveBegin(detector: SidewaysShoveGestureDetector): Boolean {
                    if(detector.deltaPixelsSinceStart > 0){
                        onGestureEventReceived(GestureEvent.SWIPING_RIGHT)
                    }else{
                        onGestureEventReceived(GestureEvent.SWIPING_LEFT);
                    }
                    return super.onSidewaysShoveBegin(detector)
                }

                override fun onSidewaysShoveEnd(
                    detector: SidewaysShoveGestureDetector,
                    velocityX: Float,
                    velocityY: Float
                ) {
                    if(detector.deltaPixelSinceLast > 0){
                        onGestureEventReceived(GestureEvent.SWIPED_RIGHT)
                    }else{
                        onGestureEventReceived(GestureEvent.SWIPED_LEFT);
                    }
                    super.onSidewaysShoveEnd(detector, velocityX, velocityY)
                }
            }
        )
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return androidGesturesManager.onTouchEvent(event);
    }

    private var previousGestureEvent:GestureEvent?=null
    private fun onGestureEventReceived(gestureEvent: GestureEvent){
        // TODO: use previousGestureEvent to improve logic for recognizing gestureEvent
        gestureCallback.invoke(gestureEvent)

        if(gestureEvent== GestureEvent.SWIPED_LEFT ||gestureEvent== GestureEvent.SWIPED_RIGHT){
            animateOverlay()
        }
        // TODO: Improve logic to manage audio
        if(gestureEvent == GestureEvent.SWIPING_UP){
           audioManager.setMediaVolume( audioManager.mediaCurrentVolume+1)
        }
        if(gestureEvent == GestureEvent.SWIPING_DOWN){
            audioManager.setMediaVolume( audioManager.mediaCurrentVolume-1)
        }
    }

}


enum class GestureEvent {
    SWIPING_LEFT, SWIPED_LEFT, SWIPING_RIGHT, SWIPED_RIGHT, SWIPING_UP, SWIPED_UP, SWIPING_DOWN, SWIPED_DOWN,DoubleTap,TwoFingerTap,SingleTapUp,SingleTapConfirmed,LongPress
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

// TODO: Fade in screen on touch
// TODO: Fade out screen on release
// TODO: Implement Window.Callback interface https://developer.android.com/reference/android/view/Window.Callback instead of overriding dispatchTouchEvent