package com.knightscrusade.client.animation

/**
 * State machine for managing animation transitions.
 *
 * Supports three states for M0: IDLE, WALK, RUN. Transitions between
 * states based on entity velocity, with configurable cross-fade duration.
 *
 * The state machine blends between the current and previous clip during
 * transitions to avoid jarring animation pops.
 */
class AnimationStateMachine {

    enum class State { IDLE, WALK, RUN }

    private val clips = mutableMapOf<State, AnimationClip>()
    private var currentState: State = State.IDLE
    private var previousState: State? = null
    private var stateTime: Float = 0f
    private var crossfadeTime: Float = 0f
    private var crossfadeDuration: Float = 0.2f

    /**
     * Register an animation clip for a state.
     */
    fun addClip(state: State, clip: AnimationClip) {
        clips[state] = clip
    }

    /**
     * Get the current animation state.
     */
    fun getCurrentState(): State = currentState

    /**
     * Transition to a new state with cross-fade.
     */
    fun transitionTo(newState: State) {
        if (newState == currentState) return
        previousState = currentState
        currentState = newState
        crossfadeTime = crossfadeDuration
        stateTime = 0f
    }

    /**
     * Update the state machine based on entity speed.
     *
     * @param horizontalSpeed Entity's horizontal speed in blocks/sec
     * @param deltaTime Time since last frame
     */
    fun update(horizontalSpeed: Double, deltaTime: Float) {
        // Determine target state from speed
        val targetState = when {
            horizontalSpeed > 5.0 -> State.RUN
            horizontalSpeed > 0.5 -> State.WALK
            else -> State.IDLE
        }

        if (targetState != currentState) {
            transitionTo(targetState)
        }

        stateTime += deltaTime

        // Tick crossfade
        if (crossfadeTime > 0f) {
            crossfadeTime = (crossfadeTime - deltaTime).coerceAtLeast(0f)
        }
    }

    /**
     * Apply the current animation state to the skeleton.
     *
     * @param skeleton The skeleton to animate
     */
    fun apply(skeleton: Skeleton) {
        val currentClip = clips[currentState] ?: return

        if (crossfadeTime > 0f && previousState != null) {
            // Cross-fade: blend previous clip out, current clip in
            val blendWeight = 1f - (crossfadeTime / crossfadeDuration)
            val prevClip = clips[previousState!!]
            if (prevClip != null) {
                skeleton.resetToBind()
                prevClip.sample(skeleton, stateTime, 1f - blendWeight)
                currentClip.sample(skeleton, stateTime, blendWeight)
            } else {
                currentClip.sample(skeleton, stateTime)
            }
        } else {
            currentClip.sample(skeleton, stateTime)
        }
    }
}
