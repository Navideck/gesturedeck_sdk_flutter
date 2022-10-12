package com.navideck.gesturedeck_android.helper

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.view.allViews
import com.navideck.gesturedeck_android.R
import jp.wasabeef.blurry.Blurry

private const val TAG = "BlurEffectHelper"

class BlurEffectHelper(
    private var activity: Activity,
    private var blurRadius: Int = 25,
    private var blurSampling: Int = 5,
    private var canUseRenderEffect: Boolean = false,
    private var blurInAnimationDuration: Int = 100,
    private val blurOutAnimationDuration: Int = 500,
    private var baseView: View,
    private var bitmapCallback: (() -> Bitmap?)? = null,
) {
    private var rootView: ViewGroup = activity.window.decorView.rootView as ViewGroup
    private var firstViewOnRoot: View? = null
    private var isBlurViewActive = false
    private var blurFadeOutAnimation: ValueAnimator? = null
    private var isBlurFadeInProgress: Boolean = false

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && rootView.allViews.toList()
                .isNotEmpty() && canUseRenderEffect
        ) {
            firstViewOnRoot = rootView.allViews.toList()[1]
        } else {
            blurFadeOutAnimation = ValueAnimator.ofFloat(1f, 0f)
            blurFadeOutAnimation?.duration = blurOutAnimationDuration.toLong()
        }
    }

    fun show() {
        if (isBlurViewActive && !isBlurFadeInProgress) return else isBlurViewActive = true

        // TODO : Experimental , Check With Flutter
        if (canUseRenderEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && firstViewOnRoot != null) {
            firstViewOnRoot?.setRenderEffect(
                RenderEffect.createBlurEffect(
                    blurRadius.toFloat(), blurRadius.toFloat(), Shader.TileMode.MIRROR
                )
            )
            return
        }

        if (isBlurFadeInProgress) {
            blurFadeOutAnimation?.reverse()
        } else {
            if (isEmptyBlurViewVisible()) Blurry.delete(rootView)

            //TODO : Reduce delay in getting bitmap from flutter as much as possible
            val color = Color.argb(120, 100, 100, 100)
            val bitmap: Bitmap? = bitmapCallback?.invoke()
            if (bitmap != null) {
                val imageView: ImageView = baseView.findViewById(R.id.backgroundImageView)
                Blurry.with(activity)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .color(color)
                    .animate(blurInAnimationDuration)
                    .from(bitmap)
                    .into(imageView)
            } else {
                Blurry.with(activity)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .color(color)
                    .animate(blurInAnimationDuration)
                    .onto(rootView)
            }
        }
    }


    private fun isEmptyBlurViewVisible(): Boolean {
        val blurryView: View =
            rootView.findViewWithTag(Blurry::class.java.simpleName) ?: return false
        val audioBarLayout: View = baseView.findViewById(R.id.audioBarLayout)
        val centerIconLayout: View = baseView.findViewById(R.id.midIconLayout)
        return (baseView.visibility == View.VISIBLE) && (audioBarLayout.visibility != View.VISIBLE && centerIconLayout.visibility != View.VISIBLE)
    }


    fun remove() {
        isBlurViewActive = false

        // To Remove BlurView rendered from Android 12 Api
        if (canUseRenderEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && firstViewOnRoot != null) {
            firstViewOnRoot?.setRenderEffect(null)
            return
        }

        // To Remove BlurView from BLURRY library
        val blurryView: View = rootView.findViewWithTag(Blurry::class.java.simpleName) ?: return
        fadeOutBlurView(blurryView)
    }

    private fun fadeOutBlurView(blurryView: View) {
        blurFadeOutAnimation?.cancel()
        blurFadeOutAnimation?.removeAllUpdateListeners()
        blurFadeOutAnimation?.removeAllListeners()

        blurFadeOutAnimation?.addListener(onStart = {
            isBlurFadeInProgress = false
        }, onEnd = {
            Blurry.delete(rootView)
            isBlurViewActive = false
            isBlurFadeInProgress = false
        })

        blurFadeOutAnimation?.addUpdateListener { animation ->
            blurryView.alpha = animation.animatedValue as Float
        }

        blurFadeOutAnimation?.start()
    }

}