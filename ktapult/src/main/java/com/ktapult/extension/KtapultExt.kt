package com.ktapult.extension

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktapult.Ktapult
import com.ktapult.KtapultViewModel
import com.ktapult.SourceElementValidator

internal var sourceElementValidator: SourceElementValidator = {
    !it.className.startsWith("com.ktapult") &&
        !it.className.startsWith("java") &&
        !it.className.startsWith("androidx")
}

fun ComponentActivity.ktapult(): Lazy<Ktapult> = viewModels<KtapultViewModel>()

fun Fragment.ktapult(): Lazy<Ktapult> = viewModels<KtapultViewModel>()

@Composable
fun ktapult(): Ktapult = viewModel<KtapultViewModel>()

internal fun findSourceElement(): StackTraceElement? {
    return Throwable().stackTrace.firstOrNull(sourceElementValidator)
}