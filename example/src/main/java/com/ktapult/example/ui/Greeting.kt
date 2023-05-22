package com.ktapult.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ktapult.Error
import com.ktapult.Ktapult
import com.ktapult.KtapultEvent
import com.ktapult.KtapultFlowMapper
import com.ktapult.KtapultState
import com.ktapult.KtapultTag
import com.ktapult.Loaded
import com.ktapult.Loading
import com.ktapult.example.Item
import com.ktapult.extension.ktapult
import com.ktapult.extension.toPayload
import com.ktapult.extension.whenTypeIs

data class GreetingState(val id: Int) : KtapultState
object GreetingEvent : KtapultEvent

@Composable
fun Greeting(
    id: Int = -1,
    ktapult: Ktapult = ktapult()
) {
    ktapult
        .collectAsState(
            state = GreetingState(id),
            mapper = KtapultFlowMapper.toPayload(),
            initial = Loading
        ).whenTypeIs<Loading> {
            Text(text = "On Loading")
        }.whenTypeIs<Error> {
            Text(text = "On Error ${it.throwable.message}")
        }.whenTypeIs<Loaded<Item>> {
            Greeting(GreetingEvent, it.data, ktapult)
        }
}

@Composable
fun Greeting(
    tag: KtapultTag,
    item: Item,
    ktapult: Ktapult
) {
    Column(Modifier.clickable {
        ktapult.update(tag) { OnItemClick(item.id) }
    }) {
        Greeting(item.value)
        Text(text = "Counter: ${item.counter}")
        Button(onClick = {
            ktapult.update(tag) { OnIncrementClick(item) }
        }) {
            Text(text = "Increment")
        }
        Button(onClick = {
            ktapult.update(tag) { OnIncrementAllClick(item) }
        }) {
            Text(text = "Increment All")
        }
    }
}

@Composable
fun Greeting(
    text: String
) {
    Text(text = "Hello ${text}!")
}

