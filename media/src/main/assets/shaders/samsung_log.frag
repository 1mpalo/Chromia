#version 320 es

precision highp float;

uniform highp sampler2D uTexSampler;
in vec2 vTexCoord;

uniform float uExposure;

out vec4 fragColor;

const mat3 BT2020_TO_BT709 = mat3(
    1.660491, -0.124550, -0.018151,
    -0.587641,  1.132900, -0.100579,
    -0.072850, -0.008349,  1.118730
);

float decodeSamsungLogChannel(float code);
vec3 decodeSamsungLog(vec3 encodedLog);
float encodeBt709Channel(float linear709);
vec3 encodeBt709(vec3 linear709Rgb);
float getLuminance2020(vec3 linearRgb);

void main() {
    vec4 sourceColor = texture(uTexSampler, vTexCoord);
    vec3 linear2020 = decodeSamsungLog(sourceColor.rgb);

    linear2020 *= exp2(uExposure);
    vec3 positive2020 = max(linear2020, vec3(0.0));
    float luminance = getLuminance2020(positive2020);
    vec3 mapped2020 = positive2020 / (1.0 + luminance);
    vec3 linear709 = clamp(BT2020_TO_BT709 * mapped2020, 0.0, 1.0);

    fragColor = vec4(
        encodeBt709(linear709),
        sourceColor.a
    );
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

float getLuminance2020(vec3 linearRgb) {
    return dot(linearRgb, vec3(0.2627, 0.6780, 0.0593));
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