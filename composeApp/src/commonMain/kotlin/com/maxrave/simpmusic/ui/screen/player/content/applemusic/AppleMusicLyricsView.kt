package com.maxrave.simpmusic.ui.screen.player.content.applemusic

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.simpmusic.expect.ui.DeviceVolumeController
import com.maxrave.simpmusic.expect.ui.isLyricsBlurSupported
import com.maxrave.simpmusic.ui.component.AppleMusicLyricPaddingX
import com.maxrave.simpmusic.ui.component.LyricsOffsetFloatingControl
import com.maxrave.simpmusic.ui.component.LyricsView
import com.maxrave.simpmusic.ui.component.hasTiming
import com.maxrave.simpmusic.ui.component.lyrics.ShareLyricsSheet
import com.maxrave.simpmusic.ui.component.lyrics.toShareLyricsLines
import com.maxrave.simpmusic.ui.icon.OpenInFull
import com.maxrave.simpmusic.ui.icon.Share
import com.maxrave.simpmusic.ui.icon.AvTimer
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.icon.ThumbsUpDown
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingContentActions
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingContentState
import com.maxrave.simpmusic.ui.screen.player.content.canVote
import com.maxrave.simpmusic.viewModel.LyricsProvider
import com.maxrave.simpmusic.viewModel.NowPlayingScreenData
import com.maxrave.simpmusic.viewModel.UIEvent
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.ai_translated
import simpmusic.composeapp.generated.resources.line_synced
import simpmusic.composeapp.generated.resources.lyrics_provider_betterlyrics
import simpmusic.composeapp.generated.resources.lyrics_provider_lrc
import simpmusic.composeapp.generated.resources.lyrics_provider_simpmusic
import simpmusic.composeapp.generated.resources.lyrics_provider_youtube
import simpmusic.composeapp.generated.resources.offline_mode
import simpmusic.composeapp.generated.resources.rich_synced
import simpmusic.composeapp.generated.resources.spotify_lyrics_provider
import simpmusic.composeapp.generated.resources.unsynced

@Composable
internal fun AppleMusicLyricsView(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    typography: AppleMusicTypography,
    viewState: AppleMusicView,
    onSelectView: (AppleMusicView) -> Unit,
    activePillContainer: Color,
    activePillContent: Color,
    deviceVolumeController: DeviceVolumeController?,
    outputName: String?,
    onOpenOutput: () -> Unit,
    modifier: Modifier = Modifier,
    showBottomCluster: Boolean = true,
    onBack: (() -> Unit)? = null,
    dataStoreManager: DataStoreManager = koinInject(),
) {
    val localDensity = LocalDensity.current
    val lyricsData = state.screenData.lyricsData

    val lyricsStyle by dataStoreManager.lyricsStyle.collectAsStateWithLifecycle(DataStoreManager.LYRICS_STYLE_CLASSIC)
    val rendererOwnsGutter = lyricsStyle == DataStoreManager.LYRICS_STYLE_APPLE_MUSIC && isLyricsBlurSupported()

    var showCluster by rememberSaveable { mutableStateOf(true) }
    var showShareSheet by rememberSaveable { mutableStateOf(false) }
    var interactionTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(showCluster, interactionTick) {
        if (showCluster) {
            delay(CLUSTER_AUTO_HIDE_MS)
            showCluster = false
        }
    }

    val scrollWakesControls =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y != 0f) {
                        showCluster = true
                        interactionTick++
                    }
                    return Offset.Zero
                }
            }
        }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier =
                Modifier.height(
                    with(localDensity) { WindowInsets.statusBars.getTop(localDensity).toDp() } + 20.dp,
                ),
        )
        AppleMusicCompactHeader(state = state, actions = actions, typography = typography, onBack = onBack)

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .nestedScroll(scrollWakesControls)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        showCluster = !showCluster
                        interactionTick++
                    },
        ) {
            if (lyricsData != null) {
                LyricsView(
                    lyricsData = lyricsData,
                    timeLine = state.timelineFlow,
                    onLineClick = { f ->
                        actions.onUIEvent(UIEvent.UpdateProgress(f))
                    },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .appleMusicVerticalFadeEdges(topFade = 28.dp, bottomFade = 18.dp)
                            .padding(horizontal = if (rendererOwnsGutter) 0.dp else AppleMusicLyricPaddingX),
                    backgroundColor = Color.Transparent,
                    footerContent = {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
                        ) {
                            if (lyricsData.translatedLyrics?.second == LyricsProvider.AI) {
                                Text(
                                    text = stringResource(Res.string.ai_translated),
                                    style = typography.footer,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Text(
                                text = appleMusicLyricsSyncText(lyricsData),
                                style = typography.footer,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            val provider = appleMusicLyricsProviderText(lyricsData)
                            if (provider.isNotBlank()) {
                                Text(
                                    text = provider,
                                    style = typography.footer,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    },
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = showCluster,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Column(
                        modifier = Modifier.padding(end = 20.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        if (lyricsData.hasTiming()) {
                            LyricsOffsetFloatingControl(
                                offsetMs = state.lyricsOffsetMs.toInt(),
                                onOffsetChange = actions.onLyricsOffsetChange,
                                backdrop = null,
                                onInteraction = { interactionTick++ },
                            ) { onClick ->
                                AppleMusicFloatingCircleButton(icon = SimpIcons.AvTimer, onClick = onClick)
                            }
                        }
                        if (lyricsData.canVote()) {
                            AppleMusicFloatingCircleButton(icon = SimpIcons.ThumbsUpDown, onClick = { actions.onShowVoteDialog() })
                        }
                        AppleMusicFloatingCircleButton(icon = SimpIcons.Share, onClick = { showShareSheet = true })
                        AppleMusicFloatingCircleButton(icon = SimpIcons.OpenInFull, onClick = { actions.onShowFullscreenLyrics() })
                    }
                }
            }
        }

        if (showShareSheet && lyricsData != null) {
            ShareLyricsSheet(
                lines = lyricsData.toShareLyricsLines(),
                songTitle = state.screenData.nowPlayingTitle,
                artistName = state.screenData.artistName,
                artwork = state.screenData.bitmap,
                seedColor = state.startColor.value,
                initialLineIndex = state.currentLyricLineIndex,
                onDismiss = { showShareSheet = false },
            )
        }

        if (!showBottomCluster) {
            Spacer(modifier = Modifier.height(with(localDensity) { WindowInsets.systemBars.getBottom(localDensity).toDp() }))
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = showCluster && showBottomCluster,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            AppleMusicBottomCluster(
                state = state,
                actions = actions,
                typography = typography,
                viewState = viewState,
                onSelectView = onSelectView,
                activePillContainer = activePillContainer,
                activePillContent = activePillContent,
                deviceVolumeController = deviceVolumeController,
                outputName = outputName,
                onOpenOutput = onOpenOutput,
            )
        }
    }
}

private const val CLUSTER_AUTO_HIDE_MS = 8_000L

@Composable
internal fun AppleMusicFloatingCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .appleMusicPressInflate()
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.24f))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = "", tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun appleMusicLyricsSyncText(lyricsData: NowPlayingScreenData.LyricsData): String {
    val syncTypeText =
        when (lyricsData.lyrics.syncType) {
            "LINE_SYNCED" -> stringResource(Res.string.line_synced)
            "RICH_SYNCED" -> stringResource(Res.string.rich_synced)
            else -> stringResource(Res.string.unsynced)
        }
    return syncTypeText
}

@Composable
private fun appleMusicLyricsProviderText(lyricsData: NowPlayingScreenData.LyricsData): String =
    when (lyricsData.lyricsProvider) {
        LyricsProvider.SIMPMUSIC -> stringResource(Res.string.lyrics_provider_simpmusic)
        LyricsProvider.LRCLIB -> stringResource(Res.string.lyrics_provider_lrc)
        LyricsProvider.YOUTUBE -> stringResource(Res.string.lyrics_provider_youtube)
        LyricsProvider.SPOTIFY -> stringResource(Res.string.spotify_lyrics_provider)
        LyricsProvider.OFFLINE -> stringResource(Res.string.offline_mode)
        LyricsProvider.BETTER_LYRICS -> stringResource(Res.string.lyrics_provider_betterlyrics)
        LyricsProvider.AI -> ""
    }
