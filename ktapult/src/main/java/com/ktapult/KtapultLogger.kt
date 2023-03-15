package com.ktapult

import android.util.Log

internal object KtapultLogger {

    private const val TAG = "Ktapult"

    var enabled: Boolean = false

    fun d(message: String) {
        if (!enabled) {
            return
        }
        Log.d(TAG, message)
    }
}