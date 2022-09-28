package com.navideck.gesturedeck_android.model

import android.util.Log
import kotlin.math.atan2


enum class SwipeDirection {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        private fun fromAngle(angle: Double): SwipeDirection {
           // Log.e("Angle", angle.toString())
            return if (inRange(angle, 45f, 135f)) {
                UP
            } else if (inRange(angle, 0f, 45f) || inRange(angle, 315f, 360f)) {
                RIGHT
            } else if (inRange(angle, 225f, 315f)) {
                DOWN
            } else {
                LEFT
            }
        }

        private fun inRange(angle: Double, init: Float, end: Float): Boolean {
            return angle >= init && angle < end
        }

        fun direction(x1: Float, y1: Float, x2: Float, y2: Float): SwipeDirection {
            val rad = atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
            val angle = (rad * 180 / Math.PI + 180) % 360
            return fromAngle(angle)
        }

    }
}