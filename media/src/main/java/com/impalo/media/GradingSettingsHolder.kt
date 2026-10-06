package com.impalo.media

import java.util.concurrent.atomic.AtomicReference

class GradingSettingsHolder(
    initial: GradingSettings = GradingSettings()
) {
    private val current = AtomicReference(initial)

    fun update(settings: GradingSettings) {
        current.set(settings)
    }

    fun snapshot() : GradingSettings = current.get()
}