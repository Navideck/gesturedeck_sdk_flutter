package com.navideck.gesturedeck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.media.AudioManager
import android.media.AudioManager.FLAG_SHOW_UI
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
import java.lang.Math.round
import java.util.*
import kotlin.math.roundToInt

class GesturedeckMapbox(activity: Activity) {
    private lateinit var gestureCallback: (gestureEvent: GestureEvent) -> Unit
    constructor(activity: Activity, gestureCallbacks: (gestureEvent: GestureEvent) -> Unit) : this(activity) {
        this.gestureCallback = gestureCallbacks
    }
    private var currentVolume:Double = 0.0
    lateinit var androidGesturesManager: AndroidGesturesManager
    private lateinit var overlayView: View

    // base view with other Views
    private lateinit var baseView: View
    private lateinit var swipeRightView: View
    private lateinit var swipeLeftView: View


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


        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        measureAndLayout(activity, baseView, 0)
        // define all views
        swipeRightView = baseView.findViewById(R.id.swipe_right_view)
        swipeLeftView = baseView.findViewById(R.id.swipe_left_view)
        // set all invisible
        swipeRightView.visibility = View.GONE
        baseView.visibility = View.GONE
        swipeLeftView.visibility = View.GONE

        container.overlay.add(overlayView)
        container.overlay.add(baseView)
        audioManager = activity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        currentVolume = audioManager.getCurrentVolumeInPercentage()
    }

    private fun animateBaseView(subView: View) {
        baseView.alpha = 0f
        baseView.visibility = View.VISIBLE
        subView.visibility = View.VISIBLE
        baseView.animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        baseView.visibility = View.GONE
                        subView.visibility = View.GONE
                    }
                })

    }

    private fun animateOverlay() {
        baseView.alpha = 0f
        baseView.visibility = View.VISIBLE


        overlayView.apply {
            alpha = 0f
            visibility = View.VISIBLE

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
                if(detector.deltaPixelsSinceStart < 0){
                    onGestureEventReceived(GestureEvent.SWIPING_UP)
                }else{
                    onGestureEventReceived(GestureEvent.SWIPING_DOWN)
                }
                return true
            }

            override fun onShoveBegin(detector: ShoveGestureDetector): Boolean {
//                if(detector.deltaPixelsSinceStart < 0){
//                    onGestureEventReceived(GestureEvent.SWIPING_UP)
//                }else{
//                    onGestureEventReceived(GestureEvent.SWIPING_DOWN)
//                }
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

        androidGesturesManager.shoveGestureDetector.maxShoveAngle = 90F
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
    private var  verticalSwipeEventCompletionTime:Date? = null

    private fun onGestureEventReceived(gestureEvent: GestureEvent){

        if(gestureEvent== GestureEvent.SWIPED_LEFT && isValidHorizontalSwipe() ){
            animateBaseView(swipeLeftView)
        }else if(gestureEvent== GestureEvent.SWIPED_RIGHT && isValidHorizontalSwipe() ){
            animateBaseView(swipeRightView)
        }else if(gestureEvent == GestureEvent.SWIPING_UP){
            currentVolume += 0.009
            audioManager.setVolumeByPercentage(currentVolume,false)
        }else if(gestureEvent == GestureEvent.SWIPING_DOWN){
            currentVolume -= 0.009
            audioManager.setVolumeByPercentage(currentVolume,false)
        }else if (gestureEvent == GestureEvent.SWIPED_UP || gestureEvent == GestureEvent.SWIPED_DOWN){
            verticalSwipeEventCompletionTime = Calendar.getInstance().time
        }

        //Update callback if horizontal swipes are valid
        if (gestureEvent == GestureEvent.SWIPED_LEFT || gestureEvent==GestureEvent.SWIPED_RIGHT ){
            if(isValidHorizontalSwipe()){
                gestureCallback.invoke(gestureEvent)
            }
        }else{
            gestureCallback.invoke(gestureEvent)
        }

        previousGestureEvent = gestureEvent
    }

    private fun isValidHorizontalSwipe():Boolean{
        var isValidSwipe = !(previousGestureEvent == GestureEvent.SWIPING_UP || previousGestureEvent == GestureEvent.SWIPING_DOWN)
        if(isValidSwipe && verticalSwipeEventCompletionTime != null){
            val currentTime = Calendar.getInstance().time
            val diffInMs: Long = currentTime.time - verticalSwipeEventCompletionTime!!.time
            if(diffInMs < 200){
                isValidSwipe = false
            }
        }
        return isValidSwipe
    }




    // Extension function to change media volume programmatically
    fun AudioManager.setMediaVolume(volumeIndex:Int) {
        Log.d("SettingAudio : ",volumeIndex.toString())
        // Set media volume level
        this.setStreamVolume(
            AudioManager.STREAM_MUSIC, // Stream type
            volumeIndex, // Volume index
            0// Flags
        )
    }

    fun AudioManager.setVolumeByPercentage(volume:Double, showSystemUI:Boolean) {
        var volumePercentage:Double = volume
        if (volume > 1) {
            volumePercentage = volume
        }
        if (volume < 0) {
            volumePercentage = 0.0
        }
        this.setMediaVolume((volumePercentage * this.mediaMaxVolume).roundToInt())
    }

    fun AudioManager.getCurrentVolumeInPercentage(): Double {
        return (this.mediaCurrentVolume / this.mediaMaxVolume.toDouble() * 10000) / 10000
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

}


