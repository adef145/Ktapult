package com.ktapult.example.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ktapult.Ktapult
import com.ktapult.KtapultEvent
import com.ktapult.KtapultFlowMapper
import com.ktapult.KtapultPayload
import com.ktapult.example.ui.theme.KtapultTheme
import com.ktapult.extension.ktapult
import com.ktapult.extension.toPayloadAs

object MainEvent : KtapultEvent

object OpenNewScreen : KtapultPayload

@Composable
fun Main(
    ktapult: Ktapult = ktapult()
) {
    KtapultTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navHostController = rememberNavController()
            LaunchedEffect(key1 = Unit) {
                ktapult
                    .collect(
                        tag = FluidEvent,
                        mapper = KtapultFlowMapper.toPayloadAs<OnItemClick>(),
                        collector = {
                            navHostController.navigate("DETAIL/${it.id}")
                        }
                    )/*TODO*/
            }

            Column {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    onClick = { ktapult.update(MainEvent) { OpenNewScreen } }) {
                    Text(text = "Open New Activity")
                }

                NavHost(navController = navHostController, startDestination = "MAIN") {
                    composable("MAIN") {
                        Fluid(ktapult)
                    }

                    composable("DETAIL/{id}") {
                        Greeting(
                            id = it.arguments?.getString("id")?.toInt() ?: -1,
                            ktapult
                        )
                    }
                }
            }
        }
    }
}