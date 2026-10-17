#version 320 es

precision highp float;

uniform sampler2D uTexSampler;
in vec2 vTexCoord;

//grading parameters
uniform float uExposure;
uniform float uContrast;
uniform float uHighlights;
uniform float uShadows;
uniform float uWhites;
uniform float uBlacks;

out vec4 fragColor;

float decodeSdrChannel(float encoded);
vec3 decodeSdr(vec3 encodedRgb);
float encodeSdrChannel(float linearValue);
vec3 encodeSdr(vec3 linearRgb);
float getLuminance(vec3 linearRgb);
vec3 applyContrast(vec3 linearRgb, float contrastFactor);
vec3 applyHighlights(vec3 linearRgb, float highlightsEv);
vec3 applyShadows(vec3 linearRgb, float shadowsEv);
vec3 applyWhites(vec3 linearRgb, float whitesAmount);
vec3 applyBlacks(vec3 linearRgb, float blacksAmount);


void main() {
    vec4 sourceColor = texture(uTexSampler, vTexCoord);

    if (
        uExposure == 0.0 &&
        uContrast == 0.0 &&
        uHighlights == 0.0 &&
        uShadows == 0.0 &&
        uWhites == 0.0 &&
        uBlacks == 0.0
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
    float highlightsEv = clamp(uHighlights / 100.0, -1.0, 1.0);
    linearRgb = applyHighlights(linearRgb, highlightsEv);

    // shadows
    float shadowsEv = clamp(uShadows / 100.0, -1.0, 1.0);
    linearRgb = applyShadows(linearRgb, shadowsEv);

    // whites
    float whitesAmount = clamp(uWhites / 100.0, -1.0, 1.0);
    linearRgb = applyWhites(linearRgb, whitesAmount);

    // blacks
    float blacksAmount = clamp(uBlacks / 100.0, -1.0, 1.0);
    linearRgb = applyBlacks(linearRgb, blacksAmount);


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
    if(highlightsEv == 0.0) return linearRgb;

    float luminance = getLuminance(linearRgb);
    if(luminance <= 0.0) return linearRgb;


    const float pivot = 0.18f;
    const float startStops = 0.5f;
    const float endStops = 2.5f;

    float stops = log2(luminance / pivot);

    float highlightsMask = smoothstep(startStops, endStops, stops);

    float gain = exp2(highlightsEv * highlightsMask);

    return linearRgb * gain;
}

vec3 applyShadows(vec3 linearRgb, float shadowsEv) {
    if(shadowsEv == 0.0) return linearRgb;


    float luminance = getLuminance(linearRgb);
    if(luminance <= 0.0) return linearRgb;

    const float pivot = 0.18f;
    const float startStops = -2.5f;
    const float endStops = -0.5f;

    float stops = log2(luminance / pivot);

    float shadowsMask = 1.0 - smoothstep(startStops, endStops, stops);

    float gain = exp2(shadowsEv * shadowsMask);

    return linearRgb * gain;
}

vec3 applyWhites(vec3 linearRgb, float whitesAmount) {
    if(whitesAmount == 0.0) return linearRgb;

    float luminance = getLuminance(linearRgb);
    if(luminance <= 0.0) return linearRgb;

    const float startLuminance = 0.5;
    const float endLuminance = 1.0;
    const float maxAdjustment = 0.25;

    float whitesMask = smoothstep(
        startLuminance,
        endLuminance,
        luminance
    );

    float adjustment = maxAdjustment * whitesAmount;

    float adjustedLuminance = luminance + adjustment * whitesMask;

    float gain = adjustedLuminance / luminance;

    return linearRgb * gain;
}

vec3 applyBlacks(vec3 linearRgb, float blacksAmount) {
    if(blacksAmount == 0.0f) return linearRgb;

    float luminance = getLuminance(linearRgb);
    if(luminance <= 0.0) return linearRgb;

    const float startLuminance = 0.0f;
    const float endLuminance = 0.125f;
    const float maxAdjustment = 0.02f;

    float blacksMask = 1.0 - smoothstep(
        startLuminance,
        endLuminance,
        luminance
    );

    float adjustment = maxAdjustment * blacksAmount;

    return linearRgb + vec3(adjustment * blacksMask);
}