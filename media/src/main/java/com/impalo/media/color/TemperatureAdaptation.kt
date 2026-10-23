package com.impalo.media.color

import kotlin.math.abs

object TemperatureAdaptation {

    private const val MAX_TINT_Y_OFFSET = 0.02

    private val neutralWhite = whiteXyz(
        x = 0.31270,
        y = 0.32900
    )

    private val warmLightWhite = whiteXyz(
        x = 0.34567,
        y = 0.35850
    )

    private val coolLightWhite = whiteXyz(
        x = 0.28315,
        y = 0.29711
    )

    fun calculateLmsScale(
        temperature: Float,
        tint: Float
    ): FloatArray {
        val normalizedTemperature = (
            temperature.coerceIn(-100f, 100f) / 100f
        ).toDouble()

        val normalizedTint = (
            tint.coerceIn(-100f, 100f) / 100f
        ).toDouble()

        if (normalizedTemperature == 0.0 && normalizedTint == 0.0) {
            return floatArrayOf(1f, 1f, 1f)
        }

        val endpoint = if (normalizedTemperature > 0.0) {
            coolLightWhite
        } else {
            warmLightWhite
        }

        val amount = abs(normalizedTemperature)

        val temperatureWhite = DoubleArray(3) { index ->
            neutralWhite[index] +
                (endpoint[index] - neutralWhite[index]) * amount
        }

        val sourceWhite = if (normalizedTint == 0.0) {
            temperatureWhite
        } else {

            val sum = temperatureWhite.sum()

            val chromaticityX = temperatureWhite[0] / sum
            val chromaticityY = temperatureWhite[1] / sum

            val adjustedY = chromaticityY +
                    normalizedTint * MAX_TINT_Y_OFFSET

            whiteXyz(
                x = chromaticityX,
                y = adjustedY
            )
        }

        val sourceLms = xyzToLms(sourceWhite)
        val targetLms = xyzToLms(neutralWhite)

        return FloatArray(3) { index ->
            (targetLms[index] / sourceLms[index]).toFloat()
        }
    }

    private fun whiteXyz(
        x: Double,
        y: Double
    ): DoubleArray {
        return doubleArrayOf(
            x / y,
            1.0,
            (1.0 - x - y) / y
        )
    }

    private fun xyzToLms(
        xyz: DoubleArray
    ): DoubleArray {
        val x = xyz[0]
        val y = xyz[1]
        val z = xyz[2]

        return doubleArrayOf(
            0.8951 * x + 0.2664 * y - 0.1614 * z,
            -0.7502 * x + 1.7135 * y + 0.0367 * z,
            0.0389 * x - 0.0685 * y + 1.0296 * z
        )
    }
}