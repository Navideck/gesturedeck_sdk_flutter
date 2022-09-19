package com.navideck.gesturedeck.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.navideck.gesturedeck.R
import com.navideck.gesturedeck.model.GestureEvent

class OverlayHelper(private val activity: Activity) {
    private lateinit var baseView:View

    init { configureOverlay() }

    private fun configureOverlay() {
        var container = activity.window.decorView.rootView as ViewGroup
        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        measureAndLayout(activity, baseView, 0)
        baseView.visibility = View.GONE
        container.overlay.add(baseView)
    }

    private fun disposeOverlay(){
        var container = activity.window.decorView.rootView as ViewGroup
        container.overlay.remove(baseView)
    }


    private fun animateBaseView(baseView: View, animationDuration:Long = 2000, onAnimationComplete : (() -> Unit)? = null) {
        baseView.alpha = 0f
        baseView.visibility = View.VISIBLE
        baseView.animate()
            .alpha(1f)
            .setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    baseView.visibility = View.GONE
                    onAnimationComplete?.invoke()
                }
            })
    }

    // Temporary
    fun testOverlay() {
        animateBaseView(baseView, onAnimationComplete = { disposeOverlay() })
    }
    fun showSwipeLeft() = animateBaseView(baseView);
    fun showSwipeRight() = animateBaseView(baseView);

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

}