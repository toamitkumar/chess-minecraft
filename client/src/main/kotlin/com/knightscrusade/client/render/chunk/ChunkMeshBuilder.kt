package com.knightscrusade.client.render.chunk

import com.knightscrusade.common.block.BlockType
import com.knightscrusade.common.chunk.ChunkColumn
import com.knightscrusade.common.chunk.SubChunk

/**
 * Builds optimized meshes from chunk block data using greedy meshing.
 *
 * Greedy meshing works by scanning each face direction slice-by-slice
 * and merging adjacent faces of the same block type into larger quads,
 * dramatically reducing vertex count compared to naive per-face meshing.
 *
 * Vertex format per vertex: [x, y, z, r, g, b, nx, ny, nz] (9 floats)
 * Two triangles per quad = 6 vertices = 54 floats per quad.
 */
object ChunkMeshBuilder {

    /**
     * Face direction definitions.
     * Each face has a normal and the two axes that define the face plane.
     */
    private enum class Face(
        val nx: Float, val ny: Float, val nz: Float,
        val axis0: Int, val axis1: Int, val axis2: Int, // sweep axis, u axis, v axis
        val positive: Boolean
    ) {
        POS_X( 1f, 0f, 0f, 0, 1, 2, true),
        NEG_X(-1f, 0f, 0f, 0, 1, 2, false),
        POS_Y( 0f, 1f, 0f, 1, 0, 2, true),
        NEG_Y( 0f,-1f, 0f, 1, 0, 2, false),
        POS_Z( 0f, 0f, 1f, 2, 0, 1, true),
        NEG_Z( 0f, 0f,-1f, 2, 0, 1, false);
    }

    /**
     * Build a mesh for a single sub-chunk.
     *
     * @param chunk The chunk column containing this sub-chunk
     * @param subChunkIndex Index of the sub-chunk within the column (0 = bottom)
     * @param worldOffsetX World-space X offset of the chunk
     * @param worldOffsetZ World-space Z offset of the chunk
     * @return Float array of vertex data, or empty array if sub-chunk has no visible faces
     */
    fun buildMesh(
        chunk: ChunkColumn,
        subChunkIndex: Int,
        worldOffsetX: Int,
        worldOffsetZ: Int
    ): FloatArray {
        val subChunk = chunk.getSubChunk(subChunkIndex)
        if (subChunk.isEmpty()) return FloatArray(0)

        val worldOffsetY = subChunkIndex * SubChunk.SIZE
        val vertices = mutableListOf<Float>()

        for (face in Face.entries) {
            buildFaceMesh(chunk, subChunkIndex, face, worldOffsetX, worldOffsetY, worldOffsetZ, vertices)
        }

        return vertices.toFloatArray()
    }

    /**
     * Uses greedy meshing to merge visible faces on one side of blocks.
     * Scans slice-by-slice along the face normal axis, then greedily
     * expands rectangles of matching block faces.
     */
    private fun buildFaceMesh(
        chunk: ChunkColumn,
        subChunkIndex: Int,
        face: Face,
        worldOffsetX: Int,
        worldOffsetY: Int,
        worldOffsetZ: Int,
        vertices: MutableList<Float>
    ) {
        val S = SubChunk.SIZE
        val subChunk = chunk.getSubChunk(subChunkIndex)

        // For each slice along the sweep axis
        for (d in 0 until S) {
            // Build a 2D mask of which faces are visible at this slice
            val mask = IntArray(S * S) // block ID or 0 (no face)

            for (v in 0 until S) {
                for (u in 0 until S) {
                    val pos = intArrayOf(0, 0, 0)
                    pos[face.axis0] = d
                    pos[face.axis1] = u
                    pos[face.axis2] = v

                    val blockType = subChunk.getBlock(pos[0], pos[1], pos[2])
                    if (blockType == BlockType.AIR || blockType.transparent) continue

                    // Check if the neighbor in the face direction is air/transparent
                    val neighborVisible = isNeighborTransparent(
                        chunk, subChunkIndex, pos[0], pos[1], pos[2], face
                    )

                    if (neighborVisible) {
                        mask[v * S + u] = blockType.id
                    }
                }
            }

            // Greedy merge: scan the mask and expand rectangles
            val visited = BooleanArray(S * S)

            for (v in 0 until S) {
                for (u in 0 until S) {
                    val idx = v * S + u
                    if (mask[idx] == 0 || visited[idx]) continue

                    val blockId = mask[idx]
                    val blockType = BlockType.fromId(blockId)

                    // Expand width (along u axis)
                    var width = 1
                    while (u + width < S && mask[v * S + u + width] == blockId && !visited[v * S + u + width]) {
                        width++
                    }

                    // Expand height (along v axis)
                    var height = 1
                    var canExpand = true
                    while (v + height < S && canExpand) {
                        for (du in 0 until width) {
                            val checkIdx = (v + height) * S + u + du
                            if (mask[checkIdx] != blockId || visited[checkIdx]) {
                                canExpand = false
                                break
                            }
                        }
                        if (canExpand) height++
                    }

                    // Mark visited
                    for (dv in 0 until height) {
                        for (du in 0 until width) {
                            visited[(v + dv) * S + u + du] = true
                        }
                    }

                    // Emit quad
                    emitQuad(
                        face, d, u, v, width, height,
                        worldOffsetX, worldOffsetY, worldOffsetZ,
                        blockType, vertices
                    )
                }
            }
        }
    }

    /**
     * Check if the block adjacent to (x, y, z) in the given face direction
     * is transparent (and thus this face should be visible).
     */
    private fun isNeighborTransparent(
        chunk: ChunkColumn,
        subChunkIndex: Int,
        x: Int, y: Int, z: Int,
        face: Face
    ): Boolean {
        var nx = x
        var ny = y + subChunkIndex * SubChunk.SIZE
        var nz = z

        when (face) {
            Face.POS_X -> nx++
            Face.NEG_X -> nx--
            Face.POS_Y -> ny++
            Face.NEG_Y -> ny--
            Face.POS_Z -> nz++
            Face.NEG_Z -> nz--
        }

        // If neighbor is outside this chunk column, treat as air (visible face)
        if (nx < 0 || nx >= SubChunk.SIZE || nz < 0 || nz >= SubChunk.SIZE) return true
        if (ny < 0 || ny >= ChunkColumn.HEIGHT) return true

        val neighborBlock = chunk.getBlock(nx, ny, nz)
        return !neighborBlock.solid
    }

    /**
     * Emit a quad (2 triangles, 6 vertices) for a greedy-merged face.
     *
     * @param face The face direction
     * @param d Position along the sweep axis
     * @param u Start position on u axis
     * @param v Start position on v axis
     * @param width Size along u axis
     * @param height Size along v axis
     */
    private fun emitQuad(
        face: Face,
        d: Int, u: Int, v: Int,
        width: Int, height: Int,
        worldOffsetX: Int, worldOffsetY: Int, worldOffsetZ: Int,
        blockType: BlockType,
        vertices: MutableList<Float>
    ) {
        // Calculate the 4 corners of the quad in world space
        val corners = Array(4) { FloatArray(3) }

        // The quad lies on the plane perpendicular to the face normal
        val offset = if (face.positive) d + 1 else d

        // Corner 0: (u, v)
        // Corner 1: (u + width, v)
        // Corner 2: (u + width, v + height)
        // Corner 3: (u, v + height)
        for (i in 0 until 4) {
            val cu = if (i == 1 || i == 2) u + width else u
            val cv = if (i == 2 || i == 3) v + height else v

            corners[i][face.axis0] = offset.toFloat()
            corners[i][face.axis1] = cu.toFloat()
            corners[i][face.axis2] = cv.toFloat()

            // Add world offset
            corners[i][0] += worldOffsetX
            corners[i][1] += worldOffsetY
            corners[i][2] += worldOffsetZ
        }

        val r = blockType.color[0]
        val g = blockType.color[1]
        val b = blockType.color[2]
        val nx = face.nx
        val ny = face.ny
        val nz = face.nz

        // Triangle 1: 0, 1, 2
        addVertex(vertices, corners[0], r, g, b, nx, ny, nz)
        addVertex(vertices, corners[1], r, g, b, nx, ny, nz)
        addVertex(vertices, corners[2], r, g, b, nx, ny, nz)

        // Triangle 2: 0, 2, 3
        addVertex(vertices, corners[0], r, g, b, nx, ny, nz)
        addVertex(vertices, corners[2], r, g, b, nx, ny, nz)
        addVertex(vertices, corners[3], r, g, b, nx, ny, nz)
    }

    private fun addVertex(
        vertices: MutableList<Float>,
        pos: FloatArray,
        r: Float, g: Float, b: Float,
        nx: Float, ny: Float, nz: Float
    ) {
        vertices.add(pos[0])
        vertices.add(pos[1])
        vertices.add(pos[2])
        vertices.add(r)
        vertices.add(g)
        vertices.add(b)
        vertices.add(nx)
        vertices.add(ny)
        vertices.add(nz)
    }
}
