#version 320 es

precision mediump float;

uniform sampler2D uTexSampler;
in vec2 vTexCoord;

out vec4 fragColor;

void main() {
    fragColor = texture(uTexSampler, vTexCoord);
}