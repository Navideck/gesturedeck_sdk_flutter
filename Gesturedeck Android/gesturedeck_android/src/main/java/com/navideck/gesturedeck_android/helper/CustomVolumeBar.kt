package com.navideck.gesturedeck_android.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.navideck.gesturedeck_android.R
import com.navideck.gesturedeck_android.model.GestureState

private const val TAG = "CustomVolumeBar"

class CustomVolumeBar(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var startY = 0f
    private var endY = 0f

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = context?.let { ContextCompat.getColor(it, R.color.colorPrimary) } ?: Color.BLUE
        mPaint.strokeWidth = 150f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawLine(0f, startY, 0f, endY, mPaint)
        super.onDraw(canvas)
    }

    fun onTouchEvent(event: MotionEvent, gestureState: GestureState) {
        when (gestureState) {
            GestureState.BEGAN -> {
                startY = event.y
                endY = event.y
                invalidate()
            }
            GestureState.CHANGED -> {
                endY = event.y
                invalidate()
            }
            GestureState.ENDED -> {

            }
        }
    }
}