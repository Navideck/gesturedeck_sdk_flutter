package com.navideck.gesturedeck_android.gesturedeckVariants

import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import com.mapbox.android.gestures.AndroidGesturesManager
import com.mapbox.android.gestures.ShoveGestureDetector
import com.mapbox.android.gestures.ShoveGestureDetector.SimpleOnShoveGestureListener
import com.mapbox.android.gestures.StandardGestureDetector.SimpleStandardOnGestureListener
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.android.gestures.StandardScaleGestureDetector.SimpleStandardOnScaleGestureListener
import com.navideck.gesturedeck_android.helper.OverlayHelper
import com.navideck.gesturedeck_android.model.BackgroundMode
import com.navideck.gesturedeck_android.model.SwipeDirection
import com.navideck.gesturedeck_android.model.GestureEvent
import com.navideck.gesturedeck_android.model.GestureState
import kotlin.math.abs

private const val TAG = "GesturedeckMapbox"

class GesturedeckMapbox(
    private var activity: Activity,
    backgroundMode: BackgroundMode = BackgroundMode.BLUR,
    blurRadius: Int = 25,
    blurSampling: Int = 5,
    dimRadius: Int = 100,
    canUseRenderEffect: Boolean = false,
    gestureCallbacks: ((gestureEvent: GestureEvent) -> Unit)? = null,
) {

    // TODO : avoid initialising twice from same activity
    companion object {
        val activityLists = arrayListOf<Activity>()
    }


    // optional Callback Method
    private var gestureCallback: ((gestureEvent: GestureEvent) -> Unit)?

    // Configurable Variables
    private val swipeMinValidDistance: Float = 50F
    private val swipeThresholdVelocity = 700
    private var minimumValidPanEvents: Int = 6
    private var currentPanEventCount = 0

    // Private Variables
    private lateinit var androidGesturesManager: AndroidGesturesManager
    private var overlayHelper: OverlayHelper
    private var touchFingerCount: Int = 1
    private var currentPanGestureState = GestureState.ENDED
    private var previousGestureEvent: GestureEvent = GestureEvent.DOUBLE_TAP

    init {
        this.gestureCallback = gestureCallbacks
        overlayHelper = OverlayHelper(
            activity, backgroundMode, blurRadius, blurSampling, dimRadius, canUseRenderEffect
        )
        setupGesturesManager(activity)
    }

    fun dispose() {
        this.gestureCallback = null
        overlayHelper.dispose()
    }

    private fun configureTouchEvent() {
        // Experimental : To avoid reliance on dispatchTouchEvent from MainActivity
        // TODO: FIX - Failed when there are Clickable and Scrollable Views on Top of Root View

        //        activity.window.decorView.rootView.setOnTouchListener { v: View, event ->
        //            v.performClick()
        //            onTouchEvents(event)
        //            true
        //        }
    }

    fun onTouchEvents(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                touchFingerCount = event.pointerCount
                // Show an Empty BlurView if Someone Put Fingers on the Screen
                if (event.pointerCount == 2) {
                    //TODO : Fix TwoFingerTouch conflicting with horizontal Swipes

                    // onGestureEvent(GestureEvent.TWO_FINGER_TOUCH)
                }
            }
            MotionEvent.ACTION_UP -> {
                currentPanEventCount = 0
                if (currentPanGestureState != GestureState.ENDED) {
                    // TODO : Check in myDevice , required this workaround because of onShoveEnded was not working Perfectly
                    // panGestureAction(ShoveGestureDetector(activity, androidGesturesManager), GestureState.ENDED)
                }
                if (previousGestureEvent == GestureEvent.TWO_FINGER_TOUCH || previousGestureEvent == GestureEvent.DOUBLE_TAP) {
                    //TODO : Fix hideView conflicting with horizontal Swipes
                    overlayHelper.hideEmptyBlurView()
                }
            }
        }
        androidGesturesManager.onTouchEvent(event)
    }

    private fun isValidTowFingersTouch(): Boolean {
        return if (touchFingerCount != 2) false else {
            touchFingerCount = 1
            true
        }
    }

    /** Gesture Actions , called from [setupGesturesManager] */

    private fun onGestureEvent(gestureEvent: GestureEvent) {
        // Send recognized Gestures to Callback
        gestureCallback?.invoke(gestureEvent)

        previousGestureEvent = gestureEvent

        when (gestureEvent) {
            GestureEvent.TWO_FINGER_TOUCH -> {
                overlayHelper.showEmptyBlurView()
            }
            GestureEvent.DOUBLE_TAP -> {
                overlayHelper.showEmptyBlurView()
            }
            else -> {}
        }
    }

    private fun swipeGestureAction(gestureEvent: GestureEvent) {
        when (gestureEvent) {
            GestureEvent.SWIPE_LEFT -> {
                overlayHelper.showSwipeLeft()
            }
            GestureEvent.SWIPE_RIGHT -> {
                overlayHelper.showSwipeRight()
            }
            else -> {}
        }
    }

    private fun scaleGestureAction(detector: StandardScaleGestureDetector, state: GestureState) {
        updateVolumeUi(detector.currentEvent, state, detector.getSwipeDirection)
    }

    private fun panGestureAction(detector: ShoveGestureDetector, state: GestureState) {
        var recognizedState: GestureState = state
        // Convert First ChangedState into BeganState
        if (currentPanGestureState == GestureState.ENDED && state == GestureState.CHANGED) {
            recognizedState = GestureState.BEGAN
        }
        updateVolumeUi(
            detector.currentEvent, recognizedState, detector.getSwipeDirection
        )
        currentPanGestureState = state
    }

    private fun updateVolumeUi(
        event: MotionEvent,
        state: GestureState,
        direction: SwipeDirection,
    ) {
        overlayHelper.updateVolumeView(state, event, direction)
    }

    /** Gesture Manager To Convert Mapbox callbacks to
     * GestureEvents and Call Specific Action Methods */
    private fun setupGesturesManager(activity: Activity) {
        androidGesturesManager = AndroidGesturesManager(activity)

        androidGesturesManager.shoveGestureDetector.maxShoveAngle = 90F

        // Scale
        androidGesturesManager.setStandardScaleGestureListener(object :
            SimpleStandardOnScaleGestureListener() {
            override fun onScale(detector: StandardScaleGestureDetector): Boolean {
                if (detector.pointersCount == 1) {
                    val gestureEvent: GestureEvent =
                        if (detector.isScalingOut) GestureEvent.SCALE_OUT else GestureEvent.SCALE_IN
                    onGestureEvent(gestureEvent)
                    scaleGestureAction(detector, GestureState.CHANGED)
                }
                return true
            }

            override fun onScaleBegin(detector: StandardScaleGestureDetector): Boolean {
                if (detector.pointersCount == 1) {
                    scaleGestureAction(detector, GestureState.BEGAN)
                }
                return super.onScaleBegin(detector)
            }

            override fun onScaleEnd(
                detector: StandardScaleGestureDetector, velocityX: Float, velocityY: Float
            ) {
                // TODO : check again with myDevice , getting -1 from original FingersTouch count on OnePlus
                // but on myDevice , its not even occurred whilePAN ,
                // but in other devices ,this even occurring after Pan as well
                if (detector.pointersCount + 1 == 1) {
                    Log.e(TAG, "Scale Ended With ${detector.pointersCount} Fingers Touch")
                    scaleGestureAction(detector, GestureState.ENDED)
                }
                super.onScaleEnd(detector, velocityX, velocityY)
            }
        })

        // Fling , SingleTap , DoubleTap
        androidGesturesManager.setStandardGestureListener(object :
            SimpleStandardOnGestureListener() {

            // To Detect Right and Left Swipes
            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                if (!isValidTowFingersTouch()) return true
                val flingDirection: SwipeDirection =
                    SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
                val distanceCovered =
                    if (flingDirection == SwipeDirection.RIGHT) (e2.x - e1.x) else (e1.x - e2.x)
//                Log.e("velocityX", velocityX.toString())
//                Log.e("distanceCovered", distanceCovered.toString())
//                Log.e("direction", flingDirection.name)
                val isHorizontalSwipe: Boolean =
                    flingDirection == SwipeDirection.LEFT || flingDirection == SwipeDirection.RIGHT
                if (!isHorizontalSwipe) return true
                val haveValidSwipeVelocity: Boolean = abs(velocityX) > swipeThresholdVelocity
                if (!haveValidSwipeVelocity) return true

                val isValidDistanceCovered: Boolean = distanceCovered > swipeMinValidDistance
                if (!isValidDistanceCovered) return true

                when (flingDirection) {
                    SwipeDirection.LEFT -> {
                        onGestureEvent(GestureEvent.SWIPE_LEFT)
                        swipeGestureAction(GestureEvent.SWIPE_LEFT)
                    }
                    SwipeDirection.RIGHT -> {
                        onGestureEvent(GestureEvent.SWIPE_RIGHT)
                        swipeGestureAction(GestureEvent.SWIPE_RIGHT)
                    }
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

        // TwoFingerTap
        androidGesturesManager.setMultiFingerTapGestureListener { _, pointersCount ->
            if (pointersCount == 2) {
                onGestureEvent(GestureEvent.TWO_FINGER_TAP)
            }
            true
        }


        // Shove
        androidGesturesManager.setShoveGestureListener(object : SimpleOnShoveGestureListener() {
            override fun onShove(
                detector: ShoveGestureDetector,
                deltaPixelsSinceLast: Float,
                deltaPixelsSinceStart: Float
            ): Boolean {
                var direction: SwipeDirection = detector.getSwipeDirection
                if (direction == SwipeDirection.UP || direction == SwipeDirection.DOWN) {
                    // TODO : Check with Multiple devices
                    currentPanEventCount += 1
                    // expecting first few events to be a Swipe
                    if (currentPanEventCount < minimumValidPanEvents) return true

                    panGestureAction(detector, GestureState.CHANGED)
                    when (direction) {
                        SwipeDirection.UP -> onGestureEvent(GestureEvent.PAN_UP)
                        SwipeDirection.DOWN -> onGestureEvent(GestureEvent.PAN_DOWN)
                        else -> {}
                    }
                }
                return true
            }

            override fun onShoveEnd(
                detector: ShoveGestureDetector, velocityX: Float, velocityY: Float
            ) {
                // TODO : Check Again in my device , its not occurring everytime
                // but in onePlus , we get this event EveryTime
                if (currentPanGestureState != GestureState.ENDED) {
                    panGestureAction(detector, GestureState.ENDED)
                }

            }
        })
    }


    /** Mapbox extensions */
    private val StandardScaleGestureDetector.getSwipeDirection: SwipeDirection
        get() {
            val e1: MotionEvent = this.previousEvent
            val e2: MotionEvent = this.currentEvent
            return SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
        }

    private val ShoveGestureDetector.getSwipeDirection: SwipeDirection
        get() {
            val e1: MotionEvent = this.previousEvent
            val e2: MotionEvent = this.currentEvent
            return SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
        }
}

