package com.knightscrusade.client.audio

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.system.MemoryUtil.NULL
import org.slf4j.LoggerFactory

/**
 * Manages OpenAL audio initialization and playback.
 *
 * For M0, this provides basic audio validation: initializes OpenAL,
 * generates a simple tone programmatically, and plays it to verify
 * 3D audio panning works. Full audio with sound files comes in later milestones.
 */
class AudioManager {

    private val logger = LoggerFactory.getLogger(AudioManager::class.java)

    private var device: Long = NULL
    private var context: Long = NULL
    private var initialized = false

    /** Buffer and source for the validation tone. */
    private var validationBuffer: Int = 0
    private var validationSource: Int = 0

    /**
     * Initialize OpenAL. Returns true if initialization succeeded.
     */
    fun init(): Boolean {
        return try {
            // Open default audio device
            device = alcOpenDevice(null as java.nio.ByteBuffer?)
            if (device == NULL) {
                logger.warn("OpenAL: No audio device found")
                return false
            }

            // Create context
            context = alcCreateContext(device, null as IntArray?)
            if (context == NULL) {
                logger.warn("OpenAL: Failed to create context")
                alcCloseDevice(device)
                return false
            }

            alcMakeContextCurrent(context)
            AL.createCapabilities(ALC.createCapabilities(device))

            // Set listener at origin
            alListener3f(AL_POSITION, 0f, 0f, 0f)
            alListener3f(AL_VELOCITY, 0f, 0f, 0f)

            initialized = true
            logger.info("OpenAL initialized: ${alGetString(AL_RENDERER)}")
            true
        } catch (e: Exception) {
            logger.error("Failed to initialize OpenAL", e)
            false
        }
    }

    /**
     * Play a short validation tone to verify audio works.
     * Generates a 440Hz sine wave programmatically.
     */
    fun playValidationTone() {
        if (!initialized) return

        // Generate a short sine wave (440Hz, 0.3 seconds, 44100 sample rate)
        val sampleRate = 44100
        val duration = 0.3f
        val frequency = 440f
        val numSamples = (sampleRate * duration).toInt()
        val samples = ShortArray(numSamples)

        for (i in samples.indices) {
            val t = i.toFloat() / sampleRate
            val amplitude = 0.3f * Short.MAX_VALUE
            // Fade in/out to avoid clicks
            val envelope = when {
                i < 1000 -> i / 1000f
                i > numSamples - 1000 -> (numSamples - i) / 1000f
                else -> 1f
            }
            samples[i] = (amplitude * envelope * kotlin.math.sin(2.0 * Math.PI * frequency * t)).toInt().toShort()
        }

        // Upload to OpenAL buffer
        validationBuffer = alGenBuffers()
        val buffer = org.lwjgl.system.MemoryUtil.memAllocShort(samples.size)
        buffer.put(samples).flip()
        alBufferData(validationBuffer, AL_FORMAT_MONO16, buffer, sampleRate)
        org.lwjgl.system.MemoryUtil.memFree(buffer)

        // Create source and play
        validationSource = alGenSources()
        alSourcei(validationSource, AL_BUFFER, validationBuffer)
        alSource3f(validationSource, AL_POSITION, 2f, 0f, 0f) // Slightly right for 3D panning
        alSourcef(validationSource, AL_GAIN, 0.5f)
        alSourcePlay(validationSource)

        logger.info("OpenAL validation: playing 440Hz tone")
    }

    /**
     * Update listener position (call each frame with camera position).
     */
    fun updateListener(x: Float, y: Float, z: Float) {
        if (!initialized) return
        alListener3f(AL_POSITION, x, y, z)
    }

    /**
     * Clean up OpenAL resources.
     */
    fun cleanup() {
        if (!initialized) return

        if (validationSource != 0) {
            alSourceStop(validationSource)
            alDeleteSources(validationSource)
        }
        if (validationBuffer != 0) {
            alDeleteBuffers(validationBuffer)
        }

        alcMakeContextCurrent(NULL)
        alcDestroyContext(context)
        alcCloseDevice(device)

        initialized = false
        logger.info("OpenAL cleaned up")
    }
}
