package com.maxrave.simpmusic.ui.screen.other

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.maxrave.common.Config
import com.maxrave.domain.data.entities.SongEntity
import com.maxrave.domain.data.player.GenericMediaItem
import com.maxrave.domain.data.model.browse.album.Track
import com.maxrave.domain.data.model.intent.GenericIntent
import com.maxrave.domain.data.model.searchResult.albums.AlbumsResult
import com.maxrave.domain.data.model.searchResult.artists.ArtistsResult
import com.maxrave.domain.data.model.searchResult.playlists.PlaylistsResult
import com.maxrave.domain.data.model.searchResult.songs.SongsResult
import com.maxrave.domain.data.model.searchResult.videos.VideosResult
import com.maxrave.domain.data.type.SearchResultType
import com.maxrave.domain.mediaservice.handler.PlaylistType
import com.maxrave.domain.mediaservice.handler.QueueData
import com.maxrave.domain.utils.connectArtists
import com.maxrave.domain.utils.toSongEntity
import com.maxrave.domain.utils.toTrack
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.expect.ui.DesktopMiniPlayerClearance
import com.maxrave.simpmusic.expect.ui.PlatformBackHandler
import com.maxrave.simpmusic.expect.ui.SongRecognitionSheet
import com.maxrave.simpmusic.expect.ui.VerticalScrollBar
import com.maxrave.simpmusic.expect.ui.layerBackdrop
import com.maxrave.simpmusic.expect.ui.rememberBackdrop
import com.maxrave.simpmusic.extension.getScreenSizeInfo
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.ui.component.AddToPlaylistModalBottomSheet
import com.maxrave.simpmusic.ui.component.CenterLoadingBox
import com.maxrave.simpmusic.ui.component.MoodCategoryCard
import com.maxrave.simpmusic.ui.component.rememberHolderPainter
import com.maxrave.simpmusic.extension.getStringBlocking
import com.maxrave.simpmusic.extension.toAppDeepLinkOrNull
import com.maxrave.simpmusic.ui.component.ArtistFullWidthItems
import com.maxrave.simpmusic.ui.component.Chip
import com.maxrave.simpmusic.ui.component.EndOfPage
import com.maxrave.simpmusic.ui.component.GlassDockIconButton
import com.maxrave.simpmusic.ui.component.GlassSuggestionRow
import com.maxrave.simpmusic.ui.component.LiquidGlassTextButton
import com.maxrave.simpmusic.ui.component.LiquidGlassButtonStyle
import com.maxrave.simpmusic.ui.component.NowPlayingBottomSheet
import com.maxrave.simpmusic.ui.component.liquidGlass
import com.maxrave.simpmusic.ui.component.PlaylistFullWidthItems
import com.maxrave.simpmusic.ui.component.ShimmerSearchItem
import com.maxrave.simpmusic.ui.component.SongFullWidthItems
import com.maxrave.simpmusic.ui.component.selection.SelectedSongsBottomSheet
import com.maxrave.simpmusic.ui.component.selection.SongSelectionTopAppBar
import com.maxrave.simpmusic.ui.component.selection.rememberSongSelectionState
import com.maxrave.simpmusic.ui.icon.ArrowBackIosNew
import com.maxrave.simpmusic.ui.icon.ArrowOutward
import com.maxrave.simpmusic.ui.icon.Close
import com.maxrave.simpmusic.ui.icon.Error
import com.maxrave.simpmusic.ui.icon.GraphicEq
import com.maxrave.simpmusic.ui.icon.History
import com.maxrave.simpmusic.ui.icon.Search
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.navigation.destination.list.AlbumDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.ArtistDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.PlaylistDestination
import com.maxrave.simpmusic.ui.navigation.destination.list.PodcastDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.MoodDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.AppLogDestination
import com.maxrave.simpmusic.ui.theme.ArtworkShape
import com.maxrave.simpmusic.ui.theme.LocalLiquidGlassEnabled
import com.maxrave.simpmusic.ui.theme.typo
import com.maxrave.simpmusic.viewModel.SearchScreenUIState
import com.maxrave.simpmusic.viewModel.SearchType
import com.maxrave.simpmusic.viewModel.SearchViewModel
import com.maxrave.simpmusic.viewModel.SharedViewModel
import com.maxrave.simpmusic.viewModel.SongSelectionViewModel
import com.maxrave.simpmusic.viewModel.toStringRes
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.albums
import simpmusic.composeapp.generated.resources.artists
import simpmusic.composeapp.generated.resources.clear_search_history
import simpmusic.composeapp.generated.resources.error_occurred
import simpmusic.composeapp.generated.resources.everything_you_need
import simpmusic.composeapp.generated.resources.in_search
import simpmusic.composeapp.generated.resources.no_results_found
import simpmusic.composeapp.generated.resources.playlists
import simpmusic.composeapp.generated.resources.podcasts
import simpmusic.composeapp.generated.resources.recognize_song
import simpmusic.composeapp.generated.resources.retry
import simpmusic.composeapp.generated.resources.search_for
import simpmusic.composeapp.generated.resources.search_for_songs_artists_albums_playlists_and_more
import simpmusic.composeapp.generated.resources.song
import simpmusic.composeapp.generated.resources.videos
import simpmusic.composeapp.generated.resources.what_do_you_want_to_listen_to

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinInject(),
    sharedViewModel: SharedViewModel = koinInject(),
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchScreenState by searchViewModel.searchScreenState.collectAsStateWithLifecycle()
    val nowPlayingData by sharedViewModel.nowPlayingState.collectAsStateWithLifecycle()
    val isMiniPlayerVisible = nowPlayingData?.mediaItem?.let { it != GenericMediaItem.EMPTY } == true
    val uiState by searchViewModel.searchScreenUIState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val moodAndGenres by searchViewModel.moodAndGenres.collectAsStateWithLifecycle()
    val moodArtwork by searchViewModel.moodArtwork.collectAsStateWithLifecycle()

    var searchUIType by rememberSaveable { mutableStateOf(SearchUIType.EMPTY) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchSubmitted by rememberSaveable { mutableStateOf(false) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    var isFocused by rememberSaveable { mutableStateOf(false) }

    val isSearchPanelVisible =
        searchUIType == SearchUIType.SEARCH_HISTORY || searchUIType == SearchUIType.SEARCH_SUGGESTIONS
    val dismissSearchPanel: () -> Unit = {
        isExpanded = false
        focusManager.clearFocus()
        keyboardController?.hide()
    }
    PlatformBackHandler(enabled = isSearchPanelVisible, onBack = dismissSearchPanel)

    // The bar floats OVER the content (a Box, not a Column) so there is something behind it to
    // blur — same arrangement HomeScreen uses. Each branch owns a scroll state, hoisted here so
    // the bar can tell whether the branch currently on screen is scrolled away from the top.
    // Two columns only on a phone held upright. Anywhere wider — tablet, landscape, desktop — two
    // columns stretch each tile to half the window, and since the tile keeps a 2:1 ratio it grows
    // absurdly tall with it.
    val screenInfo = getScreenSizeInfo()
    val isMobilePortrait = getPlatform() == Platform.Android && screenInfo.wDP < screenInfo.hDP
    val moodGridColumns = if (isMobilePortrait) 2 else 4

    val hazeState = rememberHazeState()
    // The floating chip row refracts the page scrolling under it, so the page is the glass source and
    // the row its sibling (nesting the row inside the source is the RuntimeShader feedback loop).
    val chipBackdrop = rememberBackdrop(MaterialTheme.colorScheme.background)
    val suggestionsState = rememberLazyListState()
    val historyState = rememberLazyListState()
    val moodGridState = rememberLazyGridState()
    val resultsState = rememberLazyListState()
    var searchBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val searchBarHeight = with(density) { searchBarHeightPx.toDp() }
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val isContentAtTop by remember {
        derivedStateOf {
            when (searchUIType) {
                SearchUIType.EMPTY ->
                    moodGridState.firstVisibleItemIndex == 0 && moodGridState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_HISTORY ->
                    historyState.firstVisibleItemIndex == 0 && historyState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_SUGGESTIONS ->
                    suggestionsState.firstVisibleItemIndex == 0 && suggestionsState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_RESULTS ->
                    resultsState.firstVisibleItemIndex == 0 && resultsState.firstVisibleItemScrollOffset == 0
            }
        }
    }

    val searchForString = stringResource(Res.string.search_for)
    val songString = stringResource(Res.string.song).lowercase()
    val artistString = stringResource(Res.string.artists).lowercase()
    val albumString = stringResource(Res.string.albums).lowercase()
    val playlistString = stringResource(Res.string.playlists).lowercase()
    val videoString = stringResource(Res.string.videos).lowercase()
    val podcastString = stringResource(Res.string.podcasts).lowercase()

    // Animated Placeholder
    val placeholderTexts =
        remember {
            listOf(
                "$searchForString $songString...",
                "$searchForString $artistString...",
                "$searchForString $albumString...",
                "$searchForString $playlistString...",
                "$searchForString $videoString...",
                "$searchForString $podcastString...",
            )
        }

    var currentPlaceholderIndex by remember { mutableIntStateOf(0) }

    // Animate placeholder - pause when focused
    LaunchedEffect(isFocused) {
        while (!isFocused) {
            delay(3000) // Change every 3 seconds
            currentPlaceholderIndex = (currentPlaceholderIndex + 1) % placeholderTexts.size
        }
    }

    var sheetSong by remember { mutableStateOf<SongEntity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val selectionState = rememberSongSelectionState()
    val selectionViewModel: SongSelectionViewModel = koinViewModel()
    var showSelectionSheet by rememberSaveable { mutableStateOf(false) }
    var showSelectionAddToPlaylist by rememberSaveable { mutableStateOf(false) }
    var showRecognition by rememberSaveable { mutableStateOf(false) }
    // The sheet asked for by simpmusic://recognize (the Quick Settings tile), opened once and cleared.
    val songRecognitionRequest by sharedViewModel.songRecognitionRequest.collectAsStateWithLifecycle()
    LaunchedEffect(songRecognitionRequest) {
        if (songRecognitionRequest) {
            showRecognition = true
            sharedViewModel.requestSongRecognition(false)
        }
    }
    val currentVideoId by searchViewModel.nowPlayingVideoId.collectAsStateWithLifecycle()
    val chipRowState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()

    val onMoreClick: (SongEntity) -> Unit = { song ->
        sheetSong = song
        showBottomSheet = true
    }

    LaunchedEffect(searchText) {
        if (isFocused) {
            isSearchSubmitted = false
            isExpanded = true
        }
    }

    // Keep retrieval tied to focus and submission as well as text. This cancels suggestions
    // immediately when the field loses focus or a full search is submitted.
    LaunchedEffect(searchText, isFocused, isSearchSubmitted) {
        val normalizedQuery = searchText.trim()
        if (isFocused && !isSearchSubmitted && normalizedQuery.isNotEmpty()) {
            searchViewModel.suggestQuery(normalizedQuery)
        } else {
            searchViewModel.suggestQuery("")
        }
    }

    LaunchedEffect(isSearchSubmitted) {
        if (isSearchSubmitted) {
            isExpanded = false
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            isExpanded = true
        }
    }

    LaunchedEffect(searchUIType) {
        searchViewModel.logSearchEvent("Search UI state: $searchUIType")
    }

    // Read the Compose window inset during composition; the effect only logs the captured value.
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    LaunchedEffect(isKeyboardVisible, imeBottomPx) {
        if (isKeyboardVisible) {
            delay(150)
            searchViewModel.logSearchEvent("Keyboard shown (IME bottom inset=${imeBottomPx}px)")
        } else {
            searchViewModel.logSearchEvent("Keyboard hidden")
        }
    }

    LaunchedEffect(isExpanded, searchText, isFocused) {
        searchUIType =
            if (searchText.trim().isNotEmpty() && isExpanded) {
                SearchUIType.SEARCH_SUGGESTIONS
            } else if (isFocused && isExpanded) {
                SearchUIType.SEARCH_HISTORY
            } else if (searchText.isEmpty()) {
                SearchUIType.EMPTY
            } else {
                SearchUIType.SEARCH_RESULTS
            }
    }

    //On search icon click while on search screen, open keyboard. Android only feature
    if (getPlatform() == Platform.Android) {
        val reloadDestination by sharedViewModel.reloadDestination.collectAsStateWithLifecycle()
        LaunchedEffect(reloadDestination) {
            if (reloadDestination == SearchDestination::class) {
                if (!selectionState.isActive && searchUIType == SearchUIType.EMPTY) {
                    isExpanded = true
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
                sharedViewModel.reloadDestinationDone()
            }
        }
    }

    if (showSelectionSheet) {
        val selectedIds = selectionState.selected.toList()
        SelectedSongsBottomSheet(
            count = selectedIds.size,
            onDismiss = { showSelectionSheet = false },
            onPlayNext = {
                selectionViewModel.playNext(selectedIds)
                selectionState.exit()
            },
            onAddToQueue = {
                selectionViewModel.addToQueue(selectedIds)
                selectionState.exit()
            },
            onAddToPlaylist = { showSelectionAddToPlaylist = true },
            onDownload = {
                selectionViewModel.download(selectedIds)
                selectionState.exit()
            },
            onAddToFavorite = {
                selectionViewModel.addToFavorite(selectedIds)
                selectionState.exit()
            },
        )
    }
    if (showSelectionAddToPlaylist) {
        val selectedIds = selectionState.selected.toList()
        val localPlaylists by selectionViewModel.listLocalPlaylist.collectAsStateWithLifecycle()
        AddToPlaylistModalBottomSheet(
            isBottomSheetVisible = true,
            listLocalPlaylist = localPlaylists,
            listYouTubePlaylist = emptyList(),
            onDismiss = { showSelectionAddToPlaylist = false },
            onClick = { playlist ->
                selectionViewModel.addToPlaylist(playlist.id, selectedIds)
                selectionState.exit()
            },
            onYTPlaylistClick = {},
        )
    }
    if (showBottomSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showBottomSheet = false
                sheetSong = null
            },
            navController = navController,
            song = sheetSong,
        )
    }
    if (showRecognition) {
        SongRecognitionSheet(
            onFind = { query ->
                searchText = query
                focusManager.clearFocus()
                isSearchSubmitted = true
                searchViewModel.insertSearchHistory(query)
                when (searchScreenState.searchType) {
                    SearchType.ALL -> searchViewModel.searchAll(query)
                    SearchType.SONGS -> searchViewModel.searchSongs(query)
                    SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                    SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                    SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                    SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                    SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                    SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                }
            },
            onDismiss = { showRecognition = false },
        )
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                // This is the single IME inset owner for the search page. Compose applies the
                // live keyboard bottom inset here, including height changes and IME animation.
                .imePadding()
                .background(Color.Transparent),
    ) {
        // Content scrolls under the bar (it is the haze source), so it needs top padding
        // equal to the bar's measured height to keep its first item clear of it.
        // Keep the backdrop recorder outside the glass surfaces that sample it.
        // Recording the Crossfade subtree also records the suggestions glass itself,
        // creating a RuntimeShader feedback loop when the first suggestion is composed.
        if (LocalLiquidGlassEnabled.current) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .layerBackdrop(chipBackdrop),
            )
        }
        Crossfade(
            targetState = searchUIType,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
        ) {
            when (it) {
                SearchUIType.SEARCH_SUGGESTIONS -> {
                    Box(
                        // Only the suggestion surface follows the IME. The page behind it
                        // remains full-height, avoiding a second full-screen lift.
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                    LazyColumn(
                        modifier =
                            Modifier
                                // 48 dp search dock + 12 dp dock clearance + 12 dp visual gap.
                                .padding(bottom = searchBarHeight + 16.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .widthIn(max = 344.dp)
                                .heightIn(max = 240.dp)
                                .liquidGlass(
                                    backdrop = chipBackdrop,
                                    shape = RoundedCornerShape(28.dp),
                                    interactive = false,
                                    minScrim = 0.04f,
                                    maxScrim = 0.16f,
                                ),
                        state = suggestionsState,
                        contentPadding =
                            PaddingValues(
                                top = 8.dp,
                                bottom = if (isKeyboardVisible) 8.dp else searchBarHeight + 76.dp,
                            ),
                    ) {
                        items(searchScreenState.suggestYTItems) { item ->
                            SuggestItemRow(
                                searchResult = item,
                                onItemClick = { selectedItem ->
                                    try {
                                        when (selectedItem) {
                                            is SongsResult, is VideosResult -> {
                                                focusManager.clearFocus()
                                                val firstTrack: Track =
                                                    (selectedItem as? SongsResult)?.toTrack()
                                                        ?: (selectedItem as VideosResult).toTrack()
                                                searchViewModel.setQueueData(
                                                    QueueData.Data(
                                                        listTracks = arrayListOf(firstTrack),
                                                        firstPlayedTrack = firstTrack,
                                                        playlistId = "RDAMVM${firstTrack.videoId}",
                                                    playlistName = "\"${searchText}\" ${getStringBlocking(Res.string.in_search)}",
                                                        playlistType = PlaylistType.RADIO,
                                                        continuation = null,
                                                    ),
                                                )
                                                searchViewModel.loadMediaItem(firstTrack, type = Config.SONG_CLICK)
                                            }

                                            is ArtistsResult -> {
                                                navController.navigate(ArtistDestination(selectedItem.browseId))
                                            }

                                            is AlbumsResult -> {
                                                navController.navigate(AlbumDestination(selectedItem.browseId))
                                            }

                                            is PlaylistsResult -> {
                                                navController.navigate(PlaylistDestination(selectedItem.browseId))
                                            }
                                        }
                                    } catch (exception: Exception) {
                                        searchViewModel.logSearchFailure("suggestion selection", exception)
                                    }
                                },
                            )
                        }
                        items(searchScreenState.suggestQueries) { suggestion ->
                            GlassSuggestionRow(
                                text = suggestion,
                                trailingIcon = SimpIcons.ArrowOutward,
                                onClick = {
                                    searchText = suggestion
                                    focusManager.clearFocus()
                                    isSearchSubmitted = true
                                    searchViewModel.insertSearchHistory(suggestion)
                                    when (searchScreenState.searchType) {
                                        SearchType.ALL -> searchViewModel.searchAll(suggestion)
                                        SearchType.SONGS -> searchViewModel.searchSongs(suggestion)
                                        SearchType.VIDEOS -> searchViewModel.searchVideos(suggestion)
                                        SearchType.ALBUMS -> searchViewModel.searchAlbums(suggestion)
                                        SearchType.ARTISTS -> searchViewModel.searchArtists(suggestion)
                                        SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(suggestion)
                                        SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(suggestion)
                                        SearchType.PODCASTS -> searchViewModel.searchPodcast(suggestion)
                                    }
                                },
                                onTrailingClick = {
                                    searchText = suggestion
                                    focusRequester.requestFocus()
                                },
                            )
                        }
                        item {
                            EndOfPage(
                                withoutCredit = true,
                            )
                        }
                    }
                    }
                    VerticalScrollBar(
                        scrollState = suggestionsState,
                        modifier = Modifier.padding(top = searchBarHeight, bottom = DesktopMiniPlayerClearance),
                    )
                }

                SearchUIType.SEARCH_HISTORY -> {
                    // Search history state
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                    ) {
                        LazyColumn(
                            state = historyState,
                            contentPadding = PaddingValues(top = searchBarHeight, bottom = 10.dp),
                        ) {
                            stickyHeader {
                                Crossfade(
                                    targetState = searchHistory.isNotEmpty(),
                                ) {
                                    if (it) {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.background),
                                        ) {
                                            LiquidGlassTextButton(
                                                backdrop = chipBackdrop,
                                                text = stringResource(Res.string.clear_search_history),
                                                height = 36.dp,
                                                style = LiquidGlassButtonStyle.SECONDARY,
                                                textStyle = typo().labelMedium,
                                                onClick = { searchViewModel.deleteSearchHistory() },
                                            )
                                        }
                                    }
                                }
                            }
                            items(searchHistory) { historyItem ->
                                GlassSuggestionRow(
                                    text = historyItem,
                                    leadingIcon = SimpIcons.History,
                                    trailingIcon = SimpIcons.ArrowOutward,
                                    onClick = {
                                        searchText = historyItem
                                        focusManager.clearFocus()
                                        isSearchSubmitted = true
                                        searchViewModel.insertSearchHistory(historyItem)
                                        when (searchScreenState.searchType) {
                                            SearchType.ALL -> searchViewModel.searchAll(historyItem)
                                            SearchType.SONGS -> searchViewModel.searchSongs(historyItem)
                                            SearchType.VIDEOS -> searchViewModel.searchVideos(historyItem)
                                            SearchType.ALBUMS -> searchViewModel.searchAlbums(historyItem)
                                            SearchType.ARTISTS -> searchViewModel.searchArtists(historyItem)
                                            SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(historyItem)
                                            SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(historyItem)
                                            SearchType.PODCASTS -> searchViewModel.searchPodcast(historyItem)
                                        }
                                    },
                                    onTrailingClick = {
                                        searchText = historyItem
                                        focusRequester.requestFocus()
                                    },
                                )
                            }
                            item {
                                EndOfPage(
                                    withoutCredit = true,
                                )
                            }
                        }
                    }
                    VerticalScrollBar(
                        scrollState = historyState,
                        modifier = Modifier.padding(top = searchBarHeight, bottom = DesktopMiniPlayerClearance),
                    )
                }

                SearchUIType.EMPTY -> {
                    val mood = moodAndGenres
                    if (mood == null) {
                        // First run only: the repository serves its cached copy before hitting the
                        // network, so this spinner is never seen again after the first fetch.
                        CenterLoadingBox(Modifier.fillMaxSize())
                    } else {
                        // Capped and centred: on a wide desktop window the grid would otherwise
                        // span the whole width, stretching four tiles into long bars. 1100.dp
                        // keeps a tile near 250.dp, which is its natural size.
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(moodGridColumns),
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .widthIn(max = 1100.dp)
                                    .padding(horizontal = 16.dp),
                            state = moodGridState,
                            contentPadding = PaddingValues(top = searchBarHeight),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            // Breathing room on both sides of this block: above it
                                            // sits the floating search bar, below it the tile grid.
                                            .padding(top = 36.dp, bottom = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = stringResource(Res.string.everything_you_need),
                                        style = typo().titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = stringResource(Res.string.search_for_songs_artists_albums_playlists_and_more),
                                        style = typo().bodyMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )

                                }
                            }
                            mood.sections.forEachIndexed { index, section ->
                                // First section runs straight on from the header block above it,
                                // so its own heading would just be a second title in a row.
                                if (index > 0) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            // Section titles come from YouTube already localised,
                                            // so there is no string resource to pick here.
                                            text = section.title,
                                            style = typo().titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(top = 8.dp),
                                        )
                                    }
                                }
                                // Key must include the section: every section lives in this ONE
                                // grid, and "For you" repeats categories that also appear under
                                // Moods or Genres, so params alone collides.
                                items(section.items, key = { "${section.title}/${it.params}" }) { item ->
                                    // LazyVerticalGrid only composes tiles inside the viewport, so
                                    // putting the request here IS the laziness — a category the
                                    // user never scrolls to never costs a browse.
                                    LaunchedEffect(item.params) {
                                        searchViewModel.loadMoodArtwork(item.params)
                                    }
                                    MoodCategoryCard(
                                        title = item.title,
                                        artworkUrl = moodArtwork[item.params],
                                    ) {
                                        navController.navigate(MoodDestination(item.params))
                                    }
                                }
                            }
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EndOfPage()
                            }
                        }
                        VerticalScrollBar(
                            scrollState = moodGridState,
                            modifier = Modifier.padding(top = searchBarHeight, bottom = DesktopMiniPlayerClearance),
                        )
                        }
                    }
                }

                SearchUIType.SEARCH_RESULTS -> {
                    // Content area — chips now live in the blurred bar block above.
                    Column(modifier = Modifier.fillMaxSize()) {
                        PullToRefreshBox(
                            modifier = Modifier.fillMaxSize(),
                            state = pullToRefreshState,
                            onRefresh = {
                                val query = searchText.trim()
                                if (query.isNotEmpty()) {
                                    isSearchSubmitted = true
                                    searchViewModel.insertSearchHistory(query)
                                    when (searchScreenState.searchType) {
                                        SearchType.ALL -> searchViewModel.searchAll(query)
                                        SearchType.SONGS -> searchViewModel.searchSongs(query)
                                        SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                        SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                        SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                        SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                        SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                        SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                                    }
                                }
                            },
                            isRefreshing = uiState is SearchScreenUIState.Loading,
                            indicator = {
                                PullToRefreshDefaults.Indicator(
                                    state = pullToRefreshState,
                                    isRefreshing = uiState is SearchScreenUIState.Loading,
                                    // Anchored to the top of the box, which now starts under the
                                    // bar — without this offset the spinner sits behind the bar
                                    // and only its top sliver shows.
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = searchBarHeight),
                                    containerColor = PullToRefreshDefaults.indicatorContainerColor,
                                    color = PullToRefreshDefaults.indicatorColor,
                                    maxDistance = PullToRefreshDefaults.PositionalThreshold - 5.dp,
                                )
                            },
                        ) {
                            Crossfade(targetState = uiState) { uiState ->
                                when (uiState) {
                                    is SearchScreenUIState.Loading -> {
                                        // Loading state — same top inset as the results list, or
                                        // the first shimmer row hides behind the bar and chips.
                                        LazyColumn(
                                            contentPadding =
                                                PaddingValues(
                                                    top = searchBarHeight,
                                                    bottom = 10.dp,
                                                ),
                                        ) {
                                            items(10) {
                                                ShimmerSearchItem()
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Success -> {
                                        // Success state with results
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Search Results List
                                            val currentResults =
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchScreenState.searchAllResult
                                                    SearchType.SONGS -> searchScreenState.searchSongsResult
                                                    SearchType.VIDEOS -> searchScreenState.searchVideosResult
                                                    SearchType.ALBUMS -> searchScreenState.searchAlbumsResult
                                                    SearchType.ARTISTS -> searchScreenState.searchArtistsResult
                                                    SearchType.PLAYLISTS -> searchScreenState.searchPlaylistsResult
                                                    SearchType.FEATURED_PLAYLISTS -> searchScreenState.searchFeaturedPlaylistsResult
                                                    SearchType.PODCASTS -> searchScreenState.searchPodcastsResult
                                                }

                                            Crossfade(targetState = currentResults.isNotEmpty()) {
                                                if (it) {
                                                    LazyColumn(
                                                        contentPadding =
                                                            PaddingValues(
                                                                start = 4.dp,
                                                                end = 4.dp,
                                                                top = searchBarHeight,
                                                                bottom = 10.dp,
                                                            ),
                                                        state = resultsState,
                                                    ) {
                                                        items(currentResults) { result ->
                                                            when (result) {
                                                                is SongsResult -> {
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData.Data(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${
                                                                                            getStringBlocking(
                                                                                                Res.string.in_search,
                                                                                            )
                                                                                        }",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.SONG_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                        selectionMode = selectionState.isActive,
                                                                        isSelected = selectionState.isSelected(result.videoId),
                                                                        onLongClick = { selectionState.start(it) },
                                                                        onSelectToggle = { selectionState.toggle(it) },
                                                                    )
                                                                }

                                                                is VideosResult -> {
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData.Data(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${
                                                                                            getStringBlocking(
                                                                                                Res.string.in_search,
                                                                                            )
                                                                                        }",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.VIDEO_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                        selectionMode = selectionState.isActive,
                                                                        isSelected = selectionState.isSelected(result.videoId),
                                                                        onLongClick = { selectionState.start(it) },
                                                                        onSelectToggle = { selectionState.toggle(it) },
                                                                    )
                                                                }

                                                                is AlbumsResult -> {
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            navController.navigate(
                                                                                AlbumDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is ArtistsResult -> {
                                                                    ArtistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            navController.navigate(
                                                                                ArtistDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is PlaylistsResult -> {
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            if (result.resultType == "Podcast") {
                                                                                navController.navigate(
                                                                                    PodcastDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                )
                                                                            } else {
                                                                                navController.navigate(
                                                                                    PlaylistDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                )
                                                                            }
                                                                        },
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        // Space at bottom to account for bottom navigation and mini player
                                                        item { Spacer(modifier = Modifier.height(150.dp)) }
                                                    }
                                                    VerticalScrollBar(
                                                        scrollState = resultsState,
                                                        modifier = Modifier.padding(top = searchBarHeight, bottom = DesktopMiniPlayerClearance),
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = stringResource(Res.string.no_results_found),
                                                            style = typo().titleMedium,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Error -> {
                                        Box {
                                            // Error state
                                            Column(
                                                modifier = Modifier.align(Alignment.Center),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(
                                                    text = stringResource(Res.string.error_occurred),
                                                    style = typo().titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth(),
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                LiquidGlassTextButton(
                                                    backdrop = chipBackdrop,
                                                    text = stringResource(Res.string.retry),
                                                    height = 44.dp,
                                                    style = LiquidGlassButtonStyle.PRIMARY,
                                                    textStyle = typo().labelMedium,
                                                    onClick = {
                                                        if (searchText.isNotEmpty()) {
                                                            searchViewModel.searchAll(searchText)
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }

                                    SearchScreenUIState.Empty -> {
                                        // Empty state
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.no_results_found),
                                                style = typo().titleMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = if (isKeyboardVisible) 4.dp else if (isMiniPlayerVisible) 140.dp else 76.dp,
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 344.dp)
                        .onGloballyPositioned { searchBarHeightPx = it.size.height }
                        .liquidGlass(
                            backdrop = chipBackdrop,
                            shape = RoundedCornerShape(28.dp),
                            interactive = true,
                            minScrim = 0.04f,
                            maxScrim = 0.16f,
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(visible = selectionState.isActive) {
                    SongSelectionTopAppBar(
                        state = selectionState,
                        onSelectAll = {
                            val visible =
                                when (searchScreenState.searchType) {
                                    SearchType.SONGS -> searchScreenState.searchSongsResult.map { it.videoId }
                                    SearchType.VIDEOS -> searchScreenState.searchVideosResult.map { it.videoId }
                                    SearchType.ALL ->
                                        searchScreenState.searchAllResult.mapNotNull {
                                            (it as? SongsResult)?.videoId ?: (it as? VideosResult)?.videoId
                                        }
                                    else -> emptyList()
                                }
                            selectionState.toggleSelectAll(visible)
                        },
                        onOpenActions = { showSelectionSheet = true },
                        containerColor = Color.Transparent,
                        windowInsets = WindowInsets(0),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Crossfade(
                        targetState = isSearchPanelVisible,
                        modifier = Modifier.size(44.dp),
                        animationSpec = tween(200),
                        label = "search_back_icon",
                    ) { showBack ->
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (showBack) {
                                GlassDockIconButton(
                                    imageVector = SimpIcons.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(44.dp),
                                    iconSize = 22.dp,
                                    onClick = dismissSearchPanel,
                                )
                            } else {
                                Icon(
                                    imageVector = SimpIcons.Search,
                                    contentDescription = "Search",
                                )
                            }
                        }
                    }

                    BasicTextField(
                        value = searchText,
                        onValueChange = { newText ->
                            searchText = newText
                            searchViewModel.logSearchEvent("Search input changed (length=${newText.length})")
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { isFocused = it.isFocused },
                        singleLine = true,
                        textStyle = typo().bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions =
                            KeyboardActions(
                                onSearch = {
                                    val query = searchText.trim()
                                    val deepLink = query.toAppDeepLinkOrNull()
                                    if (deepLink != null) {
                                        focusManager.clearFocus()
                                        sharedViewModel.setIntent(GenericIntent(data = deepLink))
                                    } else if (query.isNotEmpty()) {
                                        isSearchSubmitted = true
                                        focusManager.clearFocus()
                                        searchViewModel.insertSearchHistory(query)
                                        when (searchScreenState.searchType) {
                                            SearchType.ALL -> searchViewModel.searchAll(query)
                                            SearchType.SONGS -> searchViewModel.searchSongs(query)
                                            SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                            SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                            SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                            SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                            SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                            SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                                        }
                                    }
                                },
                            ),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchText.isEmpty()) {
                                    AnimatedContent(
                                        targetState = currentPlaceholderIndex,
                                        transitionSpec = {
                                            (
                                                fadeIn(animationSpec = tween(500)) +
                                                    slideInVertically { height -> height }
                                            ).togetherWith(
                                                fadeOut(animationSpec = tween(500)) +
                                                    slideOutVertically { height -> -height },
                                            )
                                        },
                                        label = "placeholder_animation",
                                    ) { index ->
                                        Text(
                                            text = placeholderTexts[index],
                                            style = typo().labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                                innerTextField()
                            }
                        },
                    )

                    if (searchText.isNotEmpty()) {
                        GlassDockIconButton(
                            imageVector = SimpIcons.Close,
                            contentDescription = "Clear search",
                            modifier = Modifier.size(40.dp),
                            onClick = {
                                searchText = ""
                                isSearchSubmitted = false
                            },
                        )
                    } else if (getPlatform() == Platform.Android) {
                        GlassDockIconButton(
                            imageVector = SimpIcons.GraphicEq,
                            contentDescription = stringResource(Res.string.recognize_song),
                            modifier = Modifier.size(40.dp),
                            onClick = { showRecognition = true },
                        )
                    }
                    GlassDockIconButton(
                        imageVector = SimpIcons.Error,
                        contentDescription = "Open app logs",
                        modifier = Modifier.size(40.dp),
                        iconSize = 20.dp,
                        onClick = { navController.navigate(AppLogDestination) },
                    )
                }

                AnimatedVisibility(
                    visible = isKeyboardVisible || searchUIType == SearchUIType.SEARCH_RESULTS,
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(chipRowState)
                                .padding(top = 6.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SearchType.entries.forEach { id ->
                            val isSelected = id == searchScreenState.searchType
                            Spacer(modifier = Modifier.width(4.dp))
                            Chip(
                                isAnimated = uiState is SearchScreenUIState.Loading,
                                isSelected = isSelected,
                                text = stringResource(id.toStringRes()),
                                backdrop = chipBackdrop,
                            ) {
                                searchViewModel.setSearchType(id)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestItemRow(
    searchResult: SearchResultType,
    onItemClick: (SearchResultType) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(searchResult) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url =
            when (searchResult) {
                is SongsResult -> {
                    searchResult.thumbnails?.lastOrNull()?.url
                }

                is AlbumsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is ArtistsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is PlaylistsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is VideosResult -> {
                    searchResult.thumbnails?.lastOrNull()?.url
                }

                else -> {
                    null
                }
            }

        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(url)
                        .crossfade(true)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(
                            if (searchResult is ArtistsResult) {
                                CircleShape
                            } else {
                                ArtworkShape
                            },
                        ),
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val title =
                when (searchResult) {
                    is SongsResult -> {
                        searchResult.title
                    }

                    is AlbumsResult -> {
                        searchResult.title
                    }

                    is ArtistsResult -> {
                        searchResult.artist
                    }

                    is PlaylistsResult -> {
                        searchResult.title
                    }

                    is VideosResult -> {
                        searchResult.title
                    }

                    else -> {
                        null
                    }
                } ?: "Unknown"

            Text(
                text = title,
                style = typo().labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))

            val subtitle =
                when (searchResult) {
                    is SongsResult -> searchResult.artists?.map { it.name }?.connectArtists()
                    is AlbumsResult -> searchResult.artists.map { it.name }.connectArtists()
                    is PlaylistsResult -> searchResult.author.ifEmpty { "YouTube Music" }
                    is ArtistsResult -> stringResource(Res.string.artists)
                    is VideosResult -> searchResult.artists?.map { it.name }?.connectArtists()
                    else -> null
                } ?: "Unknown"

            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

enum class SearchUIType {
    EMPTY,
    SEARCH_HISTORY,
    SEARCH_SUGGESTIONS,
    SEARCH_RESULTS,
}
