package com.maxrave.simpmusic.ui.component


import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.maxrave.simpmusic.ui.icon.ArrowBackIosNew
import com.maxrave.simpmusic.ui.icon.MoreVert
import com.maxrave.simpmusic.ui.icon.SimpIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalAppBar(
    title: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (RowScope.() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth().height(64.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .blur(22.dp)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.38f),
                        0.32f to MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        0.72f to MaterialTheme.colorScheme.surface.copy(alpha = 0.22f),
                        1.0f to Color.Transparent,
                    ),
                ),
        )
        TopAppBar(
        title = title,
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            if (leftIcon != null) {
                Box(
                    modifier = Modifier.padding(start = 12.dp).size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                ) {
                    leftIcon()
                }
            }
        },
        actions = rightIcon ?: {},
        // Every other screen in the app builds its TopAppBar with a transparent container and
        // onBackground content. Leaving `colors` unset here fell back to the Material default
        // (surface container + onSurface content), which is why MoodScreen — the only caller —
        // showed a light bar with black text on top of the app's dark background.
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )
    }
}

@Preview
@Composable
fun NormalAppBarPreview() {
    NormalAppBar(
        title = {
            Text(text = "Title")
        },
        leftIcon = {
            IconButton(onClick = { }) {
                Icon(
                    SimpIcons.ArrowBackIosNew,
                    contentDescription = "Back",
                )
            }
        },
        rightIcon = {
            IconButton(onClick = { }) {
                Icon(
                    SimpIcons.MoreVert,
                    contentDescription = "Back",
                )
            }
        },
    )
}