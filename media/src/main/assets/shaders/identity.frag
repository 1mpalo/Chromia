#version 320 es

precision mediump float;

uniform sampler2D uTexSampler;
in vec2 vTexCoord;

//grading parameters
uniform float uExposure;

out vec4 fragColor;

void main() {
    vec4 color = texture(uTexSampler, vTexCoord);
    float exposureMultiplier = exp2(uExposure);

    vec3 adjustedRgb = color.rgb * exposureMultiplier;
    fragColor = vec4(adjustedRgb, color.a);
}