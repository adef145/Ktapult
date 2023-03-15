package com.ktapult

interface KtapultPayload

internal object Initial : KtapultPayload

object Loading : KtapultPayload {
    override fun toString(): String {
        return "Loading"
    }
}

class Error(val throwable: Throwable) : KtapultPayload {
    override fun toString(): String {
        return super.toString().plus("(throwable=$throwable)")
    }
}

class Loaded<T>(val data: T) : KtapultPayload {
    override fun toString(): String {
        return super.toString().plus("(data=$data)")
    }
}