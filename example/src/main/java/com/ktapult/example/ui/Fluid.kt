package com.ktapult.example.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.ktapult.Ktapult
import com.ktapult.KtapultEvent
import com.ktapult.KtapultFlowMapper
import com.ktapult.KtapultPayload
import com.ktapult.KtapultState
import com.ktapult.Loaded
import com.ktapult.Loading
import com.ktapult.example.Item
import com.ktapult.extension.ktapult
import com.ktapult.extension.toPayload
import com.ktapult.extension.toPayloadAs
import com.ktapult.extension.whenTypeIs

// region KtapultTag
object FluidState : KtapultState
object FluidEvent : KtapultEvent
data class FluidItemState(val id: Int) : KtapultState
// endregion

// region KtapultPayload
object AddMore : KtapultPayload
class OnItemClick(val id: Int) : KtapultPayload {
    override fun toString(): String {
        return super.toString().plus("(id=$id)")
    }
}

class OnIncrementClick(val item: Item) : KtapultPayload {
    override fun toString(): String {
        return super.toString().plus("(item=$item)")
    }
}

class OnIncrementAllClick(val item: Item) : KtapultPayload {
    override fun toString(): String {
        return super.toString().plus("(item=$item)")
    }
}
// endregion

@Composable
fun Fluid(
    ktapult: Ktapult = ktapult()
) {
    ktapult
        .collectAsState(
            state = FluidState,
            mapper = KtapultFlowMapper.toPayload(),
            Loading
        ).whenTypeIs<Loading> {
            Text(text = "On Loading")
        }.whenTypeIs<Loaded<List<Item>>> {
            LazyColumn {
                items(it.data) { item ->
                    FluidItem(item, ktapult)
                }
                item {
                    AddMore(ktapult)
                }
            }
        }
}

@Composable
private fun FluidItem(
    item: Item,
    ktapult: Ktapult
) {
    val fluidItemState = ktapult.collectAsState(
        state = FluidItemState(item.id),
        mapper = KtapultFlowMapper.toPayloadAs(),
        initial = Loaded(item)
    )

    Greeting(FluidEvent, fluidItemState.value.data, ktapult)
}

@Composable
private fun AddMore(
    ktapul: Ktapult
) {
    Button(onClick = {
        ktapul.update(FluidEvent) { AddMore }
    }) {
        Text(text = "Add More")
    }
}