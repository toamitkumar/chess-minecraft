#version 410 core

in vec3 fragColor;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 FragColor;

// Simple directional light (sun)
const vec3 lightDir = normalize(vec3(0.3, 1.0, 0.5));
const vec3 ambientColor = vec3(0.3, 0.3, 0.35);
const vec3 lightColor = vec3(1.0, 0.98, 0.9);

void main() {
    // Diffuse lighting
    float diff = max(dot(fragNormal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 result = (ambientColor + diffuse) * fragColor;

    // Fog (distance-based, subtle)
    float dist = length(fragPos);
    float fogFactor = clamp(1.0 - (dist - 100.0) / 150.0, 0.0, 1.0);
    vec3 fogColor = vec3(0.6, 0.7, 0.85);
    result = mix(fogColor, result, fogFactor);

    FragColor = vec4(result, 1.0);
}
