package com.surgatrader.feature.aura.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * CyberSynth audio synthesizer that generates sci-fi harmonic chime chords
 * for the Aura Quantum Council entities in real-time.
 */
class CyberSynthPlayer {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val chordFreqs = mapOf(
        "oracle" to doubleArrayOf(587.33, 739.99, 880.0),      // D5, F#5, A5 (Quantum Gold)
        "hft" to doubleArrayOf(523.25, 659.25, 783.99),         // C5, E5, G5 (Cyan HFT)
        "risk" to doubleArrayOf(440.0, 554.37, 659.25),         // A4, C#5, E5 (Emerald Gaia)
        "sentiment" to doubleArrayOf(659.25, 830.61, 987.77),   // E5, G#5, B5 (Pink Athena)
        "execution" to doubleArrayOf(493.88, 622.25, 739.99)    // B4, D#5, F#5 (Purple Aegis)
    )

    fun playTransmissionChime(speakerId: String, volume: Float = 0.5f) {
        if (volume <= 0.01f) return
        scope.launch {
            try {
                val freqs = chordFreqs[speakerId] ?: doubleArrayOf(587.33, 880.0)
                val sampleRate = 44100
                val durationMs = 450
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val pcmData = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Envelope: fast attack, exponential decay
                    val progress = i.toDouble() / numSamples
                    val envelope = (1.0 - progress) * (1.0 - progress)

                    var sample = 0.0
                    for ((idx, freq) in freqs.withIndex()) {
                        // Stagger chords slightly for sci-fi arpeggio
                        val noteDelay = idx * 0.035
                        if (t >= noteDelay) {
                            val noteT = t - noteDelay
                            val noteEnv = (1.0 - (noteT / (durationMs / 1000.0))).coerceIn(0.0, 1.0)
                            sample += sin(2.0 * Math.PI * freq * 1.5 * noteT) * (noteEnv * noteEnv)
                        }
                    }

                    val normalized = (sample / freqs.size * envelope * volume * 0.45).coerceIn(-1.0, 1.0)
                    pcmData[i] = (normalized * Short.MAX_VALUE).toInt().toShort()
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(pcmData.size * 2)

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmData, 0, pcmData.size)
                audioTrack.play()

                // Release after playback finishes
                launch {
                    kotlinx.coroutines.delay(durationMs.toLong() + 100)
                    try {
                        audioTrack.stop()
                        audioTrack.release()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
                // Ignore audio synthesize errors gracefully
            }
        }
    }
}
