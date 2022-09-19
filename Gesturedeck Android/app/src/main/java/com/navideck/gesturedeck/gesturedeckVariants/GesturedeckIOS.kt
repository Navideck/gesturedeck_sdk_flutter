package com.navideck.gesturedeck.gesturedeckVariants

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.View
import com.navideck.gesturedeck.model.GestureEvent
import it.sephiroth.android.library.uigestures.*

private const val DEBUG_TAG = "GesturedeckIOS"

class GesturedeckIOS(private val activity: Activity,gestureCallbacks: (gestureEvent: GestureEvent) -> Unit) {
    private  var gestureCallback: (gestureEvent: GestureEvent) -> Unit

    private var lastYPan: Float = 0.0F
    private lateinit var baseView : View
    private lateinit var rootView : View
    private lateinit var audioManager: AudioManager

    init {
        this.gestureCallback = gestureCallbacks
        //TODO: not working if called these methods from INIT
//        configureOverlay(activity)
//        addGestureRecognizers(activity)
    }

    fun start(){
        addGestureRecognizers(activity)
    }

    // gesture recognizer actionlistener
    private val panRecognizerAction =  { gestureRecognizer: UIGestureRecognizer ->
        //TODO : configure Swipe up and down
        onGestureEventReceived(GestureEvent.PAN_UP)
        when (gestureRecognizer.state) {
            UIGestureRecognizer.State.Began -> {
                lastYPan = 0F
                var panPointX = gestureRecognizer.currentLocationX
                var panPointY = gestureRecognizer.currentLocationY
            }
            UIGestureRecognizer.State.Changed -> {
                var yPan: Float = gestureRecognizer.currentLocationY
                lastYPan = yPan
            }
            UIGestureRecognizer.State.Ended -> {
                // GestureEnded
            }
            else -> {}
        }
    }

    private val rightSwipeAction =  { recognizer: UIGestureRecognizer ->onGestureEventReceived(
        GestureEvent.SWIPE_RIGHT)}

    private val leftSwipeAction =  { recognizer: UIGestureRecognizer -> onGestureEventReceived(
        GestureEvent.SWIPE_LEFT)}


    private fun onGestureEventReceived(gestureEvent: GestureEvent){
        // Log.e(DEBUG_TAG,gestureEvent.name)
        gestureCallback.invoke(gestureEvent)
    }


    private fun addGestureRecognizers(activity: Activity){
        var context:Context = activity.applicationContext
        val delegate = UIGestureRecognizerDelegate()
        val recognizer1 =  UITapGestureRecognizer(context)
        recognizer1.actionListener = panRecognizerAction

        // add pan Gestures
        val panRecognizer =  UIPanGestureRecognizer(context)
        panRecognizer.maximumNumberOfTouches=2
        panRecognizer.minimumNumberOfTouches=2
        panRecognizer.cancelsTouchesInView = false
        panRecognizer.actionListener = panRecognizerAction


        // add leftSwipe Gesture
        val leftSwipeGestureRecognizer = UISwipeGestureRecognizer(context)
        leftSwipeGestureRecognizer.direction = UISwipeGestureRecognizer.LEFT
        leftSwipeGestureRecognizer.cancelsTouchesInView = false
        leftSwipeGestureRecognizer.numberOfTouchesRequired = 2
        leftSwipeGestureRecognizer.actionListener = leftSwipeAction


        // add rightSwipe Gesture
        val rightSwipeGestureRecognizer = UISwipeGestureRecognizer(context)
        rightSwipeGestureRecognizer.direction = UISwipeGestureRecognizer.RIGHT
        rightSwipeGestureRecognizer.cancelsTouchesInView = false
        rightSwipeGestureRecognizer.numberOfTouchesRequired = 2
        rightSwipeGestureRecognizer.actionListener = rightSwipeAction


        // add all Gestures to delegate
        delegate.addGestureRecognizer(recognizer1)
        delegate.addGestureRecognizer(panRecognizer)
        delegate.addGestureRecognizer(leftSwipeGestureRecognizer)
        delegate.addGestureRecognizer(rightSwipeGestureRecognizer)

        rootView.setGestureDelegate(delegate)

        delegate.shouldReceiveTouch = { true }
        delegate.shouldBegin = { panGestureRecognizer ->
            true
            //TODO : avoid Pan Gesture with Simultanous Swip Gesture
//            if(panGestureRecognizer is UIPanGestureRecognizer){
//                panGestureRecognizer.yVelocity > panGestureRecognizer.xVelocity
//            }else{
//                true
//            }
        }

        delegate.shouldRecognizeSimultaneouslyWithGestureRecognizer = { gestureRecognizer, otherGestureRecognizer ->
            !(// either its Tap + Pan
                    (gestureRecognizer is UITapGestureRecognizer && otherGestureRecognizer is UIPanGestureRecognizer) ||
                            (gestureRecognizer is UIPanGestureRecognizer && otherGestureRecognizer is UITapGestureRecognizer) ||
                            // or Tap + Swipe
                            (gestureRecognizer is UITapGestureRecognizer && otherGestureRecognizer is UISwipeGestureRecognizer) ||
                            (gestureRecognizer is UISwipeGestureRecognizer && otherGestureRecognizer is UITapGestureRecognizer))
        }
    }


}