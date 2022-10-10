package com.navideck.gesturedeck_android.helper

import android.os.CountDownTimer

/// [EventTimer] maintains a Single Timer Instance , and manage state of that Timer
class EventTimer {
    private var timer: CountDownTimer? = null

    var isActive: Boolean = false

    fun start(duration: Long = 1000, onComplete: () -> Unit) {
        timer?.cancel()
        timer = object : CountDownTimer(duration, 10) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                onComplete.invoke()
                isActive = false
            }
        }
        isActive = true
        timer?.start()
    }

    fun cancel() {
        if (isActive) {
            timer?.cancel()
            isActive = false
        }
    }
}
