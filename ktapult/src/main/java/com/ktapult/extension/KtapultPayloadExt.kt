package com.ktapult.extension

import androidx.compose.runtime.State
import com.ktapult.KtapultPayload

inline fun <reified T : KtapultPayload> KtapultPayload.whenTypeIs(then: (T) -> Unit): KtapultPayload {
    if (this is T) {
        then(this)
    }

    return this
}

inline fun <reified T : KtapultPayload> State<KtapultPayload>.whenTypeIs(then: (T) -> Unit): KtapultPayload {
    return value.whenTypeIs(then)
}