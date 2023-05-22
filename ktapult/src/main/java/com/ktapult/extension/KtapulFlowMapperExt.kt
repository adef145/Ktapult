package com.ktapult.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ktapult.CombineKtapultFlowMapper
import com.ktapult.KtapultFlowMapper
import com.ktapult.KtapultItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T, R1, R2> KtapultFlowMapper<T, R1>.then(
    mapper: (Flow<R1>) -> Flow<R2>
): KtapultFlowMapper<T, R2> = CombineKtapultFlowMapper(this, object : KtapultFlowMapper<R1, R2> {
    override fun map(flow: Flow<R1>): Flow<R2> = mapper(flow)
})

fun KtapultFlowMapper<KtapultItem, KtapultItem>.toPayload() = then { flow ->
    flow.map { it.payload }
}

fun KtapultFlowMapper<KtapultItem, KtapultItem>.toPair() = then { flow ->
    flow.map { Pair(it.tag, it.payload) }
}

inline fun <reified T> KtapultFlowMapper<KtapultItem, KtapultItem>.toPayloadAs() =
    toPayload().then { flow ->
        flow.filter { it is T }.map { it as T }
    }

inline fun <reified T> KtapultFlowMapper<KtapultItem, KtapultItem>.toPairAs() =
    toPair().then { flow ->
        flow.filter { it.second is T }.map { Pair(it.first, it.second as T) }
    }

fun <T, R> KtapultFlowMapper<T, R>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): KtapultFlowMapper<T, R> = then {
    it.flowWithLifecycle(lifecycle, minActiveState)
}