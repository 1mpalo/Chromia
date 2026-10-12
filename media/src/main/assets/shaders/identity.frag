#version 320 es

precision highp float;

uniform sampler2D uTexSampler;
in vec2 vTexCoord;

//grading parameters
uniform float uExposure;
uniform float uContrast;
uniform float uHighlights;

out vec4 fragColor;

float decodeSdrChannel(float encoded);
vec3 decodeSdr(vec3 encodedRgb);
float encodeSdrChannel(float linearValue);
vec3 encodeSdr(vec3 linearRgb);
float getLuminance(vec3 linearRgb);
vec3 applyContrast(vec3 linearRgb, float contrastFactor);
vec3 applyHighlights(vec3 linearRgb, float hightlightsEv);


void main() {
    vec4 sourceColor = texture(uTexSampler, vTexCoord);

    if (
        uExposure == 0.0 &&
        uContrast == 0.0 &&
        uHighlights == 0.0
    ) {
        fragColor = sourceColor;
        return;
    }

    vec3 linearRgb = decodeSdr(sourceColor.rgb);


    // exposure
    linearRgb *= exp2(uExposure);

    // contrast
    float contrastFactor = exp2(uContrast / 100.0);
    linearRgb = applyContrast(linearRgb, contrastFactor);

    // highlights
    float highlightsEv = clamp(uHighlights / 100.0, -1.0, 0.0);
    linearRgb = applyHighlights(linearRgb, highlightsEv);



    vec3 encodedRgb = encodeSdr(
        max(linearRgb, vec3(0.0))
    );


    fragColor = vec4(
        clamp(encodedRgb, 0.0, 1.0),
        sourceColor.a
    );
}

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

float getLuminance(vec3 linearRgb) {
    return dot(
        linearRgb,
        vec3(0.2126, 0.7152, 0.0722)
    );
}

vec3 applyContrast(vec3 linearRgb, float contrastFactor) {

    if (contrastFactor == 1.0) {
        return linearRgb;
    }

    const float pivot = 0.18;

    float luminance = getLuminance(linearRgb);


    if (luminance <= 0.0) {
        return linearRgb;
    }

    float adjustedLuminance =
    pivot * pow(luminance / pivot, contrastFactor);

    float gain = adjustedLuminance / luminance;

    return linearRgb * gain;
}

vec3 applyHighlights(vec3 linearRgb, float highlightsEv) {
    if(highlightsEv == 0.0) {
        return linearRgb;
    }

    float luminance = getLuminance(linearRgb);

    if(luminance <= 0.0) {
        return linearRgb;
    }

    const float pivot = 0.18f;
    const float startStops = 0.5f;
    const float endStops = 2.5f;

    float stops = log2(luminance / pivot);

    float highlightsMask = smoothstep(startStops, endStops, stops);

    float gain = exp2(hightlightsEv * highlightsMask);

    return linearRgb * gain;
}