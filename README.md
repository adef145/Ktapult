# Ktapult
[![](https://jitpack.io/v/adef145/Ktapult.svg)](https://jitpack.io/#adef145/Ktapult)

Ktapult inspired by [Catapult](https://github.com/happyfresh/Catapult) made base on Java.
And now, many developers move to Kotlin code base, and using coroutines as a asynchronous process, and jetpack compose is already in stable version.
And Ktapult born to manage and distribute your state and event. So you will only have single of truth for your state and event.

## Installation

1. Add jitpack repository to your build file
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. Add dependency
```groovy
dependencies {
    implementation 'com.github.adef145:ktapult:{version_number}'
}
```

## How it works

Ktapult made base on ViewModel.
So you can imagine Ktapult as a one of your viewmodel that you can use it in your activity, fragment, or in your composable function.

```kotlin
class YourActivity {
    
    val ktapult by ktapults()
}

class YourFragment {
    
    val ktapult by ktapults()
}

@Composable
fun YourCompose(
    ktapult = ktapult()
) {
    // or you can initiate in body
    val ktapult = ktapult()
}
```

### How to emit

To emit some state or event, at least you need 2 property: KtapultTag and KtapultPayload

KtapultTag is a sealed class that have 2 child interface:
* **KtapultState**
  KtapultState is to tell Ktapult that you need StateFlow.
* **KtapultEvent**
  KtapultEvent is to tell Ktapult that you need SharedFlow

You need to create your own tag with 

```kotlin

object YourState : KtapultState
object YourEvent : KtapultState

data class YourPayload(
  val id: Int
)

class YourViewModel : ViewModel() {

    fun fetchData(ktapult: Ktapult) {
        viewModelScope.launch(Dispatcher.IO) {
            // Similar with MutableStateFlow.emit(YourPayload(1))
            ktapult.emit(YourState, YourPayload(1))

            // Similar with MutableSharedFlow.emit(YourPayload(1))
            ktapult.emit(YourEvent, YourPayload(1))
        }
      
        // or you can use update instead for thread safe
        
        // Similar with MutableStateFlow.emit(YourPayload(1))
        ktapult.update(YourState) { YourPayload(1) }
  
        // Similar with MutableSharedFlow.emit(YourPayload(1))
        ktapult.update(YourEvent) { YourPayload(1) }
    }
}
```

### How to collect

You can collect either in Activity / Fragment, ViewModel, or collectAsState in composable function.
It similar with collect in flow, the different only you need to define which tag you want to collect and 
available flow mapper for you use it when you need to map from original flow (Flow<KtapultItem>) into new one.
Currently we support 5 mapper:
* Ktapult.ITEM_TO_ITEM_FLOW_MAPPER: this is the default mapper
* Ktapult.ITEM_TO_PAYLOAD_FLOW_MAPPER: Basically KtapultItem is data class with property (Tag, Payload).
  Instead you collect as KtapultItem, you can directly retrieve KtaplutPayload
* Ktapult.ITEM_TO_PAIR_FLOW_MAPPER: this is basically map from KtapultItem(tag, payload) into Pair<Tag, Payload>
* Ktapult.itemToPayloadAs<T>: this is to map KtapultItem into your own payload type, such as YourPayload in example above.
* Ktapult.itemToPairAs<T>: this is to map KtapultITem into Pair<Tag, T> where T is your own payload type.

```kotlin
class YourActivity {

    val ktapult by ktapults()
    
    override fun onCreate(onSavedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted {
            ktapult.collect(
                tag = YourState, // tag you want to collect
                mapper = {}, // you can map original flow (Flow<KtapultItem>) into new one
                collector = {
                    // Put your code in here after collect and map from Ktapult
                }
            )
        }
    }
}

@Composable
fun YourCompose(
  ktapult = com.ktapult.extension.ktapult()
) {
    val itemState: State<KtapultItem> = 
        ktapult.collectAsState(YourState, /* default */ YourPayload(1))
  
    // or you can collect directly to payload
    val payloadState: State<KtapultPayload> = 
        ktapult.collectPayloadAsState(YourState, /* default */ YourPayload(1))
  
    // and available extension to help you for casting the payload
    payloadState.whenTypeIs<YourPayload> { yourPayload ->
        // put your code here
    }
}
```

```markdown
MIT License

Copyright (c) 2023 Ade Fruandta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```