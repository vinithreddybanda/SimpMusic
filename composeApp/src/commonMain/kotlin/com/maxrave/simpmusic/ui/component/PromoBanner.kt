package com.maxrave.simpmusic.ui.component

import androidx.compose.runtime.compositionLocalOf

/** App-level dialog visibility, used to coordinate screen overlays. */
val LocalAppDialogOpen = compositionLocalOf { false }

/** Whether the Now Playing sheet is currently open. */
val LocalNowPlayingOpen = compositionLocalOf { false }

/** Whether the user has interacted with the app during this launch. */
val LocalUserStarted = compositionLocalOf { false }
