package com.navideck.gesturedeck_android.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.navideck.gesturedeck_android.R
import com.navideck.gesturedeck_android.model.BackgroundMode
import com.navideck.gesturedeck_android.model.GestureState
import com.navideck.gesturedeck_android.model.SwipeDirection
import kotlin.math.abs
import kotlin.math.roundToInt


private const val TAG = "OverlayHelper"

class OverlayHelper(
    private val activity: Activity,
    private var bitmapCallback: (() -> Bitmap?)? = null,
    private var tintColor: Int? = null,
    private var volumeIconDrawable: Drawable? = null,
    private var iconSwipeLeftDrawable: Drawable? = null,
    private var iconSwipeRightDrawable: Drawable? = null,
    private var iconTapDrawable: Drawable? = null,
    private var iconTapToggledDrawable: Drawable? = null,
    rootView: ViewGroup? = null
) {

    private lateinit var baseView: View
    private var audioManagerHelper: AudioManagerHelper
    private var currentVolume: Double = 0.0
    private var volumeStep: Double = 0.03
    private var lastYPan: Float = 0f
    private var yAxisOfZero: Float = 0f
    private var yAxisOfHundred: Float = 0f
    private var blurRadius: Int = 25
    private var blurSampling: Int = 5
    private var dimAlpha: Int = 240
    private var canUseRenderEffect: Boolean = false

    // AudioBar Layout
    private lateinit var audioBarLayout: ConstraintLayout
    private lateinit var volumeBar: VolumeBar
    private lateinit var txtVolumeLevel: TextView
    private lateinit var audioBaParams: ViewGroup.LayoutParams
    private lateinit var volumeIcon: ImageView

    // Center Icon Layout
    private lateinit var centerIconLayout: ConstraintLayout
    private lateinit var centerIconView: View
    private lateinit var centerIconImageView: ImageView
    private var centerIconBackgroundDrawable: Drawable? = null

    // BlurEffect
    private var blurEffect: BlurEffectHelper? = null

    // Animation
    private val fadeInAnimationDuration: Int = 100
    private val fadeOutAnimationDuration: Int = 500

    private val zoomOutAnimation: ValueAnimator = ValueAnimator.ofFloat(1f, 2f)
    private var centerIconFadeInAnimation: ObjectAnimator? = null
    private var centerIconFadeOutAnimation: ObjectAnimator? = null
    private var fadeInAnimation: ObjectAnimator? = null
    private var fadeOutAnimation: ObjectAnimator? = null


    init {
        configureOverlay(rootView)
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
        centerIconFadeInAnimation?.removeAllUpdateListeners()
        centerIconFadeOutAnimation?.removeAllUpdateListeners()
    }

    private fun configureOverlay(rootView: ViewGroup? = null) {
        val container = rootView ?: activity.window.decorView.rootView as ViewGroup
        baseView = activity.layoutInflater.inflate(R.layout.base_view, null)
        measureAndLayout(activity, baseView)
        // Initialise baseView and blurView

        if (rootView != null) {
            // show no effect when root view passed from user end
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initBackgroundMode(BackgroundMode.DIM, baseView)
        } else {
            initBackgroundMode(BackgroundMode.BLUR, baseView)
        }

        initLayouts(baseView)
        container.overlay.add(baseView)
        initFadeInOutAnimation()

        // Initialise Center Icon Colors
        initCenterIcon()

        // Initialise VolumeUi Colors
        initVolumeUI()
    }

    private fun initBackgroundMode(backgroundMode: BackgroundMode, baseView: View) {
        when (backgroundMode) {
            BackgroundMode.BLUR -> {
                blurEffect = BlurEffectHelper(
                    activity,
                    blurRadius,
                    blurSampling,
                    canUseRenderEffect,
                    fadeInAnimationDuration,
                    fadeOutAnimationDuration,
                    baseView,
                    bitmapCallback
                )
            }
            BackgroundMode.DIM -> {
                val dimBackground: Drawable? =
                    ResourcesCompat.getDrawable(activity.resources, R.drawable.dim_window, null);
                dimBackground?.alpha = dimAlpha
                baseView.background = dimBackground
            }
        }
    }


    private fun initLayouts(view: View) {
        val volumeLinearLayout = baseView.findViewById<LinearLayout>(R.id.volumeLinearLayout)
        val carVolumeLinearLayout =
            baseView.findViewById<LinearLayout>(R.id.carVolumeLinearLayout)

        if (isRunningOnCar(activity)) {
            volumeLinearLayout.visibility = View.GONE
            carVolumeLinearLayout.visibility = View.VISIBLE
            txtVolumeLevel = carVolumeLinearLayout.findViewById(R.id.txtVolumeLevel)
            volumeIcon = carVolumeLinearLayout.findViewById(R.id.volumeTopIconImage)
        } else {
            txtVolumeLevel = volumeLinearLayout.findViewById(R.id.txtVolumeLevel)
            volumeIcon = volumeLinearLayout.findViewById(R.id.volumeTopIconImage)
        }

        audioBarLayout = view.findViewById(R.id.audioBarLayout)
        volumeBar = view.findViewById(R.id.audioBar)
        audioBaParams = volumeBar.layoutParams

        centerIconLayout = baseView.findViewById(R.id.midIconLayout)
        centerIconView = baseView.findViewById(R.id.middleIconView)
        centerIconImageView = baseView.findViewById(R.id.middleIconImageView)

        view.visibility = View.GONE
        audioBarLayout.visibility = View.GONE
        centerIconLayout.visibility = View.GONE
    }

    private fun isRunningOnCar(context: Context): Boolean =
        (context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).currentModeType == Configuration.UI_MODE_TYPE_CAR

    private fun initCenterIcon() {
        val primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary)
        val secondaryColor = ContextCompat.getColor(activity, R.color.colorSecondary)
        val circularFilledBackground = DrawableCompat.wrap(
            AppCompatResources.getDrawable(
                activity, R.drawable.circular_filled_background
            )!!
        )

        val circularOuterBackground = DrawableCompat.wrap(
            AppCompatResources.getDrawable(
                activity, R.drawable.circular_outer_background
            )!!
        )
        DrawableCompat.setTint(
            circularOuterBackground, tintColor ?: primaryColor
        )
        DrawableCompat.setTint(
            circularFilledBackground, secondaryColor
        )
        val layerDrawable =
            LayerDrawable(arrayOf(circularFilledBackground, circularOuterBackground))
        var outerRingWidth = 10
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(
            1, outerRingWidth, outerRingWidth, outerRingWidth, outerRingWidth
        )

        centerIconImageView.setColorFilter(secondaryColor)
        centerIconBackgroundDrawable = layerDrawable
    }

    private fun initVolumeUI() {
        val primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary)

        val viDrawable: Drawable = volumeIconDrawable ?: ContextCompat.getDrawable(
            activity, R.drawable.icon_volume_material
        ) ?: return
        val outerRing: Drawable =
            ContextCompat.getDrawable(activity, R.drawable.circular_ring) ?: return

        volumeIcon.background = outerRing
        volumeIcon.setImageDrawable(viDrawable)
        volumeIcon.setColorFilter(primaryColor)
        val color = tintColor
        if (color != null) {
            volumeBar.setColor(color)
            txtVolumeLevel.setTextColor(color)
            volumeIcon.setColorFilter(color)
            val myGrad: GradientDrawable = volumeIcon.background as GradientDrawable
            val width = (2 * Resources.getSystem().displayMetrics.density).toInt()
            myGrad.setStroke(width, color)
        }
    }

    fun showSwipeLeft() {
        var iconDrawable: Drawable? = iconSwipeLeftDrawable
        if (iconDrawable == null) {
            iconDrawable = ContextCompat.getDrawable(
                activity, R.drawable.icon_skip_previous_material
            )
        }
        animateActionIcon(iconDrawable)
    }

    fun showSwipeRight() {
        var iconDrawable: Drawable? = iconSwipeRightDrawable
        if (iconDrawable == null) {
            iconDrawable = ContextCompat.getDrawable(
                activity, R.drawable.icon_skip_next_material
            )
        }
        animateActionIcon(iconDrawable)
    }

    private fun showPause() {
        var iconDrawable: Drawable? = iconTapDrawable
        if (iconDrawable == null) {
            iconDrawable = ContextCompat.getDrawable(
                activity, R.drawable.icon_pause_material
            )
        }
        animateActionIcon(iconDrawable)
    }

    private fun showResume() {
        var iconDrawable: Drawable? = iconTapToggledDrawable
        if (iconDrawable == null) {
            iconDrawable = ContextCompat.getDrawable(
                activity, R.drawable.icon_play_material
            )
        }
        animateActionIcon(iconDrawable)
    }

    fun showEmptyBlurView() {
        // cancel all animations first
        centerIconFadeOutAnimation?.cancel()
        centerIconFadeInAnimation?.cancel()
        fadeInAnimation?.cancel()
        fadeOutAnimation?.cancel()

        // show Empty Overlay View
        audioBarLayout.visibility = View.GONE
        centerIconLayout.visibility = View.GONE
        fadeInAnimation?.start()
    }

    fun hideEmptyBlurView() {
        // Check if Both AudioBar and MidLayout are inVisible
        // and only Base View is Visible
        var isEmptyBlurViewActive =
            (baseView.visibility == View.VISIBLE) && (audioBarLayout.visibility != View.VISIBLE || centerIconLayout.visibility != View.VISIBLE)
        if (isEmptyBlurViewActive) {
            fadeOutAnimation?.start()
        }
    }

    fun testOverlay() {
        // animateActionIcon()
        showResume()
    }

    fun onTwoFingerTap() {
        if (audioManagerHelper.isAudioPlaying()) {
            showPause()
        } else {
            showResume()
        }
    }


    private fun animateActionIcon(iconDrawable: Drawable? = null) {
        audioBarLayout.visibility = View.GONE
        if (iconDrawable != null) centerIconImageView.setImageDrawable(iconDrawable)

        if (centerIconBackgroundDrawable != null) {
            centerIconImageView.background = centerIconBackgroundDrawable
        }

        centerIconLayout.visibility = View.VISIBLE
        animateZoomOut(centerIconImageView)

        // cancel all animations first
        centerIconFadeOutAnimation?.cancel()
        centerIconFadeInAnimation?.cancel()
        fadeInAnimation?.cancel()
        fadeOutAnimation?.cancel()
        // start new Animation
        if (baseView.visibility == View.VISIBLE) {
            centerIconFadeOutAnimation?.duration =
                (fadeInAnimationDuration + fadeOutAnimationDuration).toLong()
            centerIconFadeOutAnimation?.start()
        } else {
            centerIconFadeInAnimation?.start()
        }
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

    private fun updateDeviceVolume(swipeDirection: SwipeDirection): Double {
        when (swipeDirection) {
            SwipeDirection.UP -> {
                currentVolume = audioManagerHelper.getValidPercentage(currentVolume + volumeStep)
            }
            SwipeDirection.DOWN -> {
                currentVolume = audioManagerHelper.getValidPercentage(currentVolume - volumeStep)
            }
            else -> {}
        }
        audioManagerHelper.setVolumeByPercentage(currentVolume)
        return currentVolume
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

                centerIconLayout.visibility = View.GONE
                audioBarLayout.visibility = View.VISIBLE

                // Cancel ALl Animations
                centerIconFadeInAnimation?.cancel()
                centerIconFadeOutAnimation?.cancel()
                fadeOutAnimation?.cancel()
                // Start New Animation
                if (baseView.visibility != View.VISIBLE) {
                    fadeInAnimation?.start()
                }
            }
            GestureState.CHANGED -> {
                if (audioBarLayout.visibility == View.GONE) {
                    audioBarLayout.visibility = View.VISIBLE
                }
                // To Reduce Sensitivity , recognize gesture with gap of 25 Events
                val yPan: Float = event.y
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
        volumeBar.onTouchEvent(event.y, state)
    }


    fun updateVolumeViewFromKeyEvents(
        state: GestureState, swipeDirection: SwipeDirection
    ) {
        updateDeviceVolume(swipeDirection)
        //  val centreYAxis: Float = audioBarLayout.y + audioBarLayout.height / 2;
        //  val volumePercentage: Float = (currentVolume * 100).toFloat()
        // val currentYAxis: Float = (centreYAxis / 100) * volumePercentage
        //        var progressYAxis: Float = when (swipeDirection) {
        //            SwipeDirection.DOWN -> {
        //                (2 * centreYAxis) - currentYAxis
        //            }
        //            else -> {
        //                centreYAxis - currentYAxis
        //            }
        //        }

        // Set Volume Text in UI
        val roundedVolume = ((((currentVolume * 100) * 10.0) / 100) * 2.0).roundToInt() / 2.0

        when (state) {
            GestureState.BEGAN -> {
                centerIconLayout.visibility = View.GONE
                audioBarLayout.visibility = View.VISIBLE

                // Cancel ALl Animations
                centerIconFadeInAnimation?.cancel()
                centerIconFadeOutAnimation?.cancel()
                fadeOutAnimation?.cancel()
                // Start New Animation
                fadeInAnimation?.start()

                txtVolumeLevel.text = "$roundedVolume"
                // progressYAxis = centreYAxis
            }
            GestureState.CHANGED -> {
                txtVolumeLevel.text = "$roundedVolume"
            }
            GestureState.ENDED -> {
                fadeOutAnimation?.start()
            }
        }
        volumeBar.onTouchEvent(0f, state)
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
        centerIconFadeInAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 0.0f, 1f)
        centerIconFadeOutAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 1f, 0.0f)
        fadeInAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 0.0f, 1f)
        fadeOutAnimation = ObjectAnimator.ofFloat(baseView, "alpha", 1f, 0.0f)



        centerIconFadeInAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.show()
                baseView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                centerIconFadeOutAnimation?.start()
                setDefaultDuration()
            }
        })

        centerIconFadeOutAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.remove()
            }

            override fun onAnimationEnd(animation: Animator) {
                baseView.visibility = View.GONE
                setDefaultDuration()
            }
        })

        fadeOutAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.remove()
            }

            override fun onAnimationEnd(a: Animator) {
                baseView.visibility = View.GONE
                setDefaultDuration()
            }
        })

        fadeInAnimation?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                blurEffect?.show()
                baseView.visibility = View.VISIBLE
            }
        })
    }

    private fun setDefaultDuration() {
        centerIconFadeInAnimation?.duration = fadeInAnimationDuration.toLong()
        centerIconFadeOutAnimation?.duration = fadeOutAnimationDuration.toLong()
        fadeInAnimation?.duration = fadeInAnimationDuration.toLong()
        fadeOutAnimation?.duration = fadeOutAnimationDuration.toLong()
    }


    // Helper Methods
    private fun measureAndLayout(activity: Activity, toMeasure: View) {
        val dpHeight: Int
        val dpWidth: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                activity.getSystemService(WindowManager::class.java).currentWindowMetrics
            dpHeight = metrics.bounds.height()
            dpWidth = metrics.bounds.width()
        } else {
            val outMetrics = DisplayMetrics()
            @Suppress("DEPRECATION") activity.windowManager.defaultDisplay.getMetrics(outMetrics)
            dpHeight = outMetrics.heightPixels
            dpWidth = outMetrics.widthPixels
        }

        toMeasure.measure(
            View.MeasureSpec.makeMeasureSpec(dpWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(dpHeight, View.MeasureSpec.EXACTLY)
        )
        toMeasure.layout(0, 0, dpWidth, dpHeight)
    }

}