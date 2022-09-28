package com.navideck.gesturedeck_android.helper

import android.app.Activity
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class AudioManagerHelper(private var activity: Activity) {
    private var audioManager: AudioManager =
        activity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager

    fun getValidPercentage(volume: Double): Double {
        if (volume > 1) return 1.0
        if (volume < 0) return 0.0
        return volume
    }

    fun setVolumeByPercentage(volume: Double) =
        this.setMediaVolume((this.getValidPercentage(volume) * this.mediaMaxVolume).roundToInt())

    val mediaCurrentVolumeInPercentage: Double get() = (this.mediaCurrentVolume / this.mediaMaxVolume.toDouble() * 10000) / 10000

    private fun setMediaVolume(volumeIndex: Int) =
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)

    private val mediaMaxVolume: Int get() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private val mediaCurrentVolume: Int get() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)


    fun vibrate() {
        // perform Haptic Feedback on RootView
        // TODO : Test on Android V 5-10
        val view = activity.window.decorView.rootView
        if (!view.isHapticFeedbackEnabled) view.isHapticFeedbackEnabled = true
        view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
        )
    }
}