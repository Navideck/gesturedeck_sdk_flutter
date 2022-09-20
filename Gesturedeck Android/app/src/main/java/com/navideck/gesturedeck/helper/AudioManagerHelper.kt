package com.navideck.gesturedeck.helper

import android.app.Activity
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class AudioManagerHelper(activity: Activity) {
    private var audioManager: AudioManager

    init {
        this.audioManager =
            activity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
    }

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
}