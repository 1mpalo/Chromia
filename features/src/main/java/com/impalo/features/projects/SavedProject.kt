package com.impalo.features.projects

import com.impalo.media.GradingSettings
import kotlinx.serialization.Serializable

@Serializable
data class SavedProject (
    val id: String,
    val videoUri: String,
    val displayName: String,
    val updatedAtMs: Long,
    val grading: GradingSettings
)
