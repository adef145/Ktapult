package com.ktapult.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktapult.Ktapult
import com.ktapult.Ktapult.Companion.itemToPairAs
import com.ktapult.Ktapult.Companion.itemToPayloadAs
import com.ktapult.Loaded
import com.ktapult.Loading
import com.ktapult.example.ui.AddMore
import com.ktapult.example.ui.FluidEvent
import com.ktapult.example.ui.FluidItemState
import com.ktapult.example.ui.FluidState
import com.ktapult.example.ui.GreetingEvent
import com.ktapult.example.ui.GreetingState
import com.ktapult.example.ui.OnIncrementAllClick
import com.ktapult.example.ui.OnIncrementClick
import com.ktapult.example.ui.OnItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val fluidList: MutableList<Item> = mutableListOf()

    private fun greeting(ktapult: Ktapult, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.emit(GreetingState(id), Loading)
            Thread.sleep(1000)
            ktapult.emit(GreetingState(id), Loaded(fluidList[id]))
        }
    }

    fun getFluid(ktapult: Ktapult) {
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.emit(FluidState, Loading)
            Thread.sleep(1000)
            ktapult.emit(FluidState, Loaded(mutableListOf<Item>().apply {
                for (i in 0..10) {
                    add(Item(i, "Android $i"))
                }
                fluidList.addAll(this)
            }))
        }
    }

    fun onAddMore(ktapult: Ktapult) {
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.collect(
                tags = arrayOf(FluidEvent),
                mapper = itemToPayloadAs<AddMore>(),
                collector = {
                    fluidList.add(Item(fluidList.size, "Android ${fluidList.size}"))
                    ktapult.emit(FluidState, Loaded(fluidList))
                }
            )
        }
    }

    fun onItemClick(ktapult: Ktapult) {
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.collect(
                tag = FluidEvent,
                mapper = itemToPayloadAs<OnItemClick>(),
                collector = {
                    greeting(ktapult, it.id)
                }
            )
        }
    }

    fun onIncrementClick(ktapult: Ktapult) {
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.collect(
                tags = arrayOf(FluidEvent, GreetingEvent),
                mapper = itemToPairAs<OnIncrementClick>(),
                collector = { (tag, onIncrementClick) ->
                    val item = onIncrementClick.item
                    item.counter++
                    ktapult.emit(
                        when (tag) {
                            is FluidEvent -> FluidItemState(onIncrementClick.item.id)
                            is GreetingEvent -> GreetingState(onIncrementClick.item.id)
                            else -> tag
                        },
                        Loaded(item)
                    )
                }
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            ktapult.collect(
                tags = arrayOf(FluidEvent, GreetingEvent),
                mapper = itemToPairAs<OnIncrementAllClick>(),
                collector = { (tag, onIncrementClick) ->
                    val item = onIncrementClick.item
                    item.counter++
                    ktapult.emitAll(
                        when (tag) {
                            is FluidEvent -> FluidItemState(onIncrementClick.item.id)
                            is GreetingEvent -> GreetingState(onIncrementClick.item.id)
                            else -> tag
                        },
                        Loaded(item)
                    )
                }
            )
        }
    }
}