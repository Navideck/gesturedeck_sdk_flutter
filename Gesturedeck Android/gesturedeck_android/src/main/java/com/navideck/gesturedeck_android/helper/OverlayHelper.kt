package com.navideck.gesturedeck_android.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.navideck.gesturedeck_android.R
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.SwipeDirection
import com.navideck.gesturedeck_android.model.BackgroundMode
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAG = "OverlayHelper"

class OverlayHelper(
    private val activity: Activity,
    backgroundMode: BackgroundMode = BackgroundMode.BLUR,
    var blurRadius: Int = 25,
    var blurSampling: Int = 5,
    var dimRadius: Int = 100,
    private var canUseRenderEffect: Boolean = false,
    private var setBitmapUpdater: (() -> Bitmap?)? = null,
) {

    private lateinit var baseView: View
    private var audioManagerHelper: AudioManagerHelper
    private var currentVolume: Double = 0.0
    private var volumeStep: Double = 0.03
    private var lastYPan: Float = 0f
    private var yAxisOfZero: Float = 0f
    private var yAxisOfHundred: Float = 0f

    // AudioBar Layout
    private lateinit var audioBarLayout: ConstraintLayout
    private lateinit var customVolumeBar: CustomVolumeBar
    private lateinit var txtVolumeLevel: TextView
    private lateinit var audioBaParams: ViewGroup.LayoutParams

    // Middle Icon Layout
    private lateinit var midIconLayout: ConstraintLayout
    private lateinit var middleIconView: View
    private lateinit var middleIconImageView: ImageView

    // BlurEffect
    private var blurEffect: BlurEffectHelper? = null

    // Animation
    private val fadeInAnimationDuration: Int = 100
    private val fadeOutAnimationDuration: Int = 500

    private val zoomOutAnimation: ValueAnimator = ValueAnimator.ofFloat(1f, 2f)
    private var midIconFadeInAnimation: ObjectAnimator? = null
    private var midIconFadeOutAnimation: ObjectAnimator? = null
    private var fadeInAnimation: ObjectAnimator? = null
    private var fadeOutAnimation: ObjectAnimator? = null


    init {
        configureOverlay(backgroundMode)
        initZoomOutAnimation()
        audioManagerHelper = AudioManagerHelper(activity)
        currentVolume = audioManagerHelper.mediaCurrentVolumeInPercentage
    }

    fun dispose() {
        val container = activity.window.decorView.rootView as ViewGroup
        container.removeView(baseView)
        zoomOutAnimation.removeAllUpdateListeners()
        fadeOutAnimation?.removeAllUpdateListeners()
        fadeInAnimation?.removeAllUpdateListeners()
        midIconFadeInAnimation?.removeAllUpdateListeners()
        midIconFadeOutAnimation?.removeAllUpdateListeners()
    }

    private fun configureOverlay(backgroundMode: BackgroundMode) {
        val container = activity.window.decorView.rootView as ViewGroup
        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        measureAndLayout(activity, baseView, 0)
        // Initialise baseView and blurView
        initBackgroundMode(backgroundMode, baseView)
        initLayouts(baseView)
        container.overlay.add(baseView)
        initFadeInOutAnimation()
    }

    private fun initBackgroundMode(backgroundMode: BackgroundMode, baseView: View) {
        when (backgroundMode) {
            BackgroundMode.BLUR -> {
                blurEffect =
                    BlurEffectHelper(
                        activity,
                        blurRadius,
                        blurSampling,
                        canUseRenderEffect,
                        fadeInAnimationDuration,
                        fadeOutAnimationDuration,
                        baseView,
                        setBitmapUpdater
                    )
            }
            BackgroundMode.DIM -> {
                val dimBackground: Drawable? =
                    ResourcesCompat.getDrawable(activity.resources, R.drawable.dim_window, null);
                dimBackground?.alpha = dimRadius
                baseView.background = dimBackground
            }
        }
    }


    private fun initLayouts(view: View) {
        audioBarLayout = view.findViewById(R.id.audioBarLayout)
        customVolumeBar = view.findViewById(R.id.audioBar)
        txtVolumeLevel = view.findViewById(R.id.txtVolumeLevel)
        audioBaParams = customVolumeBar.layoutParams

        midIconLayout = baseView.findViewById(R.id.midIconLayout)
        middleIconView = baseView.findViewById(R.id.middleIconView)
        middleIconImageView = baseView.findViewById(R.id.middleIconImageView)

        view.visibility = View.GONE
        audioBarLayout.visibility = View.GONE
        midIconLayout.visibility = View.GONE

    }


    fun showSwipeLeft() = showMiddleLayout(iconDrawableId = R.drawable.icon_skip_previous)
    fun showSwipeRight() = showMiddleLayout(iconDrawableId = R.drawable.icon_skip_next)
    fun showPause() = showMiddleLayout(iconDrawableId = R.drawable.icon_pause)
    fun showResume() = showMiddleLayout(iconDrawableId = R.drawable.icon_play)

    fun showEmptyBlurView() {
        audioBarLayout.visibility = View.GONE
        midIconLayout.visibility = View.GONE
        fadeInAnimation?.start()
    }

    fun hideEmptyBlurView() {
        // Check if Both AudioBar and MidLayout are inVisible
        // and only Base View is Visible
        var isEmptyBlurViewActive =
            (baseView.visibility == View.VISIBLE) &&
                    (audioBarLayout.visibility != View.VISIBLE || midIconLayout.visibility != View.VISIBLE)
        if (isEmptyBlurViewActive) {
            fadeOutAnimation?.start()
        }
    }

    fun testOverlay() {
        showMiddleLayout(animationDuration = 1500)
    }


    private fun showMiddleLayout(iconDrawableId: Int? = null, animationDuration: Long? = null) {
        audioBarLayout.visibility = View.GONE
        if (iconDrawableId != null) middleIconImageView.setImageResource(iconDrawableId)

        middleIconImageView.setColorFilter(ContextCompat.getColor(activity, R.color.colorSecondary))
        middleIconImageView.setBackgroundResource(R.drawable.filled_circular_ring)

        midIconLayout.visibility = View.VISIBLE
        animateZoomOut(middleIconImageView)

        // cancel all animations first
        midIconFadeOutAnimation?.cancel()
        midIconFadeInAnimation?.cancel()
        fadeInAnimation?.cancel()
        fadeOutAnimation?.cancel()
        // start new Animation
        midIconFadeInAnimation?.start()
    }


    private fun isValidYaxis(event: MotionEvent): Boolean {
        if (yAxisOfHundred != 0f) {
            return if (event.y < yAxisOfHundred && currentVolume == 1.0) {
                audioManagerHelper.vibrate()
                false
            } else {
                yAxisOfHundred = 0f
                true
            }
        }
        if (yAxisOfZero != 0f) {
            return if (event.y > yAxisOfZero && currentVolume == 0.0) {
                audioManagerHelper.vibrate()
                false
            } else {
                yAxisOfZero = 0f
                true
            }
        }
        return true
    }

    private fun isVolumeAtLimit(event: MotionEvent): Boolean {
        if (currentVolume == 1.0) {
            yAxisOfHundred = event.y
            return true
        } else if (currentVolume == 0.0) {
            yAxisOfZero = event.y
            return true
        }
        return false
    }

    private fun updateDeviceVolume(swipeDirection: SwipeDirection) {
        when (swipeDirection) {
            SwipeDirection.UP -> {
                currentVolume =
                    audioManagerHelper.getValidPercentage(currentVolume + volumeStep)
            }
            SwipeDirection.DOWN -> {
                currentVolume =
                    audioManagerHelper.getValidPercentage(currentVolume - volumeStep)
            }
            else -> {}
        }
        audioManagerHelper.setVolumeByPercentage(currentVolume)
    }

    fun updateVolumeView(
        state: GestureState,
        event: MotionEvent,
        swipeDirection: SwipeDirection,
    ) {
        when (state) {
            GestureState.BEGAN -> {
                lastYPan = 0f
                yAxisOfHundred = 0F
                yAxisOfZero = 0F

                midIconLayout.visibility = View.GONE
                audioBarLayout.visibility = View.VISIBLE

                // Cancel ALl Animations
                midIconFadeInAnimation?.cancel()
                midIconFadeOutAnimation?.cancel()
                fadeOutAnimation?.cancel()
                // Start New Animation
                fadeInAnimation?.start()
            }
            GestureState.CHANGED -> {
                // To Reduce Sensitivity , recognize gesture with gap of 25 Events
                var yPan: Float = event.y
                if (abs(lastYPan - yPan) < 25) return

                // Check if Current Y axis of Fingers exceeded the required Volume Level
                if (!isValidYaxis(event)) return

                // Update Device Volume accordingly
                updateDeviceVolume(swipeDirection)

                // Set Volume Text in UI
                val roundedVolume =
                    ((((currentVolume * 100) * 10.0) / 100) * 2.0).roundToInt() / 2.0
                txtVolumeLevel.text = "$roundedVolume"

                // Check if We Already reached Valid Volume Range
                lastYPan = yPan
                if (isVolumeAtLimit(event)) return
            }
            GestureState.ENDED -> {
                fadeOutAnimation?.start()
            }
        }
        // Send Touch Events to The CustomVolumeBarUI
        customVolumeBar.onTouchEvent(event, state)
    }


    // Animate BaseView

    private fun initZoomOutAnimation() {
        zoomOutAnimation.duration = (fadeInAnimationDuration + fadeOutAnimationDuration).toLong()
        zoomOutAnimation.repeatCount = 0
        zoomOutAnimation.repeatMode = ValueAnimator.REVERSE
    }

    private fun animateZoomOut(view: View) {
        // Clear Pending Animations
        zoomOutAnimation.cancel()
        zoomOutAnimation.removeAllUpdateListeners()

        // Start new
        zoomOutAnimation.addUpdateListener { animation ->
            view.scaleX = animation.animatedValue as Float
            view.scaleY = animation.animatedValue as Float
        }
        zoomOutAnimation.start()
    }


    private fun initFadeInOutAnimation() {
        midIconFadeInAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 0.0f, 1f)
        midIconFadeOutAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 1f, 0.0f)
        fadeInAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 0.0f, 1f)
        fadeOutAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 1f, 0.0f)

        midIconFadeInAnimation?.duration = fadeInAnimationDuration.toLong()
        midIconFadeOutAnimation?.duration = fadeOutAnimationDuration.toLong()
        fadeInAnimation?.duration = fadeInAnimationDuration.toLong()
        fadeOutAnimation?.duration = fadeOutAnimationDuration.toLong()

        midIconFadeInAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.show()
                baseView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                midIconFadeOutAnimation?.start()
            }
        })

        midIconFadeOutAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.remove()
            }

            override fun onAnimationEnd(animation: Animator) {
                baseView.visibility = View.GONE
            }
        })

        fadeOutAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.remove()
            }

            override fun onAnimationEnd(a: Animator) {
                baseView.visibility = View.GONE
            }
        })

        fadeInAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.show()
                baseView.visibility = View.VISIBLE
            }
        })
    }

    // Helper Methods
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