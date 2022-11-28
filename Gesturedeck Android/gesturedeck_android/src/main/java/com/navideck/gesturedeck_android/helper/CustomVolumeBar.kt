package com.navideck.gesturedeck_android.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.navideck.gesturedeck_android.R
import com.navideck.gesturedeck_android.model.GestureState

private const val TAG = "VolumeBar"

class VolumeBar(
    context: Context?,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var startY = 0f
    private var endY = 0f
    private var xAxis = 0f

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color =
            context?.let { ContextCompat.getColor(it, R.color.colorPrimary) } ?: Color.BLUE
        mPaint.strokeWidth = 150f
    }

    fun setWidth(width: Int) {
        mPaint.strokeWidth = width.toFloat()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawLine(xAxis, startY, xAxis, endY, mPaint)
        super.onDraw(canvas)
    }

    fun setBarX(barAxis: Int) {
        xAxis = barAxis.toFloat()
    }

    fun onTouchEvent(yAxis: Float, gestureState: GestureState) {
        when (gestureState) {
            GestureState.BEGAN -> {
                startY = yAxis
                endY = yAxis
                invalidate()
            }
            GestureState.CHANGED -> {
                endY = yAxis
                invalidate()
            }
            GestureState.ENDED -> {

            }
        }
    }
}