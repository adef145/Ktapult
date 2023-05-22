package com.ktapult.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ktapult.Ktapult
import com.ktapult.KtapultFlowMapper
import com.ktapult.example.ui.Main
import com.ktapult.example.ui.MainEvent
import com.ktapult.example.ui.OpenNewScreen
import com.ktapult.extension.ktapult
import com.ktapult.extension.toPayloadAs

class MainActivity : ComponentActivity() {

    private val ktapult: Ktapult by ktapult()

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Ktapult.enableLogger(true)
        Ktapult.setSourceElementValidator { it.className.startsWith("com.ktapult.example") }

        lifecycleScope.launchWhenStarted {
            ktapult.collect(
                tag = MainEvent,
                mapper = KtapultFlowMapper.toPayloadAs<OpenNewScreen>(),
                collector = {
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
            )
        }

        viewModel.getFluid(ktapult)
        viewModel.onAddMore(ktapult)
        viewModel.onItemClick(ktapult)
        viewModel.onIncrementClick(ktapult)

        setContent {
            Main(ktapult)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    KtapultTheme {
//        Greeting()
//    }
//}