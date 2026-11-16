package com.maxrave.simpmusic.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kmpalette.loader.rememberNetworkLoader
import com.kmpalette.rememberDominantColorState
import com.maxrave.common.CHART_SUPPORTED_COUNTRY
import com.maxrave.common.Config
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.home.HomeItem
import com.maxrave.domain.data.model.home.chart.Chart
import com.maxrave.domain.data.model.mood.Mood
import com.maxrave.domain.extension.now
import com.maxrave.domain.mediaservice.handler.PlaylistType
import com.maxrave.domain.mediaservice.handler.QueueData
import com.maxrave.domain.utils.toSongEntity
import com.maxrave.domain.utils.toTrack
import com.maxrave.logger.Logger
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.expect.ui.HorizontalScrollBar
import com.maxrave.simpmusic.expect.ui.layerBackdrop
import com.maxrave.simpmusic.expect.ui.rememberBackdrop
import com.maxrave.simpmusic.extension.angledGradientBackground
import com.maxrave.simpmusic.extension.artworkScrimBrush
import com.maxrave.simpmusic.extension.getScreenSizeInfo
import com.maxrave.simpmusic.extension.isScrollingUp
import com.maxrave.simpmusic.extension.mouseDragScroll
import com.maxrave.simpmusic.extension.rgbFactor
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.ui.component.CenterLoadingBox
import com.maxrave.simpmusic.ui.component.Chip
import com.maxrave.simpmusic.ui.component.CreateActions
import com.maxrave.simpmusic.ui.component.DropdownButton
import com.maxrave.simpmusic.ui.component.EndOfPage
import com.maxrave.simpmusic.ui.component.ExpandableChip
import com.maxrave.simpmusic.ui.component.HomeDiscoverSection
import com.maxrave.simpmusic.ui.component.HomeItem
import com.maxrave.simpmusic.ui.component.HomeItemContentPlaylist
import com.maxrave.simpmusic.ui.component.HomeShimmer
import com.maxrave.simpmusic.ui.component.ItemArtistChart
import com.maxrave.simpmusic.ui.component.MoodMomentAndGenreHomeItem
import com.maxrave.simpmusic.ui.component.NowPlayingBottomSheet
import com.maxrave.simpmusic.ui.component.OfflineErrorState
import com.maxrave.simpmusic.ui.component.QuickPicksItem
import com.maxrave.simpmusic.ui.component.SectionHeaderButton
import com.maxrave.simpmusic.ui.component.homeCardAspectRatio
import com.maxrave.simpmusic.ui.component.rememberHolderPainter
import com.maxrave.simpmusic.ui.icon.Notifications
import com.maxrave.simpmusic.ui.icon.Person
import com.maxrave.simpmusic.ui.icon.Settings
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.MoodDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.NotificationDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.SettingsDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.ArtistDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.PodcastDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.PlaylistDestination
import com.maxrave.simpmusic.ui.navigation.destination.login.LoginDestination
import com.maxrave.simpmusic.ui.screen.library.LibraryDynamicPlaylistType
import com.maxrave.simpmusic.ui.theme.LocalLiquidGlassEnabled
import com.maxrave.simpmusic.ui.theme.desktopPanelDark
import com.maxrave.simpmusic.ui.theme.typo
import com.maxrave.simpmusic.viewModel.HomeViewModel
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_COMMUTE
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_ENERGIZE
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_FEEL_GOOD
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_FOCUS
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_PARTY
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_RELAX
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_ROMANCE
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_SAD
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_SLEEP
import com.maxrave.simpmusic.viewModel.HomeViewModel.Companion.HOME_PARAMS_WORKOUT
import com.maxrave.simpmusic.viewModel.ListState
import com.maxrave.simpmusic.viewModel.SharedViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.all
import simpmusic.composeapp.generated.resources.chart
import simpmusic.composeapp.generated.resources.commute
import simpmusic.composeapp.generated.resources.energize
import simpmusic.composeapp.generated.resources.feel_good
import simpmusic.composeapp.generated.resources.focus
import simpmusic.composeapp.generated.resources.good_afternoon
import simpmusic.composeapp.generated.resources.good_evening
import simpmusic.composeapp.generated.resources.good_morning
import simpmusic.composeapp.generated.resources.good_night
import simpmusic.composeapp.generated.resources.guest
import simpmusic.composeapp.generated.resources.let_s_pick_a_playlist_for_you
import simpmusic.composeapp.generated.resources.let_s_start_with_a_radio
import simpmusic.composeapp.generated.resources.log_in_to_YouTube
import simpmusic.composeapp.generated.resources.mood
import simpmusic.composeapp.generated.resources.notification
import simpmusic.composeapp.generated.resources.party
import simpmusic.composeapp.generated.resources.play_all
import simpmusic.composeapp.generated.resources.quick_picks
import simpmusic.composeapp.generated.resources.relax
import simpmusic.composeapp.generated.resources.romance
import simpmusic.composeapp.generated.resources.sad
import simpmusic.composeapp.generated.resources.sleep
import simpmusic.composeapp.generated.resources.top_artists
import simpmusic.composeapp.generated.resources.what_is_best_choice_today
import simpmusic.composeapp.generated.resources.workout

private val HomeGlowOverflow = 140.dp

private val homeMoods =
    listOf(
        Res.string.relax to HOME_PARAMS_RELAX,
        Res.string.sleep to HOME_PARAMS_SLEEP,
        Res.string.energize to HOME_PARAMS_ENERGIZE,
        Res.string.sad to HOME_PARAMS_SAD,
        Res.string.romance to HOME_PARAMS_ROMANCE,
        Res.string.feel_good to HOME_PARAMS_FEEL_GOOD,
        Res.string.workout to HOME_PARAMS_WORKOUT,
        Res.string.party to HOME_PARAMS_PARTY,
        Res.string.commute to HOME_PARAMS_COMMUTE,
        Res.string.focus to HOME_PARAMS_FOCUS,
    )

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    onScrolling: (onTop: Boolean) -> Unit = {},
    viewModel: HomeViewModel =
        koinViewModel(),
    sharedViewModel: SharedViewModel =
        koinInject(),
    navController: NavController,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val isScrollingUp by scrollState.isScrollingUp()
    val accountInfo by viewModel.accountInfo.collectAsStateWithLifecycle()
    val homeData by viewModel.homeItemList.collectAsStateWithLifecycle()
    val quickPicksTitle = stringResource(Res.string.quick_picks)
    // Build the transformed Quick Picks shelf once per feed update, not once per shelf
    // recomposition. The original list remains the source of truth.
    val quickPicksShelf =
        remember(homeData, quickPicksTitle) {
            homeData.firstOrNull { it.title == quickPicksTitle }?.let { shelf ->
                shelf.copy(
                    contents =
                        shelf.contents.mapNotNull { content ->
                            content?.copy(
                                artists =
                                    content.artists?.let { artists ->
                                        if (artists.size > 1) artists.dropLast(1) else artists
                                    },
                            )
                        },
                )
            }
        }
    val homeShelves =
        remember(homeData, accountInfo) {
            val accountName = accountInfo?.first
            if (accountName.isNullOrBlank()) {
                homeData
            } else {
                homeData.map {
                    if (it.subtitle.equals(accountName, ignoreCase = true)) it.copy(thumbnail = null) else it
                }
            }
        }
    val newRelease by viewModel.newRelease.collectAsStateWithLifecycle()
    val homeShelfKeys =
        remember(homeShelves) {
            val occurrences = mutableMapOf<String, Int>()
            homeShelves.map { shelf ->
                val firstContentId =
                    shelf.contents.firstNotNullOfOrNull { content ->
                        content?.videoId ?: content?.browseId ?: content?.playlistId
                    }.orEmpty()
                val identity = "${shelf.title}:${shelf.moreEndpoint?.browseId.orEmpty()}:${shelf.channelId.orEmpty()}:$firstContentId"
                val occurrence = occurrences[identity] ?: 0
                occurrences[identity] = occurrence + 1
                "home-shelf:$identity:$occurrence"
            }
        }
    val newReleaseShelfKeys =
        remember(newRelease) {
            val occurrences = mutableMapOf<String, Int>()
            newRelease.map { shelf ->
                val firstContentId =
                    shelf.contents.firstNotNullOfOrNull { content ->
                        content?.videoId ?: content?.browseId ?: content?.playlistId
                    }.orEmpty()
                val identity = "${shelf.title}:${shelf.moreEndpoint?.browseId.orEmpty()}:${shelf.channelId.orEmpty()}:$firstContentId"
                val occurrence = occurrences[identity] ?: 0
                occurrences[identity] = occurrence + 1
                "new-release-shelf:$identity:$occurrence"
            }
        }
    val chart by viewModel.chart.collectAsStateWithLifecycle()
    val moodMomentAndGenre by viewModel.exploreMoodItem.collectAsStateWithLifecycle()
    val chartLoading by viewModel.loadingChart.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val regionChart by viewModel.regionCodeChart.collectAsStateWithLifecycle()
    val reloadDestination by sharedViewModel.reloadDestination.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val chipRowState = rememberScrollState()
    val params by viewModel.params.collectAsStateWithLifecycle()
    val homeListState by viewModel.homeListState.collectAsStateWithLifecycle()
    val continuation by viewModel.continuation.collectAsStateWithLifecycle()

    val language = Locale.current.toLanguageTag().substringBefore('-')

    val backgroundColor = MaterialTheme.colorScheme.background
    val isLightTheme = backgroundColor.luminance() > 0.5f
    val pageBackground =
        if (getPlatform() == Platform.Desktop) {
            if (isLightTheme) MaterialTheme.colorScheme.surfaceContainer else desktopPanelDark
        } else {
            backgroundColor
        }
    var topHeaderColor by remember {
        mutableStateOf(backgroundColor)
    }
    val animatedColor by animateColorAsState(topHeaderColor, tween(500))
    val mainHomeThumbnail by viewModel.mainHomeThumbnail.collectAsStateWithLifecycle()
    val headerImage by viewModel.homeBackground.collectAsStateWithLifecycle()
    var headerImageFailed by remember(headerImage) { mutableStateOf(false) }
    val imageHttpClient = remember { HttpClient(CIO) }
    DisposableEffect(imageHttpClient) {
        onDispose { imageHttpClient.close() }
    }
    val networkLoader = rememberNetworkLoader(imageHttpClient)
    val dominantColorState =
        rememberDominantColorState(
            defaultColor = backgroundColor,
            defaultOnColor = backgroundColor,
            loader = networkLoader,
        )

    LaunchedEffect(mainHomeThumbnail) {
        mainHomeThumbnail?.let {
            dominantColorState.updateFrom(Url(it))
        }
    }

    LaunchedEffect(dominantColorState, isLightTheme) {
        snapshotFlow { dominantColorState.color }.collect {
            topHeaderColor = if (isLightTheme) lerp(it, Color.White, 0.85f) else it.rgbFactor(0.3f)
        }
    }

    var topAppBarHeightPx by rememberSaveable {
        mutableIntStateOf(0)
    }

    var moodExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val atTop by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0 }
    }
    val chipBackdrop = rememberBackdrop(MaterialTheme.colorScheme.background)

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect {
                if (it <= 1) {
                    onScrolling.invoke(true)
                } else {
                    onScrolling.invoke(isScrollingUp)
                }
            }
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getHomeItemList(params)
        Logger.w("HomeScreen", "onRefresh")
    }
    LaunchedEffect(key1 = reloadDestination) {
        if (reloadDestination == HomeDestination::class) {
            if (scrollState.firstVisibleItemIndex > 1) {
                Logger.w("HomeScreen", "scrollState.firstVisibleItemIndex: ${scrollState.firstVisibleItemIndex}")
                scrollState.animateScrollToItem(0)
                sharedViewModel.reloadDestinationDone()
            } else {
                Logger.w("HomeScreen", "scrollState.firstVisibleItemIndex: ${scrollState.firstVisibleItemIndex}")
                onRefresh.invoke()
            }
        }
    }
    LaunchedEffect(key1 = loading) {
        if (!loading) {
            isRefreshing = false
            sharedViewModel.reloadDestinationDone()
            coroutineScope.launch {
                pullToRefreshState.animateToHidden()
            }
        }
    }

    val shouldStartPaginate =
        remember {
            derivedStateOf {
                homeListState != ListState.PAGINATION_EXHAUST &&
                    (
                        scrollState.layoutInfo.visibleItemsInfo
                            .lastOrNull()
                            ?.index ?: -9
                    ) >= (scrollState.layoutInfo.totalItemsCount - 1)
            }
        }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        Logger.d("HomeScreen", "shouldStartPaginate: ${shouldStartPaginate.value}")
        Logger.d("HomeScreen", "homeListState: $homeListState")
        Logger.d("HomeScreen", "Continuation: $continuation")
        if (shouldStartPaginate.value && homeListState == ListState.IDLE) {
            viewModel.getContinueHomeItem(
                continuation,
            )
        }
    }

    Box {
        PullToRefreshBox(
            modifier =
                Modifier
                    .then(if (LocalLiquidGlassEnabled.current) Modifier.layerBackdrop(chipBackdrop) else Modifier),
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isRefreshing,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(
                                top =
                                    with(LocalDensity.current) {
                                        topAppBarHeightPx.toDp()
                                    },
                            ),
                    containerColor = PullToRefreshDefaults.indicatorContainerColor,
                    color = PullToRefreshDefaults.indicatorColor,
                    maxDistance = PullToRefreshDefaults.PositionalThreshold,
                )
            },
        ) {
            Crossfade(targetState = loading, label = "Home Shimmer") { loading ->
                if (!loading) {
                    if (homeData.isEmpty()) {
                        OfflineErrorState(
                            onRetry = onRefresh,
                            onOpenDownloaded = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.Downloaded.toStringParams(),
                                    ),
                                )
                            },
                        )
                        return@Crossfade
                    }
                    LazyColumn(
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(if (getPlatform() == Platform.Android) 6.dp else 8.dp),
                    ) {
                        itemsIndexed(
                            items = homeShelves,
                            key = { index, _ -> homeShelfKeys.getOrElse(index) { "home-shelf:$index" } },
                            contentType = { _, _ -> "home-shelf" },
                        ) { index, item ->
                            Box {
                                if (index == 0) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .matchParentSize()
                                                .layout { measurable, constraints ->
                                                    val height = constraints.maxHeight + HomeGlowOverflow.roundToPx()
                                                    val placeable =
                                                        measurable.measure(constraints.copy(minHeight = height, maxHeight = height))
                                                    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, 0) }
                                                }.angledGradientBackground(listOf(animatedColor, pageBackground), 25f),
                                    ) {
                                        if (headerImage != null && !headerImageFailed) {
                                            AsyncImage(
                                                model =
                                                    ImageRequest
                                                        .Builder(LocalPlatformContext.current)
                                                        .data(headerImage)
                                                        .crossfade(550)
                                                        .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                onError = { headerImageFailed = true },
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .angledGradientBackground(listOf(pageBackground.copy(alpha = 0.6f), pageBackground), 25f),
                                            )
                                        }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(0.6f)
                                                    .align(Alignment.BottomCenter)
                                                    .background(artworkScrimBrush(pageBackground)),
                                        )
                                    }
                                }
                                Column(
                                    modifier =
                                        Modifier
                                            .padding(horizontal = if (getPlatform() == Platform.Android) 12.dp else 15.dp),
                                ) {
                                    if (index == 0) {
                                        Spacer(
                                            Modifier.height(
                                                with(LocalDensity.current) { topAppBarHeightPx.toDp() } + 8.dp,
                                            ),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (item.title == quickPicksTitle) {
                                        AnimatedVisibility(visible = quickPicksShelf != null) {
                                            quickPicksShelf?.let { shelf ->
                                                QuickPicks(
                                                    homeItem = shelf,
                                                    navController = navController,
                                                    viewModel = viewModel,
                                                )
                                            }
                                        }
                                    } else if (item.isSongCardShelf()) {
                                        HomeDiscoverSection(
                                            data = item,
                                            navController = navController,
                                            homeViewModel = viewModel,
                                        )
                                    } else {
                                        HomeItem(
                                            navController = navController,
                                            data = item,
                                        )
                                    }
                                }
                            }
                        }
                        item(key = "home-pagination", contentType = "loading") {
                            AnimatedVisibility(
                                homeListState == ListState.PAGINATING,
                                enter = expandVertically() + expandVertically(),
                                exit = fadeOut() + shrinkVertically(),
                            ) {
                                CenterLoadingBox(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                )
                            }
                        }
                        if (homeListState == ListState.PAGINATION_EXHAUST) {
                            itemsIndexed(
                                items = newRelease,
                                key = { index, _ -> newReleaseShelfKeys.getOrElse(index) { "new-release-shelf:$index" } },
                                contentType = { _, _ -> "home-shelf" },
                            ) { _, item ->
                                AnimatedVisibility(
                                    visible = newRelease.isNotEmpty(),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = if (getPlatform() == Platform.Android) 12.dp else 15.dp),
                                    ) {
                                        HomeItem(
                                            navController = navController,
                                            data = item,
                                        )
                                    }
                                }
                            }
                            item(key = "home-moods", contentType = "mood-shelf") {
                                AnimatedVisibility(
                                    visible = moodMomentAndGenre != null,
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = if (getPlatform() == Platform.Android) 12.dp else 15.dp),
                                    ) {
                                        moodMomentAndGenre?.let {
                                            MoodMomentAndGenre(
                                                mood = it,
                                                navController = navController,
                                                backdrop = chipBackdrop,
                                            )
                                        }
                                    }
                                }
                            }
                            item(key = "home-chart", contentType = "chart-section") {
                                Column(
                                    Modifier
                                        .padding(vertical = 10.dp)
                                        .padding(horizontal = if (getPlatform() == Platform.Android) 12.dp else 15.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    ChartTitle()
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Crossfade(targetState = regionChart) {
                                        if (it != null) {
                                            DropdownButton(
                                                items = CHART_SUPPORTED_COUNTRY.itemsData.toList(),
                                                defaultSelected =
                                                    CHART_SUPPORTED_COUNTRY.itemsData.getOrNull(
                                                        CHART_SUPPORTED_COUNTRY.items.indexOf(it),
                                                    )
                                                        ?: CHART_SUPPORTED_COUNTRY.itemsData[1],
                                            ) {
                                                viewModel.exploreChart(
                                                    CHART_SUPPORTED_COUNTRY.items[
                                                        CHART_SUPPORTED_COUNTRY.itemsData.indexOf(
                                                            it,
                                                        ),
                                                    ],
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Crossfade(
                                        targetState = chartLoading,
                                        label = "Chart",
                                    ) { loading ->
                                        if (!loading) {
                                            chart?.let {
                                                ChartData(
                                                    chart = it,
                                                    navController = navController,
                                                )
                                            }
                                        } else {
                                            CenterLoadingBox(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height(400.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item(key = "home-end", contentType = "footer") {
                            EndOfPage()
                        }
                    }
                } else {
                    Column {
                        Spacer(
                            Modifier.height(
                                with(LocalDensity.current) {
                                    topAppBarHeightPx.toDp()
                                } + 8.dp,
                            ),
                        )
                        HomeShimmer()
                    }
                }
            }
        }
        AnimatedContent(
            targetState = atTop,
            transitionSpec = {
                fadeIn(tween(300)).togetherWith(fadeOut(tween(300)))
            },
        ) { target ->
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .then(
                            if (target) {
                                Modifier.background(Color.Transparent)
                            } else {
                                Modifier
                            },
                        ).onGloballyPositioned { coordinates ->
                            topAppBarHeightPx = coordinates.size.height
                        },
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(
                                WindowInsets.statusBars,
                            ),
                )
                val createPinned = getPlatform() == Platform.Desktop || getScreenSizeInfo().let { it.wDP >= it.hDP }
                val chipHeadFade by animateFloatAsState(if (chipRowState.canScrollBackward) 1f else 0f)
                val chipTailFade by animateFloatAsState(if (chipRowState.canScrollForward) 1f else 0f)
                val createActions: @Composable () -> Unit = {
                    CreateActions(
                        backdrop = chipBackdrop,
                        navController = navController,
                        onSettings = null,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            // Keep the feed edge-to-edge while scrolling: the full filter row is
                            // only present at the top; the scrolled state is a compact glass rail.
                            .height(
                                when {
                                    target && getPlatform() == Platform.Android -> 50.dp
                                    target -> 56.dp
                                    getPlatform() == Platform.Android -> 38.dp
                                    else -> 42.dp
                                },
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .padding(start = if (getPlatform() == Platform.Android) 12.dp else 16.dp, end = 8.dp)
                            .liquidGlass(chipBackdrop, RoundedCornerShape(24.dp)),
                    ) {
                        HomeAccountButton(
                            accountName = accountInfo?.first,
                            thumbnailUrl = accountInfo?.second,
                            navController = navController,
                        )
                    }
                    BoxWithConstraints(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        if (target) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .chipStripEdgeFades(32.dp, start = { chipHeadFade }, end = { chipTailFade })
                                        .mouseDragScroll(chipRowState)
                                        .horizontalScroll(chipRowState)
                                        .widthIn(min = maxWidth),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Chip(
                                        isAnimated = loading,
                                        isSelected = params == null && !moodExpanded,
                                        text = stringResource(Res.string.all),
                                        backdrop = chipBackdrop,
                                    ) {
                                        moodExpanded = false
                                        viewModel.setParams(null)
                                    }
                                    ExpandableChip(
                                        text = stringResource(Res.string.mood),
                                        options = homeMoods.map { stringResource(it.first) },
                                        selectedIndex = homeMoods.indexOfFirst { it.second == params }.takeIf { it >= 0 },
                                        expanded = moodExpanded,
                                        onExpandedChange = { moodExpanded = it },
                                        onSelect = { index ->
                                            moodExpanded = false
                                            viewModel.setParams(homeMoods[index].second)
                                        },
                                        isAnimated = loading,
                                        backdrop = chipBackdrop,
                                    )
                                }
                            }
                    }
                    }
                    if (!createPinned) {
                        Box(Modifier.padding(start = 8.dp, end = 12.dp)) { createActions() }
                    } else {
                        Box(Modifier.padding(start = 8.dp, end = 16.dp)) { createActions() }
                    }
                }
            }
        }
    }
}

internal fun Modifier.chipStripEdgeFades(
    width: Dp,
    start: () -> Float,
    end: () -> Float,
): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val fadePx = width.toPx().coerceAtMost(size.width / 2f)
            val startFade = start()
            if (startFade > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(colorStops = chipFadeStops(startFade, fadingOut = false), startX = 0f, endX = fadePx),
                    blendMode = BlendMode.DstIn,
                )
            }
            val endFade = end()
            if (endFade > 0f) {
                drawRect(
                    brush =
                        Brush.horizontalGradient(
                            colorStops = chipFadeStops(endFade, fadingOut = true),
                            startX = size.width - fadePx,
                            endX = size.width,
                        ),
                    blendMode = BlendMode.DstIn,
                )
            }
        }

private fun chipFadeStops(
    fade: Float,
    fadingOut: Boolean,
): Array<Pair<Float, Color>> =
    Array(CHIP_FADE_STEPS + 1) { i ->
        val t = i / CHIP_FADE_STEPS.toFloat()
        val ramp = t * t * (3f - 2f * t)
        t to Color.Black.copy(alpha = 1f - fade * (if (fadingOut) ramp else 1f - ramp))
    }

private const val CHIP_FADE_STEPS = 24

@Composable
private fun HomeAccountButton(
    accountName: String?,
    thumbnailUrl: String?,
    navController: NavController,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val signedIn = !accountName.isNullOrBlank()
    val displayName = if (signedIn) accountName.orEmpty() else stringResource(Res.string.guest)
    val hour = remember { now().time.hour }
    val greeting =
        stringResource(
            when (hour) {
                in 6..12 -> Res.string.good_morning
                in 13..17 -> Res.string.good_afternoon
                in 18..23 -> Res.string.good_evening
                else -> Res.string.good_night
            },
        )
    Box {
        AccountAvatar(
            signedIn = signedIn,
            thumbnailUrl = thumbnailUrl,
            size = if (getPlatform() == Platform.Android) 36.dp else 44.dp,
            contentDescription = displayName,
            modifier = Modifier.clickable { menuOpen = true },
        )
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
            modifier =
                Modifier
                    .widthIn(max = 280.dp)
                    .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccountAvatar(
                    signedIn = signedIn,
                    thumbnailUrl = thumbnailUrl,
                    size = 40.dp,
                    contentDescription = null,
                )
                Column {
                    Text(text = displayName, style = typo().titleSmall, maxLines = 1)
                    Text(text = greeting, style = typo().bodySmall)
                }
            }
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.notification), style = typo().bodyMedium) },
                leadingIcon = { Icon(SimpIcons.Notifications, contentDescription = null) },
                onClick = {
                    menuOpen = false
                    navController.navigate(NotificationDestination)
                },
            )
            DropdownMenuItem(
                text = { Text("Settings", style = typo().bodyMedium) },
                leadingIcon = { Icon(SimpIcons.Settings, contentDescription = null) },
                onClick = {
                    menuOpen = false
                    navController.navigate(SettingsDestination)
                },
            )
            if (!signedIn) {
                Button(
                    onClick = {
                        menuOpen = false
                        navController.navigate(LoginDestination)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Text(stringResource(Res.string.log_in_to_YouTube), color = LocalContentColor.current)
                }
            }
        }
    }
}

@Composable
private fun AccountAvatar(
    signedIn: Boolean,
    thumbnailUrl: String?,
    size: Dp,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val avatarModifier =
        Modifier
            .size(size)
            .clip(CircleShape)
            .then(modifier)
    if (signedIn && !thumbnailUrl.isNullOrBlank()) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(thumbnailUrl)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(thumbnailUrl)
                    .crossfade(true)
                    .build(),
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = avatarModifier,
        )
    } else {
        Box(
            modifier = avatarModifier.background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SimpIcons.Person,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size * 0.6f),
            )
        }
    }
}

private val LandscapeGridItemMaxWidth = 400.dp

private fun HomeItem.isSongCardShelf(): Boolean =
    itemsPerColumn == null &&
        moreEndpoint == null &&
        contents.isNotEmpty() &&
        contents.all { it != null && !it.videoId.isNullOrEmpty() && it.homeCardAspectRatio() == 1f }

@ExperimentalFoundationApi
@Composable
fun QuickPicks(
    homeItem: HomeItem,
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val lazyListState = rememberLazyGridState()
    val snapperFlingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = lazyListState, snapPosition = SnapPosition.Start))
    val quickPickKeys =
        remember(homeItem.contents) {
            val occurrences = mutableMapOf<String, Int>()
            homeItem.contents.mapIndexed { index, item ->
                val identity = item?.videoId?.takeIf { it.isNotBlank() } ?: "empty:$index"
                val occurrence = occurrences[identity] ?: 0
                occurrences[identity] = occurrence + 1
                "quick-pick:$identity:$occurrence"
            }
        }
    val isPortrait = getScreenSizeInfo().let { it.wDP < it.hDP }
    val density = LocalDensity.current
    var widthDp by remember {
        mutableStateOf(0.dp)
    }
    var bottomSheetShow by remember { mutableStateOf(false) }
    var track by remember { mutableStateOf<Track?>(null) }

    if (bottomSheetShow) {
        NowPlayingBottomSheet(
            onDismiss = { bottomSheetShow = false },
            song = track?.toSongEntity(),
            navController = navController,
        )
    }

    Column(
        Modifier
            .padding(vertical = 8.dp)
            .onGloballyPositioned { coordinates ->
                with(density) {
                    widthDp = (coordinates.size.width).toDp()
                }
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.let_s_start_with_a_radio),
                    style = typo().bodySmall,
                )
                Text(
                    text = stringResource(Res.string.quick_picks),
                    style = typo().headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                )
            }
            if (homeItem.playAllEndpoint != null) {
                SectionHeaderButton(
                    text = stringResource(Res.string.play_all),
                    onClick = { viewModel.playAll(homeItem) },
                )
            }
        }
        LazyHorizontalGrid(
            rows = GridCells.Fixed(4),
            modifier = Modifier.height(256.dp).mouseDragScroll(lazyListState, snapperFlingBehavior),
            state = lazyListState,
            flingBehavior = snapperFlingBehavior,
        ) {
            itemsIndexed(
                items = homeItem.contents,
                key = { index, _ -> quickPickKeys.getOrElse(index) { "quick-pick:$index" } },
                contentType = { _, item -> if (item == null) "empty-quick-pick" else "quick-pick" },
            ) { _, item ->
                if (item != null) {
                    QuickPicksItem(
                        onClick = {
                            val firstQueue: Track = item.toTrack()
                            viewModel.setQueueData(
                                QueueData.Data(
                                    listTracks = arrayListOf(firstQueue),
                                    firstPlayedTrack = firstQueue,
                                    playlistId = "RDAMVM${item.videoId}",
                                    playlistName = "\"${item.title}\" Radio",
                                    playlistType = PlaylistType.RADIO,
                                    continuation = null,
                                ),
                            )
                            viewModel.loadMediaItem(
                                firstQueue,
                                type = Config.SONG_CLICK,
                            )
                        },
                        onLongClick = {
                            track = item.toTrack()
                            bottomSheetShow = true
                        },
                        data = item,
                        widthDp = if (isPortrait) widthDp else minOf(widthDp, LandscapeGridItemMaxWidth + 30.dp),
                    )
                }
            }
        }
        HorizontalScrollBar(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            scrollState = lazyListState,
            flingBehavior = snapperFlingBehavior,
        )
    }
}

@Composable
fun MoodMomentAndGenre(
    mood: Mood,
    navController: NavController,
) {
    Column(
        Modifier
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.let_s_pick_a_playlist_for_you),
            style = typo().bodyMedium,
        )
        mood.sections.forEach { section ->
            val gridState = rememberLazyGridState()
            val flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = gridState))
            Text(
                text = section.title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
            )
            LazyHorizontalGrid(
                rows = GridCells.Fixed(3),
                modifier = Modifier.height(210.dp).mouseDragScroll(gridState, flingBehavior),
                state = gridState,
                flingBehavior = flingBehavior,
            ) {
                items(
                    items = section.items,
                    key = { item -> item.params },
                    contentType = { "mood-category" },
                ) { item ->
                    MoodMomentAndGenreHomeItem(
                        title = item.title,
                        stripeColor = item.stripeColor,
                    ) {
                        navController.navigate(
                            MoodDestination(
                                item.params,
                            ),
                        )
                    }
                }
            }
            HorizontalScrollBar(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                scrollState = gridState,
                flingBehavior = flingBehavior,
            )
        }
    }
}

@Composable
fun ChartTitle() {
    Column {
        Text(
            text = stringResource(Res.string.what_is_best_choice_today),
            style = typo().bodyMedium,
        )
        Text(
            text = stringResource(Res.string.chart),
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
        )
    }
}

@Composable
fun ChartData(
    chart: Chart,
    navController: NavController,
) {
    var gridWidthDp by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    val lazyListState2 = rememberLazyGridState()
    val snapperFlingBehavior2 =
        rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = lazyListState2, snapPosition = SnapPosition.Start))
    val isPortrait = getScreenSizeInfo().let { it.wDP < it.hDP }

    Column(
        Modifier.onGloballyPositioned { coordinates ->
            with(density) {
                gridWidthDp = (coordinates.size.width).toDp()
            }
        },
    ) {
        chart.listChartItem.forEach { item ->
            Text(
                text = item.title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
            )
            val lazyListState = rememberLazyListState()
            val snapperFlingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyListState = lazyListState))
            LazyRow(
                modifier = Modifier.mouseDragScroll(lazyListState, snapperFlingBehavior),
                state = lazyListState,
                flingBehavior = snapperFlingBehavior,
            ) {
                items(
                    items = item.playlists,
                    key = { playlist -> "chart-playlist:${playlist.id}" },
                    contentType = { "chart-playlist" },
                ) { playlist ->
                    HomeItemContentPlaylist(
                        onClick = {
                            navController.navigate(
                                PlaylistDestination(
                                    playlistId = playlist.id,
                                    isYourYouTubePlaylist = false,
                                ),
                            )
                        },
                        data = playlist,
                    )
                }
            }
        }
        Text(
            text = stringResource(Res.string.top_artists),
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
        )
        LazyHorizontalGrid(
            rows = GridCells.Fixed(3),
            modifier = Modifier.height(240.dp).mouseDragScroll(lazyListState2, snapperFlingBehavior2),
            state = lazyListState2,
            flingBehavior = snapperFlingBehavior2,
        ) {
            items(
                items = chart.artists.itemArtists,
                key = { artist -> "chart-artist:${artist.browseId}" },
                contentType = { "chart-artist" },
            ) { data ->
                ItemArtistChart(
                    onClick = {
                        navController.navigate(
                            ArtistDestination(
                                channelId = data.browseId,
                            ),
                        )
                    },
                    data = data,
                    widthDp = if (isPortrait) gridWidthDp else minOf(gridWidthDp, LandscapeGridItemMaxWidth),
                )
            }
        }
        HorizontalScrollBar(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            scrollState = lazyListState2,
            flingBehavior = snapperFlingBehavior2,
        )
        chart.podcasts?.let { podcasts ->
            Text(
                text = podcasts.title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
            )
            val podcastGridState = rememberLazyGridState()
            val podcastFlingBehavior =
                rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = podcastGridState, snapPosition = SnapPosition.Start))
            LazyHorizontalGrid(
                rows = GridCells.Fixed(3),
                modifier = Modifier.height(240.dp).mouseDragScroll(podcastGridState, podcastFlingBehavior),
                state = podcastGridState,
                flingBehavior = podcastFlingBehavior,
            ) {
                items(
                    items = podcasts.shows,
                    key = { show -> "chart-podcast:${show.browseId}" },
                    contentType = { "chart-podcast" },
                ) { data ->
                    ItemArtistChart(
                        onClick = { navController.navigate(PodcastDestination(podcastId = data.browseId)) },
                        data = data,
                        widthDp = if (isPortrait) gridWidthDp else minOf(gridWidthDp, LandscapeGridItemMaxWidth),
                        thumbnailShape = RoundedCornerShape(8.dp),
                        subtitle = data.subscribers,
                    )
                }
            }
            HorizontalScrollBar(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                scrollState = podcastGridState,
                flingBehavior = podcastFlingBehavior,
            )
        }
    }
}
