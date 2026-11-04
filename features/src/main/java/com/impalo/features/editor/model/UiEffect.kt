package com.impalo.features.editor.model

sealed class UiEffect {
    data class ShowToast(val message: String) : UiEffect()
}