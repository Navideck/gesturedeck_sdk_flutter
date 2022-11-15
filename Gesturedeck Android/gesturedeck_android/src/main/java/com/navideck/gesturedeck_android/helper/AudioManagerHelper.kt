package com.navideck.gesturedeck_android.helper

import android.app.Activity
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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

    fun isAudioPlaying(): Boolean {
        return audioManager.isMusicActive
    }

    fun setVolumeByPercentage(volume: Double) =
        this.setMediaVolume((this.getValidPercentage(volume) * this.mediaMaxVolume).roundToInt())

    val mediaCurrentVolumeInPercentage: Double get() = (this.mediaCurrentVolume / this.mediaMaxVolume.toDouble() * 10000) / 10000

    private fun setMediaVolume(volumeIndex: Int) =
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)

    private val mediaMaxVolume: Int get() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private val mediaCurrentVolume: Int get() = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    fun vibrate(vibrationDuration: Long = 3) {
        if (Build.VERSION.SDK_INT >= 26) {
            val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager =
                    activity.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                activity.getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    vibrationDuration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {
            @Suppress("DEPRECATION")
            (activity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(vibrationDuration)
        }
    }
}