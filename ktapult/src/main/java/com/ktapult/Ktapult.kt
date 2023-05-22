package com.ktapult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.ktapult.extension.sourceElementValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector

interface Ktapult {

    companion object {
        internal val ktapults: MutableList<Ktapult> = mutableListOf()

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
        collect(tags, KtapultFlowMapper, collector)
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
            mapper = KtapultFlowMapper,
            initial = KtapultItem(state, initial)
        )
    }

    // endregion
}

typealias SourceElementValidator = (StackTraceElement) -> Boolean