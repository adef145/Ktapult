package com.ktapult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.ktapult.extension.sourceElementValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

interface Ktapult {

    companion object {
        internal val ktapults: MutableList<Ktapult> = mutableListOf()

        val ITEM_TO_ITEM_FLOW_MAPPER: KtapultFlowMapper<KtapultItem, KtapultItem> = { it }

        val ITEM_TO_PAYLOAD_FLOW_MAPPER: KtapultFlowMapper<KtapultItem, KtapultPayload> =
            { it.map { item -> item.payload } }

        val ITEM_TO_PAIR_FLOW_MAPPER: KtapultFlowMapper<KtapultItem, Pair<KtapultTag, KtapultPayload>> =
            { it.map { item -> Pair(item.tag, item.payload) } }

        inline fun <reified T : KtapultPayload> itemToPayloadAs(): KtapultFlowMapper<KtapultItem, T> =
            { flow ->
                flow.filter { it.payload is T }.map { it.payload as T }
            }

        inline fun <reified T : KtapultPayload> itemToPairAs(): KtapultFlowMapper<KtapultItem, Pair<KtapultTag, T>> =
            { flow ->
                flow.filter { it.payload is T }.map { Pair(it.tag, it.payload as T) }
            }

        fun enableLogger(enable: Boolean) {
            KtapultLogger.enabled = enable
        }

        fun setSourceElementValidator(validator: SourceElementValidator) {
            sourceElementValidator = validator
        }
    }

    var scope: CoroutineScope

    suspend fun emit(tag: KtapultTag, payload: KtapultPayload)

    suspend fun emitAll(tag: KtapultTag, payload: KtapultPayload)

    fun update(tag: KtapultTag, block: suspend () -> KtapultPayload)

    fun updateAll(tag: KtapultTag, block: suspend () -> KtapultPayload)

    // region Collect

    /**
     * tags: List of KtapultTag
     * mapper: Item to another type
     */
    suspend fun <T> collect(
        tags: Array<KtapultTag>,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        collector: FlowCollector<T>
    )

    /**
     * tag: Single of KtapultTag
     * mapper: Item to another type
     */
    suspend fun <T> collect(
        tag: KtapultTag,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        collector: FlowCollector<T>
    ) {
        collect(arrayOf(tag), mapper, collector)
    }

    /**
     * tags: List of KtapultTag
     * without map to another type. Always as KtapultItem
     */
    suspend fun collect(
        tags: Array<KtapultTag>,
        collector: FlowCollector<KtapultItem>
    ) {
        collect(tags, ITEM_TO_ITEM_FLOW_MAPPER, collector)
    }

    /**
     * tag: Single of KtapultTag
     * without map to another type. Always as KtapultItem
     */
    suspend fun collect(
        tag: KtapultTag,
        collector: FlowCollector<KtapultItem>
    ) {
        collect(arrayOf(tag), collector)
    }

    // endregion

    // region State

    @Composable
    fun <T> collectAsState(
        state: KtapultState,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        initial: T
    ): State<T>

    @Composable
    fun collectAsState(state: KtapultState, initial: KtapultPayload): State<KtapultItem> {
        return collectAsState(
            state = state,
            mapper = ITEM_TO_ITEM_FLOW_MAPPER,
            initial = KtapultItem(state, initial)
        )
    }

    // endregion
}

typealias KtapultFlowMapper<T, R> = (Flow<T>) -> Flow<R>

typealias SourceElementValidator = (StackTraceElement) -> Boolean