package com.maxrave.simpmusic.ui.screen.home

import com.maxrave.simpmusic.ui.component.AppBackButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.maxrave.domain.data.entities.NotificationEntity
import com.maxrave.simpmusic.expect.ui.DesktopMiniPlayerClearance
import com.maxrave.simpmusic.expect.ui.VerticalScrollBar
import com.maxrave.simpmusic.extension.barBlurStyle
import com.maxrave.simpmusic.expect.ui.layerBackdrop
import com.maxrave.simpmusic.expect.ui.rememberBackdrop
import com.maxrave.simpmusic.ui.component.liquidGlass
import com.maxrave.simpmusic.ui.theme.LocalLiquidGlassEnabled
import androidx.compose.foundation.shape.RoundedCornerShape
import com.maxrave.simpmusic.extension.formatTimeAgo
import com.maxrave.simpmusic.extension.mouseDragScroll
import com.maxrave.simpmusic.ui.component.AmbientThemeGlow
import com.maxrave.simpmusic.ui.component.CenterLoadingBox
import com.maxrave.simpmusic.ui.component.EndOfPage
import com.maxrave.simpmusic.ui.component.RippleIconButton
import com.maxrave.simpmusic.ui.component.rememberHolderPainter
import com.maxrave.simpmusic.ui.icon.ArrowBackIosNew
import com.maxrave.simpmusic.ui.icon.RssFeed
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.navigation.destination.list.AlbumDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.ArtistDestination
import com.maxrave.simpmusic.ui.theme.ArtworkShape
import com.maxrave.simpmusic.ui.theme.typo
import com.maxrave.simpmusic.viewModel.NotificationViewModel
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.album
import simpmusic.composeapp.generated.resources.new_release
import simpmusic.composeapp.generated.resources.no_notification
import simpmusic.composeapp.generated.resources.notification
import simpmusic.composeapp.generated.resources.singles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val listNotification by viewModel.listNotification.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val hazeState = rememberHazeState()
    val headerBackdrop = rememberBackdrop(MaterialTheme.colorScheme.background)
    // Home's rule: transparent only while pixel-0 is on screen; the frost itself stays light.
    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
    }

    // Home-family ambient ground — scrolls away with the list (see SettingScreen's note on the
    // draw-phase translation). Tinted with the app's primary, as Settings is.
    AmbientThemeGlow(
        tint = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier.graphicsLayer {
                translationY =
                    if (listState.firstVisibleItemIndex == 0) {
                        -listState.firstVisibleItemScrollOffset.toFloat()
                    } else {
                        -size.height
                    }
            },
    )
    Box(Modifier.fillMaxSize().hazeSource(hazeState).then(if (LocalLiquidGlassEnabled.current) Modifier.layerBackdrop(headerBackdrop) else Modifier)) {
        Crossfade(targetState = listNotification) {
            if (it == null) {
                Box(
                    Modifier.fillMaxSize(),
                ) {
                    CenterLoadingBox(modifier = Modifier.align(Alignment.Center))
                }
            } else if (it.isNotEmpty()) {
                // contentPadding, not Modifier.padding: the bar floats now, and the list has to
                // slide UNDER it for the frost to have anything to frost.
                LazyColumn(
                    state = listState,
                    contentPadding =
                        PaddingValues(
                            start = 15.dp,
                            end = 15.dp,
                            top = 15.dp,
                            bottom = 15.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(it.filterNot { notification -> notification.type == NotificationEntity.TYPE_BLOG }) { notification ->
                        NotificationItem(
                            notification = notification,
                            navController,
                        )
                    }
                    item {
                        EndOfPage()
                    }
                }
                VerticalScrollBar(
                    scrollState = listState,
                    modifier = Modifier.padding(bottom = DesktopMiniPlayerClearance),
                )
            } else {
                Box(
                    Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = stringResource(Res.string.no_notification),
                        style = typo().titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

}