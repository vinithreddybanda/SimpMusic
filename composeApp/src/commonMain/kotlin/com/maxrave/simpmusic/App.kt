package com.maxrave.simpmusic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.maxrave.common.LibraryChipType
import com.maxrave.domain.data.player.GenericMediaItem
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.manager.DataStoreManager.Values.TRUE
import com.maxrave.logger.Logger
import com.maxrave.simpmusic.expect.Orientation
import com.maxrave.simpmusic.expect.currentOrientation
import com.maxrave.simpmusic.expect.ui.layerBackdrop
import com.maxrave.simpmusic.expect.ui.rememberBackdrop
import com.maxrave.simpmusic.extension.copy
import com.maxrave.simpmusic.ui.component.AppBottomNavigationBar
import com.maxrave.simpmusic.ui.component.AppNavigationRail
import com.maxrave.simpmusic.ui.component.DesktopSidebar
import com.maxrave.simpmusic.ui.component.LiquidGlassAppBottomNavigationBar
import com.maxrave.simpmusic.ui.component.LocalAppDialogOpen
import com.maxrave.simpmusic.ui.component.LocalNowPlayingOpen
import com.maxrave.simpmusic.ui.component.LocalUserStarted
import com.maxrave.simpmusic.ui.icon.ArrowForwardIos
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.navigation.destination.home.AnalyticsDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.NotificationDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.WrappedDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.MixForYouDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.AlbumDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.ArtistDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.PlaylistDestination
import com.maxrave.simpmusic.ui.navigation.destination.player.FullscreenDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.ui.navigation.graph.AppNavigationGraph
import com.maxrave.simpmusic.ui.screen.MiniPlayer
import com.maxrave.simpmusic.ui.screen.other.UnofficialBuildScreen
import com.maxrave.simpmusic.ui.screen.player.NowPlayingScreen
import com.maxrave.simpmusic.ui.screen.player.NowPlayingScreenContent
import com.maxrave.simpmusic.ui.theme.AppTheme
import com.maxrave.simpmusic.ui.theme.ForceDarkContent
import com.maxrave.simpmusic.ui.theme.desktopPanelDark
import com.maxrave.simpmusic.ui.theme.desktopWindowDark
import com.maxrave.simpmusic.ui.theme.desktopWindowLight
import com.maxrave.simpmusic.ui.theme.parseThemeColorHex
import com.maxrave.simpmusic.utils.isAppLinkAvailable
import com.maxrave.simpmusic.utils.libraryTabOf
import com.maxrave.simpmusic.viewModel.SharedViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.notification
import simpmusic.composeapp.generated.resources.this_link_is_not_supported
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalFoundationApi::class)
@Composable
fun App(
    viewModel: SharedViewModel = koinInject(),
    showDesktopNotificationPermissionDialog: Boolean = false,
    onDismissDesktopNotificationPermissionDialog: (doNotShowAgain: Boolean) -> Unit = {},
    onOpenDesktopNotificationSettings: (doNotShowAgain: Boolean) -> Unit = {},
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navController = rememberNavController()
    val isDesktopShell = getPlatform() == Platform.Desktop

    val sleepTimerState by viewModel.sleepTimerState.collectAsStateWithLifecycle()
    val nowPlayingData by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val intent by viewModel.intent.collectAsStateWithLifecycle()

    val isLiquidGlassEnabled = TRUE
    val isLocalTrackingEnabled by viewModel.getLocalTrackingEnabled().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val showAnalyticsTab = isLocalTrackingEnabled == TRUE
    val isYouTubeLoggedIn by viewModel.getYouTubeLoggedIn().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val showMixForYouTab = isYouTubeLoggedIn == TRUE

    val themeMode by viewModel.getThemeMode().collectAsStateWithLifecycle(DataStoreManager.THEME_MODE_DARK)
    val themeColorSource by viewModel.getThemeColorSource().collectAsStateWithLifecycle(DataStoreManager.THEME_COLOR_DEFAULT)
    val customThemeColorHex by viewModel.getCustomThemeColor().collectAsStateWithLifecycle(DataStoreManager.DEFAULT_THEME_COLOR_HEX)
    val isOfficialBuild by viewModel.isOfficialBuild.collectAsStateWithLifecycle()
    val isShowMiniPlayer by remember {
        derivedStateOf {
            val item = nowPlayingData?.mediaItem
            item != null && item != GenericMediaItem.EMPTY
        }
    }

    var isShowNowPlaylistScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var isInFullscreen by rememberSaveable {
        mutableStateOf(false)
    }

    var isNavBarVisible by rememberSaveable {
        mutableStateOf(true)
    }

    val appDialogOpen = false

    LaunchedEffect(sleepTimerState.isDone) {
        if (sleepTimerState.isDone) viewModel.stopSleepTimer()
    }

    var userStarted by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(isShowNowPlaylistScreen) {
        if (isShowNowPlaylistScreen) userStarted = true
    }

    val hazeState =
        rememberHazeState()

    LaunchedEffect(intent) {
        val intent = intent ?: return@LaunchedEffect
        val data = intent.data
        Logger.d("MainActivity", "onCreate: $data")
        if (data != null) {
            val isPlutoScheme = data.scheme == "pluto" || data.scheme == "simpmusic"
            if (isPlutoScheme && data.host == "notification") {
                viewModel.setIntent(null)
                navController.navigate(
                    NotificationDestination,
                )
            } else if (data.scheme == "wordbyword" && data.host == "lastfm-auth") {
                val token = data.getQueryParameter("token")
                Logger.d("MainActivity", "Last.fm callback, token present: ${!token.isNullOrEmpty()}")
                viewModel.setIntent(null)
                token?.let { viewModel.completeLastfmLogin(it) }
            } else if (isPlutoScheme && data.host == "discord-auth") {
                Logger.d("MainActivity", "Discord callback, code present: ${!data.getQueryParameter("code").isNullOrEmpty()}")
                viewModel.setIntent(null)
                viewModel.completeDiscordLogin(data.toString())
            } else if (data.host == "simpmusic.org" || isPlutoScheme) {
                val segments = data.pathSegments
                val appPath =
                    if (isPlutoScheme) {
                        data.host
                    } else {
                        segments.getOrNull(1)
                    }
                Logger.d("MainActivity", "simpmusic.org deep link, appPath: $appPath")
                val localTrackingOn = viewModel.getLocalTrackingEnabled().first() == TRUE
                val youTubeLoggedIn = viewModel.getYouTubeLoggedIn().first() == TRUE
                viewModel.setIntent(null)
                when (appPath) {
                    "watch" -> {
                        data.getQueryParameter("v")?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }

                    "playlist" -> {
                        data.getQueryParameter("list")?.let { playlistId ->
                            if (playlistId.startsWith("OLAK5uy_")) {
                                navController.navigate(AlbumDestination(browseId = playlistId))
                            } else if (playlistId.startsWith("VL")) {
                                navController.navigate(PlaylistDestination(playlistId = playlistId))
                            } else {
                                navController.navigate(PlaylistDestination(playlistId = "VL$playlistId"))
                            }
                        }
                    }

                    "channel", "c" -> {
                        val artistId =
                            if (data.scheme == "simpmusic") {
                                segments.firstOrNull()
                            } else {
                                segments.getOrNull(2)
                            }
                        artistId?.let {
                            if (it.startsWith("UC")) {
                                navController.navigate(ArtistDestination(channelId = it))
                            } else {
                                viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                            }
                        }
                    }

                    "album" -> {
                        data.getQueryParameter("id")?.let { albumId ->
                            navController.navigate(AlbumDestination(browseId = albumId))
                        }
                    }

                    "library" -> {
                        val type = data.getQueryParameter("type")
                        if (type.isNullOrBlank()) {
                            val tab = libraryTabOf(data)
                            if (!isAppLinkAvailable(appPath, tab, localTrackingOn, youTubeLoggedIn)) {
                                viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                            } else {
                                viewModel.requestLibraryTab(tab)
                                navController.navigate(LibraryDestination)
                            }
                        } else {
                            navController.navigate(LibraryDynamicPlaylistDestination(type = type))
                        }
                    }

                    "analytics" -> {
                        if (isAppLinkAvailable(appPath, null, localTrackingOn, youTubeLoggedIn)) {
                            navController.navigate(AnalyticsDestination)
                        } else {
                            viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                        }
                    }

                    "taste" -> {
                        if (isAppLinkAvailable(appPath, null, localTrackingOn, youTubeLoggedIn)) {
                            viewModel.requestLibraryTab(LibraryChipType.YOUR_LIBRARY)
                            navController.navigate(LibraryDestination)
                        } else {
                            viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                        }
                    }

                    "recognize" -> {
                        if (isAppLinkAvailable(appPath, null, localTrackingOn, youTubeLoggedIn)) {
                            viewModel.requestSongRecognition(true)
                            navController.navigate(SearchDestination) { launchSingleTop = true }
                        } else {
                            viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                        }
                    }

                    else -> {
                        viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                    }
                }
            } else {
                Logger.d("MainActivity", "onCreate: $data")
                when (val path = data.pathSegments.firstOrNull()) {
                    "playlist" -> {
                        data
                            .getQueryParameter("list")
                            ?.let { playlistId ->
                                viewModel.setIntent(null)
                                if (playlistId.startsWith("OLAK5uy_")) {
                                    navController.navigate(
                                        AlbumDestination(
                                            browseId = playlistId,
                                        ),
                                    )
                                } else if (playlistId.startsWith("VL")) {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = playlistId,
                                        ),
                                    )
                                } else {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = "VL$playlistId",
                                        ),
                                    )
                                }
                            }
                    }

                    "channel", "c" -> {
                        data.lastPathSegment?.let { artistId ->
                            if (artistId.startsWith("UC")) {
                                viewModel.setIntent(null)
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = artistId,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(
                                    getString(
                                        Res.string.this_link_is_not_supported,
                                    ),
                                )
                            }
                        }
                    }

                    else -> {
                        when {
                            path == "watch" -> data.getQueryParameter("v")
                            data.host == "youtu.be" -> path
                            else -> null
                        }?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }
                }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        Logger.d("MainActivity", "Current destination: ${navBackStackEntry?.destination?.route}")
        if (navBackStackEntry?.destination?.route?.contains("FullscreenDestination") == true) {
            isShowNowPlaylistScreen = false
        }
        isInFullscreen = navBackStackEntry?.destination?.hierarchy?.any {
            it.hasRoute(FullscreenDestination::class) || it.hasRoute(WrappedDestination::class)
        } == true
    }
    LaunchedEffect(showAnalyticsTab) {
        if (!showAnalyticsTab &&
            navBackStackEntry?.destination?.hierarchy?.any {
                it.hasRoute(AnalyticsDestination::class)
            } == true
        ) {
            navController.navigate(HomeDestination) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    LaunchedEffect(showMixForYouTab) {
        if (!showMixForYouTab &&
            navBackStackEntry?.destination?.hierarchy?.any {
                it.hasRoute(MixForYouDestination::class)
            } == true
        ) {
            navController.navigate(HomeDestination) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val isTablet = windowSize.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isTabletLandscape = isTablet && currentOrientation() == Orientation.LANDSCAPE

    AppTheme(
        themeMode = themeMode,
        themeColorSource = themeColorSource,
        customThemeColor = parseThemeColorHex(customThemeColorHex),
        liquidGlassEnabled = isLiquidGlassEnabled == TRUE,
    ) {
        if (!isOfficialBuild) {
            UnofficialBuildScreen()
            return@AppTheme
        }
        val isLightScheme = MaterialTheme.colorScheme.background.luminance() > 0.5f

        val desktopWindow = if (isLightScheme) desktopWindowLight else desktopWindowDark
        val desktopPanel =
            if (isLightScheme) MaterialTheme.colorScheme.surfaceContainer else desktopPanelDark

        val backdrop = rememberBackdrop(if (isDesktopShell) desktopPanel else MaterialTheme.colorScheme.background)
        Scaffold(
            // Draw the app surface edge-to-edge behind the status bar/notch. Individual
            // top bars retain their own safe insets for readable controls.
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier =
                Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (!userStarted) {
                            val type = awaitPointerEvent(PointerEventPass.Initial).type
                            if (type == PointerEventType.Press || type == PointerEventType.Scroll) {
                                userStarted = true
                            }
                        }
                    }
                },
            containerColor =
                if (isDesktopShell) desktopWindow else MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isTablet) {
                    AnimatedVisibility(
                        isNavBarVisible,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut(),
                    ) {
                        val useFoldingBar = getPlatform() == Platform.Android || isLiquidGlassEnabled == TRUE
                        Column {
                            AnimatedVisibility(
                                isShowMiniPlayer && !useFoldingBar && isLiquidGlassEnabled == DataStoreManager.FALSE,
                                enter = fadeIn() + slideInHorizontally(),
                                exit = fadeOut(),
                            ) {
                                MiniPlayer(
                                    Modifier
                                        .height(60.dp)
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                        ).padding(
                                            bottom = 4.dp,
                                        ),
                                    backdrop = backdrop,
                                    navController = navController,
                                    onClick = {
                                        isShowNowPlaylistScreen = true
                                    },
                                    onClose = {
                                        viewModel.stopPlayer()
                                        viewModel.isServiceRunning = false
                                    },
                                )
                            }
                            if (useFoldingBar) {
                                LiquidGlassAppBottomNavigationBar(
                                    navController = navController,
                                    backdrop = backdrop,
                                    viewModel = viewModel,
                                    onOpenNowPlaying = { isShowNowPlaylistScreen = true },
                                    showAnalyticsTab = showAnalyticsTab,
                                    showMixForYouTab = showMixForYouTab,
                                    liquidGlass = isLiquidGlassEnabled == TRUE,
                                ) { klass ->
                                    viewModel.reloadDestination(klass)
                                }
                            } else {
                                AppBottomNavigationBar(
                                    navController = navController,
                                    showAnalyticsTab = showAnalyticsTab,
                                    showMixForYouTab = showMixForYouTab,
                                ) { klass ->
                                    viewModel.reloadDestination(klass)
                                }
                            }
                        }
                    }
                }
            },
            content = { innerPadding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .then(
                            if (isLiquidGlassEnabled == TRUE && !isTablet) {
                                Modifier.layerBackdrop(backdrop)
                            } else {
                                Modifier
                            },
                        )
                ) {
                    val tabletMiniPlayer: @Composable (Modifier) -> Unit = { modifier ->
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isShowMiniPlayer && isTablet && !isInFullscreen,
                            modifier = modifier,
                            enter = fadeIn() + slideInHorizontally(),
                            exit = fadeOut(),
                        ) {
                            MiniPlayer(
                                if (getPlatform() == Platform.Android) {
                                    Modifier
                                        .height(if (isLiquidGlassEnabled == TRUE) 56.dp else 60.dp)
                                        .fillMaxWidth(0.8f)
                                        .padding(
                                            horizontal = 12.dp,
                                        ).padding(
                                            bottom = 4.dp,
                                        )
                                } else {
                                    Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 20.dp)
                                        .height(60.dp)
                                },
                                backdrop = backdrop,
                                navController = navController,
                                onClick = {
                                    isShowNowPlaylistScreen = true
                                },
                                onClose = {
                                    viewModel.stopPlayer()
                                    viewModel.isServiceRunning = false
                                },
                                onOpenFullscreenLyrics = {
                                    viewModel.requestFullscreenLyrics()
                                    isShowNowPlaylistScreen = true
                                },
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxSize(),
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (isLiquidGlassEnabled == TRUE && isDesktopShell && isTablet && !isInFullscreen) {
                                            Modifier.layerBackdrop(backdrop)
                                        } else {
                                            Modifier
                                        },
                                    ),
                            ) {
                                if (isTablet && !isInFullscreen) {
                                    if (isDesktopShell) {
                                        DesktopSidebar(
                                            navController = navController,
                                            containerColor = desktopPanel,
                                            showAnalyticsTab = showAnalyticsTab,
                                            showMixForYouTab = showMixForYouTab,
                                            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                        ) { klass ->
                                            viewModel.reloadDestination(klass)
                                        }
                                    } else {
                                        AppNavigationRail(
                                            navController = navController,
                                            showAnalyticsTab = showAnalyticsTab,
                                            showMixForYouTab = showMixForYouTab,
                                        ) { klass ->
                                            viewModel.reloadDestination(klass)
                                        }
                                    }
                                }
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                        .then(
                                            if (isDesktopShell) {
                                                Modifier
                                                    .padding(8.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(desktopPanel)
                                            } else {
                                                Modifier
                                            },
                                        ),
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .then(
                                                if (isLiquidGlassEnabled == TRUE && !isDesktopShell && isTablet && !isInFullscreen) {
                                                    Modifier.layerBackdrop(backdrop)
                                                } else {
                                                    Modifier
                                                },
                                            ).hazeSource(hazeState),
                                    ) {
                                        CompositionLocalProvider(
                                            LocalAppDialogOpen provides appDialogOpen,
                                            LocalNowPlayingOpen provides isShowNowPlaylistScreen,
                                            LocalUserStarted provides userStarted,
                                        ) {
                                            AppNavigationGraph(
                                                innerPadding = innerPadding,
                                                navController = navController,
                                                hideNavBar = {
                                                    isNavBarVisible = false
                                                },
                                                showNavBar = {
                                                    isNavBarVisible = true
                                                },
                                                showNowPlayingSheet = {
                                                    isShowNowPlaylistScreen = true
                                                },
                                                onScrolling = {},
                                            )
                                        }
                                    }
                                    if (!isDesktopShell) {
                                        tabletMiniPlayer(Modifier.padding(innerPadding).align(Alignment.BottomCenter))
                                    }
                                }
                            }
                            if (isDesktopShell) {
                                tabletMiniPlayer(Modifier.padding(innerPadding).align(Alignment.BottomCenter))
                            }
                        }
                        if (isTablet && isTabletLandscape && !isInFullscreen) {
                            AnimatedVisibility(
                                isShowNowPlaylistScreen,
                                enter = expandHorizontally() + fadeIn(),
                                exit = fadeOut() + shrinkHorizontally(),
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxHeight()
                                        .layout { measurable, constraints ->
                                            val width =
                                                (
                                                    ((constraints.maxWidth - 80.dp.roundToPx()) * 0.35f).roundToInt() -
                                                        if (isDesktopShell) 8.dp.roundToPx() else 0
                                                ).coerceIn(0, constraints.maxWidth)
                                            val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                                            layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                                        },
                                ) {
                                    if (!isDesktopShell) Spacer(Modifier.width(8.dp))
                                    Box(
                                        Modifier
                                            .padding(
                                                innerPadding.copy(
                                                    start = 0.dp,
                                                    top = 0.dp,
                                                    bottom = 0.dp,
                                                ),
                                            ).then(
                                                if (isDesktopShell) {
                                                    Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                                                } else {
                                                    Modifier
                                                },
                                            ).clip(
                                                RoundedCornerShape(12.dp),
                                            ).then(
                                                if (isDesktopShell) {
                                                    Modifier.background(desktopPanel)
                                                } else {
                                                    Modifier
                                                },
                                            ),
                                    ) {
                                        ForceDarkContent {
                                            NowPlayingScreenContent(
                                                navController = navController,
                                                sharedViewModel = viewModel,
                                                isExpanded = true,
                                                dismissIcon = SimpIcons.ArrowForwardIos,
                                            ) {
                                                isShowNowPlaylistScreen = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isDesktopShell && !isInFullscreen) {
                        Box(
                            Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(52.dp)
                                .drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { RectangleShape },
                                    effects = { blur(24.dp.toPx()) },
                                ).background(MaterialTheme.colorScheme.background.copy(alpha = 0.16f)),
                        )
                    }
                }

                if (isShowNowPlaylistScreen && !isTabletLandscape) {
                    ForceDarkContent {
                        NowPlayingScreen(
                            navController = navController,
                        ) {
                            isShowNowPlaylistScreen = false
                        }
                    }
                }

                
            },
        )
    }
}
