package com.navideck.gesturedeck.gesturedeckVariants

import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.mapbox.android.gestures.AndroidGesturesManager
import com.mapbox.android.gestures.ShoveGestureDetector
import com.mapbox.android.gestures.ShoveGestureDetector.SimpleOnShoveGestureListener
import com.mapbox.android.gestures.StandardGestureDetector.SimpleStandardOnGestureListener
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.android.gestures.StandardScaleGestureDetector.SimpleStandardOnScaleGestureListener
import com.navideck.gesturedeck.helper.AudioManagerHelper
import com.navideck.gesturedeck.helper.OverlayHelper
import com.navideck.gesturedeck.model.SwipeDirection
import com.navideck.gesturedeck.model.GestureEvent
import kotlin.math.abs

private const val TAG = "GesturedeckMapbox"

class GesturedeckMapbox(activity: Activity, gestureCallbacks:((gestureEvent: GestureEvent) -> Unit)? = null) {
    private var gestureCallback: ((gestureEvent: GestureEvent) -> Unit)?

    private lateinit var androidGesturesManager: AndroidGesturesManager
    private var audioManagerHelper:AudioManagerHelper
    private var overlayHelper:OverlayHelper
    private var currentVolume:Double = 0.0
    private val swipeMinValidDistance:Float = 100F
    private val swipeMaxValidDistance:Float = 600F
    private val swipeThresholdVelocity = 1000
    private var touchFingerCount:Int = 1
    private var volumeStep: Double = 0.03

    init {
        this.gestureCallback = gestureCallbacks
        overlayHelper =  OverlayHelper(activity)
        audioManagerHelper = AudioManagerHelper(activity)
        currentVolume = audioManagerHelper.mediaCurrentVolumeInPercentage
        setupGesturesManager(activity)
        configureTouchEvent(activity)
    }

    private fun configureTouchEvent(activity: Activity){
        // Experimental : To avoid reliance on dispatchTouchEvent from MainActivity
        activity.window.decorView.rootView.setOnTouchListener { v: View, event ->
            v.performClick()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN ->{
                    touchFingerCount = event.pointerCount
                }
            }
            androidGesturesManager.onTouchEvent(event);
        }
    }

    private fun isValidTowFingersTouch():Boolean{
        return if(touchFingerCount != 2) false else{
            touchFingerCount = 1
            true
        }
    }

    private fun onGestureEvent(gestureEvent: GestureEvent){
        when (gestureEvent) {
            GestureEvent.SWIPE_LEFT -> {
                overlayHelper.showSwipeLeft()
            }
            GestureEvent.SWIPE_RIGHT -> {
                overlayHelper.showSwipeRight()
            }
            else -> {}
        }
        // Send Gestures to the callback Listener
        gestureCallback?.invoke(gestureEvent)
    }

    private fun panGestureAction(detector: ShoveGestureDetector,state: GestureState){
       if(!detector.isVerticalGesture)return
        when(state){
            GestureState.BEGAN ->{
                Log.e(TAG,"StartedPanning")
                overlayHelper.showBaseView()
            }
            GestureState.CHANGED -> {
                when(detector.getSwipeDirection){
                    SwipeDirection.UP -> {
                        currentVolume = audioManagerHelper.getValidPercentage(currentVolume + volumeStep)
                    }
                    SwipeDirection.DOWN->{
                        currentVolume = audioManagerHelper.getValidPercentage(currentVolume - volumeStep)
                    }else -> {}
                }
                overlayHelper.updateVolumeView(currentVolume.toFloat())
                audioManagerHelper.setVolumeByPercentage(currentVolume)
            }
            GestureState.ENDED -> {
                Log.e(TAG,"EndedPanning")
                //TODO : FIX - Not getting always
                overlayHelper.hideBaseView()
            }
        }
    }


    private fun setupGesturesManager(activity: Activity) {
        androidGesturesManager = AndroidGesturesManager(activity)
        androidGesturesManager.shoveGestureDetector.maxShoveAngle = 90F
        androidGesturesManager.setStandardScaleGestureListener(
            object : SimpleStandardOnScaleGestureListener() {
                override fun onScale(detector: StandardScaleGestureDetector): Boolean {
                    if (detector.pointersCount == 1) {
                        var gestureEvent:GestureEvent = if(detector.isScalingOut) GestureEvent.SCALE_OUT else GestureEvent.SCALE_IN
                        onGestureEvent(gestureEvent)
                    }
                    return true
                }
            })

        androidGesturesManager.setStandardGestureListener(object :
            SimpleStandardOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if(!isValidTowFingersTouch()) return true
                var flingDirection:SwipeDirection = SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
                var distanceCovered  = if(flingDirection == SwipeDirection.RIGHT) (e2.x - e1.x) else (e1.x - e2.x)

                // Check Swipe Direction
                var isHorizontalSwipe:Boolean = flingDirection == SwipeDirection.LEFT || flingDirection == SwipeDirection.RIGHT
                if(!isHorizontalSwipe)return true

                Log.e(TAG,"Fling :Direction -> ${flingDirection.name} | Distance $distanceCovered | Velocity : ${abs(velocityX)}")

                // Check Swipe Velocity
                var haveValidSwipeVelocity:Boolean =  abs(velocityX) > swipeThresholdVelocity
                if(!haveValidSwipeVelocity)return  true

                // Check Swipe Distance
                var isValidDistanceCovered:Boolean = distanceCovered in swipeMinValidDistance..swipeMaxValidDistance
                if(!isValidDistanceCovered)return  true

                Log.e(TAG,"SwipeGestureRecognizedWith :Distance $distanceCovered | Velocity : ${abs(velocityX)}")

                when (flingDirection) {
                    SwipeDirection.LEFT -> onGestureEvent(GestureEvent.SWIPE_LEFT)
                    SwipeDirection.RIGHT -> onGestureEvent(GestureEvent.SWIPE_RIGHT)
                    else -> {}
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onGestureEvent(GestureEvent.SINGLE_TAP)
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                onGestureEvent(GestureEvent.DOUBLE_TAP)
                return true
            }

        })

        androidGesturesManager.setMultiFingerTapGestureListener { _, pointersCount ->
            if (pointersCount == 2) {
                onGestureEvent(GestureEvent.TWO_FINGER_TAP)
            }
            true
        }

        androidGesturesManager.setShoveGestureListener(object : SimpleOnShoveGestureListener() {
            override fun onShove(
                detector: ShoveGestureDetector,
                deltaPixelsSinceLast: Float,
                deltaPixelsSinceStart: Float
            ): Boolean {
                panGestureAction(detector,GestureState.CHANGED)
                when (detector.getSwipeDirection) {
                    SwipeDirection.UP -> onGestureEvent(GestureEvent.PAN_UP)
                    SwipeDirection.DOWN -> onGestureEvent(GestureEvent.PAN_DOWN)
                    else -> {}
                }
                return true
            }

            override fun onShoveBegin(detector: ShoveGestureDetector): Boolean {
                panGestureAction(detector,GestureState.BEGAN)
                return true
            }

            override fun onShoveEnd(
                detector: ShoveGestureDetector,
                velocityX: Float,
                velocityY: Float
            ) {
                // TODO FIX: Not Executing Everytime
                panGestureAction(detector,GestureState.ENDED)
            }
        })


    }

    // ShoveGestureDetector Extension
    private val ShoveGestureDetector.getSwipeDirection: SwipeDirection get(){
        var e1: MotionEvent = this.previousEvent
        var e2: MotionEvent = this.currentEvent
        return SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
    }

    private val ShoveGestureDetector.isVerticalGesture: Boolean get() {
       var swipeDirection:SwipeDirection = this.getSwipeDirection
        return  swipeDirection == SwipeDirection.UP ||swipeDirection ==  SwipeDirection.DOWN
    }

    private val ShoveGestureDetector.isHorizontalGesture: Boolean get() {
        var swipeDirection:SwipeDirection = this.getSwipeDirection
        return  swipeDirection == SwipeDirection.LEFT ||swipeDirection ==  SwipeDirection.RIGHT
    }

    enum class GestureState { BEGAN , CHANGED , ENDED }

}

