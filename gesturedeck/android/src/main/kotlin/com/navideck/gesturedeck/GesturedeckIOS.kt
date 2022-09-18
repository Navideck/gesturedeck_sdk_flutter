package com.navideck.gesturedeck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import it.sephiroth.android.library.uigestures.*


class GesturedeckIOS(activity: Activity) {
    private lateinit var gestureCallback: (gestureEvent: GestureEvent) -> Unit
    constructor(activity: Activity, gestureCallbacks: (gestureEvent: GestureEvent) -> Unit) : this(activity) {
        this.gestureCallback = gestureCallbacks
    }

    private var tag = "GesturedeckIOS"

    private var lastYPan: Float = 0.0F
    private lateinit var baseView : View
    private lateinit var rootView : View

    private lateinit var swipeRightView: View
    private lateinit var swipeLeftView: View
    private lateinit var audioManager: AudioManager

    init {
        configureOverlay(activity)
        addGestureRecognizers(activity.applicationContext)
    }

    private fun configureOverlay(activity: Activity) {
        Log.d(tag,"Configureing overlay")
        var container = activity.window.decorView.rootView as ViewGroup
       // rootView = container

        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        // define all views
        swipeRightView = baseView.findViewById(R.id.swipe_right_view)
        swipeLeftView = baseView.findViewById(R.id.swipe_left_view)
        // set all invisible
        swipeRightView.visibility = View.GONE
        baseView.visibility = View.GONE
        swipeLeftView.visibility = View.GONE

        container.overlay.add(baseView)
        audioManager = activity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
       // return androidGesturesManager.onTouchEvent(event);
        return  true
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


    // gesture recognizer actionlistener
    private val panRecognizerAction =  { gestureRecognizer: UIGestureRecognizer ->
        Log.e("PanGesture","${gestureRecognizer.state} State")
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

    private val rightSwipeAction =  { recognizer: UIGestureRecognizer ->
        Log.e("RightSwipeGesture","${recognizer.numberOfTouches} Fingers  | ${recognizer.currentLocationX}")
        onGestureEventReceived(GestureEvent.SWIPED_RIGHT)
    }

    private val leftSwipeAction =  { recognizer: UIGestureRecognizer ->
        Log.e("LeftSwipeGesture","${recognizer.numberOfTouches} Fingers  | ${recognizer.currentLocationX}")
        onGestureEventReceived(GestureEvent.SWIPED_LEFT)
    }


    private fun onGestureEventReceived(gestureEvent: GestureEvent){
        if(gestureEvent== GestureEvent.SWIPED_LEFT  ){
            animateBaseView(swipeLeftView)
        }else if(gestureEvent== GestureEvent.SWIPED_RIGHT  ){
            animateBaseView(swipeRightView)
        }else if(gestureEvent == GestureEvent.SWIPING_UP){


        }else if(gestureEvent == GestureEvent.SWIPING_DOWN){

        }else if (gestureEvent == GestureEvent.SWIPED_UP || gestureEvent == GestureEvent.SWIPED_DOWN){

        }

        //Update callback if horizontal swipes are valid
        if (gestureEvent == GestureEvent.SWIPED_LEFT || gestureEvent==GestureEvent.SWIPED_RIGHT ){
                gestureCallback.invoke(gestureEvent)
        }else{
            gestureCallback.invoke(gestureEvent)
        }
    }


    private fun addGestureRecognizers(context:Context){
        Log.d("GeturedeckIOS","Configuring GestureDeck")
        val delegate = UIGestureRecognizerDelegate()
        val recognizer1 =  UITapGestureRecognizer(context)

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

        baseView.setGestureDelegate(delegate)
        delegate.shouldReceiveTouch = { recognizer -> true }
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

        Log.d("GeturedeckIOS","Getures Configured")
    }


}