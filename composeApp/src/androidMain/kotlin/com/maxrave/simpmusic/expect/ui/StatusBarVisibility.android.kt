package com.maxrave.simpmusic.expect.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun ApplyStatusBarVisibility(hidden: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, hidden) {
        val activity = view.context.findActivity()
        val controller = activity?.window?.let { WindowInsetsControllerCompat(it, view) }
        if (hidden) {
            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            controller?.show(WindowInsetsCompat.Type.statusBars())
        }
        onDispose {
            if (hidden) controller?.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
