package com.ktapult

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktapult.Ktapult.Companion.ktapults
import com.ktapult.extension.findSourceElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

internal class KtapultViewModel : ViewModel(), Ktapult {

    override var scope: CoroutineScope = viewModelScope

    private val mapOfFlow: MutableMap<KtapultTag, Flow<KtapultItem>> = mutableMapOf()

    private val ktapultSourceElement = findSourceElement()

    init {
        ktapults.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        Ktapult.ktapults.remove(this)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun emit(tag: KtapultTag, payload: KtapultPayload) {
        emit(tag, payload, findSourceElement())
    }

    override suspend fun emitAll(tag: KtapultTag, payload: KtapultPayload) {
        emitAll(tag, payload, ktapults.filter { it != this }, findSourceElement())
    }

    override fun update(tag: KtapultTag, block: suspend () -> KtapultPayload) {
        val sourceElement = findSourceElement()
        viewModelScope.launch(Dispatchers.IO) {
            emit(tag, block(), sourceElement)
        }
    }

    override fun updateAll(tag: KtapultTag, block: suspend () -> KtapultPayload) {
        val sourceElement = findSourceElement()
        val ktapults = ktapults.filter { it != this }
        viewModelScope.launch(Dispatchers.IO) {
            emitAll(tag, block(), ktapults, sourceElement)
        }
    }

    override suspend fun <T> collect(
        tags: Array<KtapultTag>,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        collector: FlowCollector<T>
    ) {
        collect(findSourceElement(), tags, mapper, collector)
    }

    @Composable
    override fun <T> collectAsState(
        state: KtapultState,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        initial: T
    ): State<T> {
        val sourceElement = findSourceElement()
        return flow {
            collect(sourceElement, arrayOf(state), mapper) {
                emit(it)
            }
        }.collectAsState(initial = initial)
    }

    private fun of(vararg tags: KtapultTag): Flow<KtapultItem> =
        merge(*tags.map { of(it) }.toTypedArray())

    private fun of(tag: KtapultTag): Flow<KtapultItem> {
        synchronized(mapOfFlow) {
            return (mapOfFlow[tag] ?: if (tag is KtapultState) {
                MutableStateFlow(KtapultItem(tag, Initial)).also {
                    mapOfFlow[tag] = it
                }
            } else {
                MutableSharedFlow<KtapultItem>().also {
                    mapOfFlow[tag] = it
                }
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun emit(
        tag: KtapultTag,
        payload: KtapultPayload,
        sourceElement: StackTraceElement?
    ) {
        KtapultLogger.d(">> Send${getSourceElementInfo(sourceElement)}:\n\tpayload: $payload\n\ttag: $tag")
        (of(tag) as FlowCollector<KtapultItem>).emit(KtapultItem(tag, payload))
    }

    private suspend fun emitAll(
        tag: KtapultTag,
        payload: KtapultPayload,
        ktapults: List<Ktapult>,
        sourceElement: StackTraceElement?,
    ) {
        emit(tag, payload, sourceElement)
        ktapults.forEach {
            viewModelScope.launch(Dispatchers.IO) {
                it.emit(tag, payload)
            }
        }
    }

    private suspend fun <T> collect(
        sourceElement: StackTraceElement?,
        tags: Array<KtapultTag>,
        mapper: KtapultFlowMapper<KtapultItem, T>,
        collector: FlowCollector<T>
    ) {
        mapper.map(
            of(tags = tags).filterNot { it.payload is Initial }
        ).collect {
            KtapultLogger.d("<< Collect${getSourceElementInfo(sourceElement)}:\n\tvalue: $it")
            collector.emit(it)
        }
    }

    private fun getSourceElementInfo(sourceElement: StackTraceElement?): String {
        return (sourceElement ?: ktapultSourceElement)?.let { " by $it" } ?: ""
    }
}