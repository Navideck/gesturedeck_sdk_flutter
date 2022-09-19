package com.navideck.gesturedeck.helper

import abak.tr.com.boxedverticalseekbar.BoxedVertical
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.navideck.gesturedeck.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

private const val TAG = "OverlayHelper"


class OverlayHelper(private val activity: Activity) {
    private lateinit var baseView : View
    private lateinit var audioProgressBarView : BoxedVertical
    private lateinit var middleIconView : View

    init {
        configureOverlay()
    }

    private fun configureOverlay() {
        var container = activity.window.decorView.rootView as ViewGroup
        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        measureAndLayout(activity, baseView, 0)

        audioProgressBarView = baseView.findViewById(R.id.audioProgressBarView)
        middleIconView = baseView.findViewById(R.id.middleIconView)

        baseView.visibility = View.GONE
        middleIconView.visibility = View.GONE
        audioProgressBarView.visibility = View.GONE

        // Initialize Blur View
        var blurView:BlurView = baseView.findViewById(R.id.blurViewAudioBar)
        var windowBackground = activity.window.decorView.background;
        var blurAlgorithm =  RenderScriptBlur(activity)
        blurView.setupWith(container, blurAlgorithm).setFrameClearDrawable(windowBackground).setBlurRadius(10F)
        container.overlay.add(baseView)
    }

    private fun disposeOverlay(){
        var container = activity.window.decorView.rootView as ViewGroup
        container.overlay.remove(baseView)
    }

    // Temporary
    fun testOverlay() {
        showMiddleIconView(middleIconView, onAnimationComplete = { disposeOverlay() })
    }

    fun showSwipeLeft() = showMiddleIconView(middleIconView);
    fun showSwipeRight() = showMiddleIconView(middleIconView);

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


    private fun showMiddleIconView(middleIconView:View, onAnimationComplete : (() -> Unit)? = null){
        audioProgressBarView.visibility = View.GONE
        middleIconView.visibility = View.VISIBLE
        showBaseView(baseView,){
            hideBaseView(baseView,){
                middleIconView.visibility = View.GONE
            }
        }
    }

    fun updateVolumeView(volume:Float){
        middleIconView.visibility = View.GONE
        audioProgressBarView.visibility = View.VISIBLE
        audioProgressBarView.max = 100
        audioProgressBarView.value = (volume*100).toInt()
    }


     fun showBaseView(view: View?=null, animationDuration:Long = 200, onAnimationComplete : (() -> Unit)? = null){
         var v = view ?:baseView
         v.alpha = 0f
         v.visibility = View.VISIBLE
         v.animate()
            .alpha(0.9f)
            .setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationComplete?.invoke()
                }
            })
    }

     fun hideBaseView(view: View?=null, animationDuration:Long = 700, onAnimationComplete : (() -> Unit)? = null){
         var v = view ?:baseView
         v.animate()
            .alpha(0f)
            .setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationComplete?.invoke()
                }
            })
    }

}