package com.maxrave.simpmusic.expect.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun ApplyStatusBarVisibility(hidden: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, hidden) {
        val activity = view.context as? Activity
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
