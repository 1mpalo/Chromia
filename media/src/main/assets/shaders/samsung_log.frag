#version 320 es

precision highp float;

uniform highp sampler2D uTexSampler;
in vec2 vTexCoord;

uniform float uExposure;
uniform float uContrast;
uniform float uHighlights;
uniform float uShadows;
uniform float uWhites;
uniform float uBlacks;
uniform float uVibrance;
uniform float uSaturation;
uniform vec3 uWhiteBalanceLmsScale;

out vec4 fragColor;

const mat3 BT2020_TO_BT709 = mat3(
    1.660491, -0.124550, -0.018151,
    -0.587641,  1.132900, -0.100579,
    -0.072850, -0.008349,  1.118730
);

const mat3 BT2020_TO_BRADFORD_LMS = mat3(
        0.6401244855, -0.0277091146,  0.0067827036,
        0.3055343440,  1.0542883623, -0.0119136256,
        -0.0042796502,  0.0138571352,  1.0948975178
);

const mat3 BRADFORD_LMS_TO_BT2020 = mat3(
        1.5427250656,  0.0406661469, -0.0091144289,
        -0.4469519689,  0.9365898415,  0.0129598644,
        0.0116867536, -0.0116946244,  0.9131278413
);

float decodeSamsungLogChannel(float code);
vec3 decodeSamsungLog(vec3 encodedLog);
float encodeBt709Channel(float linear709);
vec3 encodeBt709(vec3 linear709Rgb);
float getLuminance2020(vec3 linearRgb2020);
vec3 applyContrast(vec3 linearRgb2020, float contrastFactor);
vec3 applyHighlights(vec3 linearRgb2020, float highlightsEv);
vec3 applyShadows(vec3 linearRgb2020, float shadowsEv);
vec3 applyWhiteBalance(vec3 linearRgb2020);
vec3 applyWhites(vec3 linearRgb2020, float whitesAmount);
vec3 applyBlacks(vec3 linearRgb2020, float blacksAmount);
vec3 applySaturation(vec3 linearRgb2020, float saturationAmount);
vec3 applyVibrance(vec3 linearRgb2020, float vibranceAmount);

void main() {
    vec4 sourceColor = texture(uTexSampler, vTexCoord);
    vec3 linear2020 = decodeSamsungLog(sourceColor.rgb);

    linear2020 = applyWhiteBalance(linear2020);
    linear2020 *= exp2(uExposure);
    linear2020 = applyContrast(linear2020, exp2(uContrast / 100.0));

    float highlightsEv = clamp(uHighlights / 100.0, -1.0, 1.0);
    float shadowsEv = clamp(uShadows / 100.0, -1.0, 1.0);
    linear2020 = applyHighlights(linear2020, highlightsEv);
    linear2020 = applyShadows(linear2020, shadowsEv);

    float whitesAmount = clamp(uWhites / 100.0, -1.0, 1.0);
    float blacksAmount = clamp(uBlacks / 100.0, -1.0, 1.0);
    linear2020 = applyWhites(linear2020, whitesAmount);
    linear2020 = applyBlacks(linear2020, blacksAmount);

    float vibranceAmount = clamp(uVibrance / 100.0, -1.0, 1.0);
    float saturationAmount = clamp(uSaturation / 100.0, 0.0, 2.0);
    linear2020 = applyVibrance(linear2020, vibranceAmount);
    linear2020 = applySaturation(linear2020, saturationAmount);

    vec3 positive2020 = max(linear2020, vec3(0.0));
    float luminance = getLuminance2020(positive2020);
    vec3 mapped2020 = positive2020 / (1.0 + luminance);
    vec3 linear709 = clamp(BT2020_TO_BT709 * mapped2020, 0.0, 1.0);

    fragColor = vec4(encodeBt709(linear709), sourceColor.a);
}

float decodeSamsungLogChannel(float code) {
    if(code >= 0.206561909) {
        return pow(10.0, (code - 0.720504856) / 0.258984868) - 0.0003645;
    }

    if(code < 0.0) return -0.05;

    return 0.016904 - pow(10.0, (code + 0.24597) / -0.20942);
}

vec3 decodeSamsungLog(vec3 encodedLog) {
    return vec3(
        decodeSamsungLogChannel(encodedLog.r),
        decodeSamsungLogChannel(encodedLog.g),
        decodeSamsungLogChannel(encodedLog.b)
    );
}

float getLuminance2020(vec3 linearRgb2020) {
    return dot(linearRgb2020, vec3(0.2627, 0.6780, 0.0593));
}

float encodeBt709Channel(float linear709) {
    return linear709 < 0.018
        ? 4.5 * linear709
        : 1.099 * pow(linear709, 0.45) - 0.099;
}

vec3 encodeBt709(vec3 linear709Rgb) {
    return vec3(
        encodeBt709Channel(linear709Rgb.r),
        encodeBt709Channel(linear709Rgb.g),
        encodeBt709Channel(linear709Rgb.b)
    );
}

vec3 applyContrast(vec3 linearRgb2020, float contrastFactor) {

    if (contrastFactor == 1.0) {
        return linearRgb2020;
    }

    const float pivot = 0.18;

    float luminance = getLuminance2020(linearRgb2020);

    if (luminance <= 0.0) {
        return linearRgb2020;
    }

    float adjustedLuminance =
        pivot * pow(luminance / pivot, contrastFactor);

    float gain = adjustedLuminance / luminance;

    return linearRgb2020 * gain;
}

vec3 applyHighlights(vec3 linearRgb2020, float highlightsEv) {

    if(highlightsEv == 0.0) return linearRgb2020;

    float luminance = getLuminance2020(linearRgb2020);
    if(luminance <= 0.0) return linearRgb2020;


    const float pivot = 0.18f;
    const float startStops = 0.5f;
    const float endStops = 2.5f;

    float stops = log2(luminance / pivot);

    float highlightsMask = smoothstep(startStops, endStops, stops);

    float gain = exp2(highlightsEv * highlightsMask);

    return linearRgb2020 * gain;
}


vec3 applyShadows(vec3 linearRgb2020, float shadowsEv) {
    if(shadowsEv == 0.0) return linearRgb2020;


    float luminance = getLuminance2020(linearRgb2020);
    if(luminance <= 0.0) return linearRgb2020;

    const float pivot = 0.18f;
    const float startStops = -2.5f;
    const float endStops = -0.5f;

    float stops = log2(luminance / pivot);

    float shadowsMask = 1.0 - smoothstep(startStops, endStops, stops);

    float gain = exp2(shadowsEv * shadowsMask);

    return linearRgb2020 * gain;
}

vec3 applyWhiteBalance(vec3 linearRgb2020) {
    if (all(equal(uWhiteBalanceLmsScale, vec3(1.0)))) return linearRgb2020;

    vec3 lms = BT2020_TO_BRADFORD_LMS * linearRgb2020;
    lms *= uWhiteBalanceLmsScale;
    return BRADFORD_LMS_TO_BT2020 * lms;
}

vec3 applyWhites(vec3 linearRgb2020, float whitesAmount) {
    if (whitesAmount == 0.0) return linearRgb2020;
    float luminance = getLuminance2020(linearRgb2020);
    if (luminance <= 0.0) return linearRgb2020;

    float mask = smoothstep(0.5, 1.0, luminance);
    float adjustedLuminance = luminance + 0.25 * whitesAmount * mask;
    return linearRgb2020 * (adjustedLuminance / luminance);
}

vec3 applyBlacks(vec3 linearRgb2020, float blacksAmount) {
    if (blacksAmount == 0.0) return linearRgb2020;
    float luminance = getLuminance2020(linearRgb2020);
    if (luminance <= 0.0) return linearRgb2020;

    float mask = 1.0 - smoothstep(0.0, 0.125, luminance);
    return linearRgb2020 + vec3(0.02 * blacksAmount * mask);
}

vec3 applySaturation(vec3 linearRgb2020, float saturationAmount) {
    if (saturationAmount == 1.0) return linearRgb2020;
    float luminance = getLuminance2020(linearRgb2020);
    if (luminance <= 0.0) return linearRgb2020;

    vec3 gray = vec3(luminance);
    return gray + (linearRgb2020 - gray) * saturationAmount;
}

vec3 applyVibrance(vec3 linearRgb2020, float vibranceAmount) {
    if (vibranceAmount == 0.0) return linearRgb2020;

    float maxChannel = max(linearRgb2020.r, max(linearRgb2020.g, linearRgb2020.b));
    if (maxChannel <= 0.0) return linearRgb2020;

    float minChannel = min(linearRgb2020.r, min(linearRgb2020.g, linearRgb2020.b));
    float weight = clamp(minChannel / maxChannel, 0.0, 1.0);

    return applySaturation(linearRgb2020, 1.0 + vibranceAmount * weight);
}