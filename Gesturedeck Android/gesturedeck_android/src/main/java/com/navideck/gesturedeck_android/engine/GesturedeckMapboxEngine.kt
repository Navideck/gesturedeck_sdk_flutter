package com.navideck.gesturedeck_android.engine

import android.app.Activity
import android.view.MotionEvent
import com.mapbox.android.gestures.*
import com.navideck.gesturedeck_android.helper.EventTimer
import com.navideck.gesturedeck_android.helper.GesturedeckInterface
import com.navideck.gesturedeck_android.model.GestureEvent
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.SwipeDirection
import kotlin.math.abs

private const val TAG = "GesturedeckMapboxEngine"

open class GesturedeckMapboxEngine(
    activity: Activity,
    private var gesturedeckInterface: GesturedeckInterface
) {

    private lateinit var androidGesturesManager: AndroidGesturesManager
    private var touchFingerCount: Int = 1
    private var currentPanGestureState = GestureState.ENDED
    private var previousGestureEvent: GestureEvent? = null
    private val swipeMinValidDistance: Float = 50F
    private val swipeThresholdVelocity = 700
    private var minimumValidPanEvents: Int = 6
    private var currentPanEventCount = 0
    private var minimumDoubleTapPressDuration: Long = 100
    private var doubleTapTimer: EventTimer = EventTimer()
    private var minimumTwoFingerPressDuration: Long = 150
    private var twoFingerTapTimer: EventTimer = EventTimer()
    private var twoFingerLiftDelay: Long = 150
    private var twoFingerLiftDelayTimer: EventTimer = EventTimer()

    init {
        setupGesturesManager(activity)
    }

    fun onGestureEvent(gestureEvent: GestureEvent) {
        gesturedeckInterface.onGestureEvent(gestureEvent)
        previousGestureEvent = gestureEvent
    }

    fun onSwipeGestureAction(swipeDirection: SwipeDirection) {
        gesturedeckInterface.onSwipeGestureAction(swipeDirection)
    }

    fun onScaleGestureAction(
        event: MotionEvent, swipeDirection: SwipeDirection, state: GestureState
    ) {
        gesturedeckInterface.onScaleGestureAction(event, swipeDirection, state)
    }

    fun onPanGestureAction(
        event: MotionEvent, swipeDirection: SwipeDirection, state: GestureState
    ) {
        var recognizedState: GestureState = state
        if (currentPanGestureState == GestureState.ENDED && state == GestureState.CHANGED) {
            recognizedState = GestureState.BEGAN
        }
        gesturedeckInterface.onPanGestureAction(event, swipeDirection, recognizedState)
        currentPanGestureState = state
    }


    fun onTouchEvents(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                // TODO : add oneFinger DoubleTap and Swipe
            }
            MotionEvent.ACTION_DOWN -> {
                touchFingerCount = event.pointerCount
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                touchFingerCount = event.pointerCount
                if (event.pointerCount == 2) {
                    onGestureEvent(GestureEvent.TWO_FINGER_TOUCH)
                    twoFingerTapTimer.start(duration = minimumTwoFingerPressDuration) {
                        if (previousGestureEvent == GestureEvent.TWO_FINGER_TOUCH) {
                            onGestureEvent(GestureEvent.TWO_FINGER_HOLD)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                currentPanEventCount = 0
                if (twoFingerTapTimer.isActive) twoFingerTapTimer.cancel()
                if (doubleTapTimer.isActive) doubleTapTimer.cancel()
                when (previousGestureEvent) {
                    GestureEvent.TWO_FINGER_HOLD -> {
                        twoFingerLiftDelayTimer.start(duration = twoFingerLiftDelay) {
                            if (previousGestureEvent == GestureEvent.TWO_FINGER_HOLD) {
                                onGestureEvent(GestureEvent.TWO_FINGER_LIFT)
                            }
                        }
                    }
                    GestureEvent.DOUBLE_TAP_HOLD -> {
                        onGestureEvent(GestureEvent.DOUBLE_TAP_LIFT)
                    }
                    else -> {}
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

    private fun setupGesturesManager(activity: Activity) {
        androidGesturesManager = AndroidGesturesManager(activity)

        androidGesturesManager.shoveGestureDetector.maxShoveAngle = 90F

        // Scale
        androidGesturesManager.setStandardScaleGestureListener(object :
            StandardScaleGestureDetector.SimpleStandardOnScaleGestureListener() {
            override fun onScale(detector: StandardScaleGestureDetector): Boolean {
                if (detector.pointersCount == 1) {
                    val gestureEvent: GestureEvent =
                        if (detector.isScalingOut) GestureEvent.SCALE_OUT else GestureEvent.SCALE_IN
                    onGestureEvent(gestureEvent)
                    onScaleGestureAction(
                        detector.currentEvent, detector.getSwipeDirection, GestureState.CHANGED
                    )
                }
                return true
            }

            override fun onScaleBegin(detector: StandardScaleGestureDetector): Boolean {
                if (detector.pointersCount == 1) {
                    onScaleGestureAction(
                        detector.currentEvent, detector.getSwipeDirection, GestureState.BEGAN
                    )
                }
                return super.onScaleBegin(detector)
            }

            override fun onScaleEnd(
                detector: StandardScaleGestureDetector, velocityX: Float, velocityY: Float
            ) {
                if (detector.pointersCount + 1 == 1) {
                    onScaleGestureAction(
                        detector.currentEvent, detector.getSwipeDirection, GestureState.ENDED
                    )
                }
                super.onScaleEnd(detector, velocityX, velocityY)
            }
        })

        // Fling , SingleTap , DoubleTap
        androidGesturesManager.setStandardGestureListener(object :
            StandardGestureDetector.SimpleStandardOnGestureListener() {

            // To Detect Right and Left Swipes
            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                if (!isValidTowFingersTouch()) return true
                val flingDirection: SwipeDirection =
                    SwipeDirection.direction(e1.x, e1.y, e2.x, e2.y)
                val distanceCovered =
                    if (flingDirection == SwipeDirection.RIGHT) (e2.x - e1.x) else (e1.x - e2.x)
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
                        onSwipeGestureAction(SwipeDirection.LEFT)
                    }
                    SwipeDirection.RIGHT -> {
                        onGestureEvent(GestureEvent.SWIPE_RIGHT)
                        onSwipeGestureAction(SwipeDirection.RIGHT)
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
                // recognize it as DOUBLE_TAP_HOLD if no other event occurred within given duration
                doubleTapTimer.start(duration = minimumDoubleTapPressDuration) {
                    if (previousGestureEvent == GestureEvent.DOUBLE_TAP) {
                        onGestureEvent(GestureEvent.DOUBLE_TAP_HOLD)
                    }
                }
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
        androidGesturesManager.setShoveGestureListener(object :
            ShoveGestureDetector.SimpleOnShoveGestureListener() {
            override fun onShove(
                detector: ShoveGestureDetector,
                deltaPixelsSinceLast: Float,
                deltaPixelsSinceStart: Float
            ): Boolean {
                val direction: SwipeDirection = detector.getSwipeDirection
                if (direction == SwipeDirection.UP || direction == SwipeDirection.DOWN) {
                    // TODO : Check with Multiple devices
                    currentPanEventCount += 1
                    // expecting first few events to be a Swipe
                    if (currentPanEventCount < minimumValidPanEvents) return true

                    onPanGestureAction(
                        detector.currentEvent, detector.getSwipeDirection, GestureState.CHANGED
                    )
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
                if (currentPanGestureState != GestureState.ENDED) {
                    onPanGestureAction(
                        detector.currentEvent, detector.getSwipeDirection, GestureState.ENDED
                    )
                }

            }
        })
    }

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