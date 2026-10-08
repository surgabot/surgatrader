package com.surgatrader.feature.aura.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

/**
 * Text-to-speech engine powered by Android native TTS configured for natural Indonesian.
 */
class AndroidIndonesianVoiceEngine(
    context: Context,
    private val onSpeechStarted: () -> Unit = {},
    private val onSpeechCompleted: () -> Unit = {},
    private val onSpeechError: (String) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private var pendingText: String? = null
    private var pendingRate: Float = 1.0f
    private var pendingVolume: Float = 1.0f

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val localeId = Locale("id", "ID")
            val result = tts?.setLanguage(localeId)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to Indonesian alias or system default
                tts?.setLanguage(Locale("in", "ID"))
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onSpeechStarted()
                }

                override fun onDone(utteranceId: String?) {
                    onSpeechCompleted()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onSpeechError("TTS speech playback failed")
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    onSpeechError("TTS error code: $errorCode")
                }
            })

            isInitialized = true
            pendingText?.let { text ->
                speak(text, pendingRate, pendingVolume)
                pendingText = null
            }
        }
    }

    fun speak(text: String, rate: Float = 1.0f, volume: Float = 1.0f) {
        if (!isInitialized) {
            pendingText = text
            pendingRate = rate
            pendingVolume = volume
            return
        }

        stop()

        tts?.setSpeechRate(rate)
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
        }
        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }
}
