package com.knightscrusade.client.animation

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * A bone in a skeletal hierarchy.
 *
 * Each bone has a local transform (position, rotation, scale) relative
 * to its parent. The world transform is computed by multiplying up
 * the hierarchy: worldTransform = parent.worldTransform * localTransform.
 *
 * @param name Unique bone name (e.g. "body", "head", "leg_front_left")
 * @param bindPosition Default position relative to parent
 * @param bindRotation Default rotation relative to parent
 */
class Bone(
    val name: String,
    val bindPosition: Vector3f = Vector3f(0f, 0f, 0f),
    val bindRotation: Quaternionf = Quaternionf()
) {
    var parent: Bone? = null
        private set
    val children = mutableListOf<Bone>()

    /** Current animated position (relative to parent). */
    val localPosition = Vector3f(bindPosition)
    /** Current animated rotation (relative to parent). */
    val localRotation = Quaternionf(bindRotation)

    /** Computed world-space transform (updated during skeleton update). */
    val worldTransform = Matrix4f()

    /**
     * Add a child bone to this bone.
     */
    fun addChild(child: Bone): Bone {
        child.parent = this
        children.add(child)
        return this
    }

    /**
     * Reset this bone to its bind pose.
     */
    fun resetToBind() {
        localPosition.set(bindPosition)
        localRotation.set(bindRotation)
    }

    /**
     * Compute the world transform by combining parent transform with local transform.
     *
     * @param parentTransform The parent bone's world transform (identity for root)
     */
    fun updateWorldTransform(parentTransform: Matrix4f) {
        worldTransform.set(parentTransform)
            .translate(localPosition)
            .rotate(localRotation)

        for (child in children) {
            child.updateWorldTransform(worldTransform)
        }
    }
}
