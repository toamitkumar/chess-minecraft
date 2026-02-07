package com.knightscrusade.client.animation

import org.joml.Matrix4f

/**
 * A skeleton composed of a bone hierarchy.
 *
 * Manages the bone tree and provides methods to update world transforms.
 * The Knight skeleton has the hierarchy:
 * ```
 * root → body → head
 *             → arm_left
 *             → arm_right
 *             → hip → leg_front_left
 *                   → leg_front_right
 *                   → leg_back_left
 *                   → leg_back_right
 * ```
 */
class Skeleton {

    val bones = mutableMapOf<String, Bone>()
    var root: Bone? = null
        private set

    /**
     * Set the root bone of the skeleton.
     */
    fun setRoot(bone: Bone): Skeleton {
        root = bone
        registerBone(bone)
        return this
    }

    /**
     * Get a bone by name.
     */
    fun getBone(name: String): Bone? = bones[name]

    /**
     * Update all bone world transforms starting from the root.
     *
     * @param rootTransform Base transform (typically entity world position/rotation)
     */
    fun update(rootTransform: Matrix4f = Matrix4f()) {
        root?.updateWorldTransform(rootTransform)
    }

    /**
     * Reset all bones to their bind pose.
     */
    fun resetToBind() {
        bones.values.forEach { it.resetToBind() }
    }

    private fun registerBone(bone: Bone) {
        bones[bone.name] = bone
        for (child in bone.children) {
            registerBone(child)
        }
    }

    companion object {
        /**
         * Create the Knight skeleton with the standard bone hierarchy.
         */
        fun createKnightSkeleton(): Skeleton {
            val root = Bone("root")
            val body = Bone("body", bindPosition = org.joml.Vector3f(0f, 0.5f, 0f))
            val head = Bone("head", bindPosition = org.joml.Vector3f(0f, 0.7f, -0.1f))
            val armLeft = Bone("arm_left", bindPosition = org.joml.Vector3f(-0.37f, 0.2f, 0f))
            val armRight = Bone("arm_right", bindPosition = org.joml.Vector3f(0.37f, 0.2f, 0f))
            val hip = Bone("hip", bindPosition = org.joml.Vector3f(0f, 0f, 0f))
            val legFL = Bone("leg_front_left", bindPosition = org.joml.Vector3f(-0.14f, -0.5f, -0.1f))
            val legFR = Bone("leg_front_right", bindPosition = org.joml.Vector3f(0.14f, -0.5f, -0.1f))
            val legBL = Bone("leg_back_left", bindPosition = org.joml.Vector3f(-0.14f, -0.5f, 0.1f))
            val legBR = Bone("leg_back_right", bindPosition = org.joml.Vector3f(0.14f, -0.5f, 0.1f))

            root.addChild(body)
            body.addChild(head)
            body.addChild(armLeft)
            body.addChild(armRight)
            body.addChild(hip)
            hip.addChild(legFL)
            hip.addChild(legFR)
            hip.addChild(legBL)
            hip.addChild(legBR)

            return Skeleton().setRoot(root)
        }
    }
}
