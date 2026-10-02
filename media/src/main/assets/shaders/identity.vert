#version 320 es

in vec4 aFramePosition;
out vec2 vTexCoord;

void main() {
    gl_Position = aFramePosition;
    vTexCoord = (aFramePosition.xy + 1.0) * 0.5;
}