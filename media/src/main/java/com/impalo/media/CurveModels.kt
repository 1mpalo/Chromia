package com.impalo.media

import kotlinx.serialization.Serializable

@Serializable
enum class CurveChannel {
    RGB, R, G, B,
}

@Serializable
data class CurvePoint(
    val x: Float,
    val y: Float,
)

private val defaultCurvePoints = listOf(
    CurvePoint(0f, 0f),
    CurvePoint(0.5f, 0.5f),
    CurvePoint(1f, 1f),
)

fun defaultCurves(): Map<CurveChannel, List<CurvePoint>> =
    CurveChannel.entries.associateWith { defaultCurvePoints }