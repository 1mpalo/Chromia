#version 320 es

precision highp float;

uniform sampler2D uTexSampler;
in vec2 vTexCoord;

//grading parameters
uniform float uExposure;

out vec4 fragColor;

float decodeSdrChannel(float encoded) {
    if (encoded < 0.0812) {
        return encoded / 4.5;
    }

    return pow(
        (encoded + 0.099) / 1.099,
        1.0 / 0.45
    );
}

vec3 decodeSdr(vec3 encodedRgb) {
    return vec3(
        decodeSdrChannel(encodedRgb.r),
        decodeSdrChannel(encodedRgb.g),
        decodeSdrChannel(encodedRgb.b)
    );
}

float encodeSdrChannel(float linearValue) {
    if (linearValue < 0.018) {
        return linearValue * 4.5;
    }

    return 1.099 * pow(linearValue, 0.45) - 0.099;
}

vec3 encodeSdr(vec3 linearRgb) {
    return vec3(
        encodeSdrChannel(linearRgb.r),
        encodeSdrChannel(linearRgb.g),
        encodeSdrChannel(linearRgb.b)
    );
}

void main() {
    vec4 sourceColor = texture(uTexSampler, vTexCoord);

    if (uExposure == 0.0) {
        fragColor = sourceColor;
        return;
    }

    vec3 linearRgb = decodeSdr(sourceColor.rgb);

    float exposureMultiplier = exp2(uExposure);
    vec3 exposedRgb = linearRgb * exposureMultiplier;

    vec3 encodedRgb = encodeSdr(
        max(exposedRgb, vec3(0.0))
    );

    fragColor = vec4(
        clamp(encodedRgb, 0.0, 1.0),
        sourceColor.a
    );
}