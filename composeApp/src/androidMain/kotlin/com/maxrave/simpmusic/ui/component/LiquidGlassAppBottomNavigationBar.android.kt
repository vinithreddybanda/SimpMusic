package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maxrave.domain.data.player.GenericMediaItem
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop
import com.maxrave.simpmusic.ui.navigation.destination.home.AnalyticsDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.MixForYouDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.ui.screen.MiniPlayer
import com.maxrave.simpmusic.ui.theme.LocalIsDarkTheme
import com.maxrave.simpmusic.viewModel.SharedViewModel
import kotlin.reflect.KClass

private const val BAR_MORPH_MS = 260
private val BarEdgePadding = 12.dp
private val SearchGap = 8.dp
private val SearchSize = 48.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean,
    showAnalyticsTab: Boolean,
    showMixForYouTab: Boolean,
    onOpenNowPlaying: () -> Unit,
    liquidGlass: Boolean,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit,
) {
    val layer = rememberGraphicsLayer()
    val searchFabInteraction = rememberGlassInteraction()
    val luminance = rememberGlassLuminance(layer, enabled = liquidGlass)

    val nowPlayingData by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val isShowMiniPlayer by remember {
        derivedStateOf {
            val item = nowPlayingData?.mediaItem
            item != null && item != GenericMediaItem.EMPTY
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavScreens =
        remember(showAnalyticsTab, showMixForYouTab) {
            listOfNotNull(
                BottomNavScreen.Home,
                BottomNavScreen.MixForYou.takeIf { showMixForYouTab },
                BottomNavScreen.Analytics.takeIf { showAnalyticsTab },
                BottomNavScreen.Library,
                BottomNavScreen.Search,
            )
        }
    val barTabs =
        remember(showAnalyticsTab, showMixForYouTab) {
            listOfNotNull(
                BottomNavScreen.Home,
                BottomNavScreen.MixForYou.takeIf { showMixForYouTab },
                BottomNavScreen.Analytics.takeIf { showAnalyticsTab },
                BottomNavScreen.Library,
            )
        }
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(
            when (startDestination) {
                is HomeDestination -> BottomNavScreen.Home.ordinal
                is SearchDestination -> BottomNavScreen.Search.ordinal
                is LibraryDestination -> BottomNavScreen.Library.ordinal
                is AnalyticsDestination -> BottomNavScreen.Analytics.ordinal
                is MixForYouDestination -> BottomNavScreen.MixForYou.ordinal
                else -> BottomNavScreen.Home.ordinal // Default to Home if not recognized
            },
        )
    }
    LaunchedEffect(showAnalyticsTab, showMixForYouTab) {
        if ((!showAnalyticsTab && selectedIndex == BottomNavScreen.Analytics.ordinal) ||
            (!showMixForYouTab && selectedIndex == BottomNavScreen.MixForYou.ordinal)
        ) {
            selectedIndex = BottomNavScreen.Home.ordinal
        }
    }
    var isExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    var isInSearchDestination by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(currentBackStackEntry, bottomNavScreens) {
        currentBackStackEntry?.destination?.let { current ->
            isInSearchDestination = current.hasRoute(SearchDestination::class)
            bottomNavScreens.firstOrNull { screen ->
                current.hierarchy.any { it.hasRoute(screen.destination::class) }
            }?.let { selectedIndex = it.ordinal }
        }
    }

    LaunchedEffect(isInSearchDestination) {
        isExpanded = !isInSearchDestination
    }

    val miniPlayerPadding = if (liquidGlass) 10.dp else 12.dp
    val miniPlayerGap = if (liquidGlass) 8.dp else 6.dp
    val constraintSet =
        remember(isShowMiniPlayer, isExpanded, liquidGlass) {
            decoupledConstraints(isShowMiniPlayer, isExpanded, miniPlayerPadding, miniPlayerGap)
        }

    LaunchedEffect(isScrolledToTop) {
        if (!isInSearchDestination) {
            isExpanded = isScrolledToTop
        }
    }

    fun selectTab(index: Int) {
        val screen = bottomNavScreens.find { it.ordinal == index } ?: return
        if (selectedIndex == index) {
            if (currentBackStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(screen.destination::class)
                } == true
            ) {
                reloadDestinationIfNeeded(screen.destination::class)
            } else {
                navController.navigate(screen.destination)
            }
        } else {
            selectedIndex = index
            navController.navigate(screen.destination) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val collapsedCircle: @Composable () -> Unit = {
        val selectedScreen = bottomNavScreens.find { it.ordinal == selectedIndex } ?: BottomNavScreen.Home
        Box(
            modifier = Modifier.fillMaxSize().clickable(enabled = !isExpanded) { isExpanded = true },
            contentAlignment = Alignment.Center,
        ) {
            if (liquidGlass) {
                selectedScreen.icon(selected = true)
            } else {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                    selectedScreen.icon(selected = true)
                }
            }
        }
    }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.navigationBars.asPaddingValues(),
                ).padding(
                    bottom = 4.dp,
                )
                .then(if (liquidGlass) Modifier.imePadding() else Modifier),
    ) {
        val capsuleBudget = maxWidth - BarEdgePadding * 2 - SearchGap - SearchSize
        val capsuleWidth =
            if (liquidGlass) {
                liquidGlassTabBarWidth(barTabs.size, capsuleBudget)
            } else {
                flatTabBarWidth(barTabs.size, capsuleBudget)
            }
        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier.fillMaxWidth(),
            animateChangesSpec = tween(BAR_MORPH_MS),
        ) {
            Layout(
                modifier =
                    Modifier
                        .layoutId("toolbar")
                        // The floating bar can be folded/unfolded directly, in addition to
                        // following scroll direction. Up folds it; down restores the full tabs.
                        .pointerInput(isExpanded) {
                            var verticalDrag = 0f
                            detectVerticalDragGestures(
                                onVerticalDrag = { change, dragAmount ->
                                    verticalDrag += dragAmount
                                    change.consume()
                                },
                                onDragEnd = {
                                    val threshold = 24.dp.toPx()
                                    if (verticalDrag < -threshold) isExpanded = false
                                    if (verticalDrag > threshold) isExpanded = true
                                    verticalDrag = 0f
                                },
                                onDragCancel = { verticalDrag = 0f },
                            )
                        },
                content = {
                    if (liquidGlass) {
                        LiquidGlassTabBar(
                            tabs = barTabs,
                            selectedTab = barTabs.indexOfFirst { it.ordinal == selectedIndex },
                            backdrop = backdrop,
                            layer = layer,
                            luminance = luminance,
                            availableWidth = capsuleBudget,
                            onTabSelected = { position -> selectTab(barTabs[position].ordinal) },
                            collapsedContent = collapsedCircle,
                        )
                        Box(
                            modifier =
                                Modifier
                                    .size(SearchSize)
                                    .drawInteractiveGlass(
                                        LocalIsDarkTheme.current,
                                        backdrop,
                                        null,
                                        { luminance.value },
                                        CircleShape,
                                        searchFabInteraction,
                                        pressedScale = 1.04f,
                                        blurScale = 0.55f,
                                        minScrim = 0.18f,
                                        maxScrim = 0.36f,
                                    ).clickable { selectTab(BottomNavScreen.Search.ordinal) },
                            contentAlignment = Alignment.Center,
                        ) {
                            BottomNavScreen.Search.icon()
                        }
                    } else {
                        FlatTabBar(
                            tabs = barTabs,
                            selectedIndex = selectedIndex,
                            availableWidth = capsuleBudget,
                            onTabSelected = { screen -> selectTab(screen.ordinal) },
                            collapsedContent = collapsedCircle,
                        )
                        FlatSearchButton(
                            selected = selectedIndex == BottomNavScreen.Search.ordinal,
                            onClick = { selectTab(BottomNavScreen.Search.ordinal) },
                        )
                    }
                },
            ) { measurables, constraints ->
                val folded = CollapsedBarSize.roundToPx()
                val capsule = capsuleWidth.roundToPx()
                val gap = SearchGap.roundToPx()
                val search = SearchSize.roundToPx()
                val expanded = capsule + gap + search
                val width =
                    if (constraints.hasFixedWidth) {
                        constraints.maxWidth
                    } else {
                        expanded.coerceIn(constraints.minWidth, constraints.maxWidth)
                    }
                val height =
                    if (constraints.hasFixedHeight) {
                        constraints.maxHeight
                    } else {
                        (if (liquidGlass) BarHeight.roundToPx() else 64.dp.roundToPx()).coerceIn(constraints.minHeight, constraints.maxHeight)
                    }
                val progress = ((width - folded).toFloat() / (expanded - folded).coerceAtLeast(1)).coerceIn(0f, 1f)
                val bar = measurables[0].measure(Constraints.fixed(lerp(folded, capsule, progress), height))
                val fab = measurables[1].measure(Constraints.fixed(search, search))
                val fabAlpha = ((progress - 0.5f) / 0.5f).coerceIn(0f, 1f)
                layout(width, height) {
                    bar.placeRelative(0, 0)
                    if (fabAlpha > 0f) {
                        fab.placeRelativeWithLayer(capsule + gap, (height - search) / 2) {
                            alpha = fabAlpha
                            scaleX = lerp(0.8f, 1f, fabAlpha)
                            scaleY = scaleX
                        }
                    }
                }
            }
            MiniPlayer(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = miniPlayerPadding)
                    .height(52.dp)
                    .layoutId("miniPlayer"),
                backdrop = backdrop,
                isVisible = isShowMiniPlayer,
                onClick = {
                    onOpenNowPlaying()
                },
                onClose = {
                    viewModel.stopPlayer()
                    viewModel.isServiceRunning = false
                },
            )
        }
    }
}

private fun decoupledConstraints(
    isMiniplayerShow: Boolean = true,
    isExpanded: Boolean,
    miniPlayerPadding: Dp,
    miniPlayerGap: Dp,
): ConstraintSet =
    ConstraintSet {
        val toolbar = createRefFor("toolbar")
        constrain(toolbar) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, margin = BarEdgePadding)
            if (!isExpanded) {
                width = Dimension.value(CollapsedBarSize)
                height = Dimension.value(CollapsedBarSize)
            } else {
                end.linkTo(parent.end, margin = BarEdgePadding)
                width = Dimension.wrapContent
                height = Dimension.value(BarHeight)
            }
        }
        val miniPlayer = createRefFor("miniPlayer")
        constrain(miniPlayer) {
            if (!isExpanded) {
                start.linkTo(toolbar.start, margin = CollapsedBarSize + miniPlayerGap - miniPlayerPadding)
                end.linkTo(parent.end)
                top.linkTo(toolbar.top)
                bottom.linkTo(toolbar.bottom)
                width = if (isMiniplayerShow) Dimension.fillToConstraints else Dimension.wrapContent
            } else {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toolbar.top, margin = miniPlayerGap)
                width = if (isMiniplayerShow) Dimension.matchParent else Dimension.wrapContent
            }
            visibility =
                if (isMiniplayerShow) {
                    Visibility.Visible
                } else {
                    Visibility.Gone
                }
        }
    }
