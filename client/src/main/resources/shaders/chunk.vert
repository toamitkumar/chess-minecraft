#version 410 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aColor;
layout(location = 2) in vec3 aNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;

out vec3 fragColor;
out vec3 fragNormal;
out vec3 fragPos;

void main() {
    vec4 worldPos = uModel * vec4(aPos, 1.0);
    gl_Position = uProjection * uView * worldPos;
    fragColor = aColor;
    // Transform normal by the model matrix (no non-uniform scaling, so this is fine)
    fragNormal = mat3(uModel) * aNormal;
    fragPos = worldPos.xyz;
}
