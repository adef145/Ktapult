package com.ktapult

import kotlinx.coroutines.flow.Flow

interface KtapultFlowMapper<T, R> {

    fun map(flow: Flow<T>): Flow<R>

    companion object : KtapultFlowMapper<KtapultItem, KtapultItem> {
        override fun map(flow: Flow<KtapultItem>): Flow<KtapultItem> = flow
    }
}

internal class CombineKtapultFlowMapper<T, R, R2>(
    private val mapper1: KtapultFlowMapper<T, R>,
    private val mapper2: KtapultFlowMapper<R, R2>
) : KtapultFlowMapper<T, R2> {

    override fun map(flow: Flow<T>): Flow<R2> {
        return mapper2.map(mapper1.map(flow))
    }
}