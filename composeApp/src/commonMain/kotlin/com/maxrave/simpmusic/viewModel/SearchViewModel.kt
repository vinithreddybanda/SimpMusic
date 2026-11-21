package com.maxrave.simpmusic.viewModel

import androidx.lifecycle.viewModelScope
import com.maxrave.common.SELECTED_LANGUAGE
import com.maxrave.domain.data.entities.SearchHistory
import com.maxrave.domain.data.model.searchResult.albums.AlbumsResult
import com.maxrave.domain.data.model.searchResult.artists.ArtistsResult
import com.maxrave.domain.data.model.searchResult.playlists.PlaylistsResult
import com.maxrave.domain.data.model.searchResult.songs.SongsResult
import com.maxrave.domain.data.model.mood.Mood
import com.maxrave.domain.data.model.searchResult.videos.VideosResult
import com.maxrave.domain.data.type.SearchResultType
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.domain.repository.HomeRepository
import com.maxrave.domain.repository.SearchRepository
import com.maxrave.domain.utils.Resource
import com.maxrave.domain.utils.toQueryList
import com.maxrave.logger.LogLevel
import com.maxrave.logger.Logger
import com.maxrave.simpmusic.viewModel.base.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.albums
import simpmusic.composeapp.generated.resources.all
import simpmusic.composeapp.generated.resources.artists
import simpmusic.composeapp.generated.resources.featured_playlists
import simpmusic.composeapp.generated.resources.playlists
import simpmusic.composeapp.generated.resources.podcasts
import simpmusic.composeapp.generated.resources.songs
import simpmusic.composeapp.generated.resources.videos

// State cho tìm kiếm
data class SearchScreenState(
    val searchType: SearchType = SearchType.ALL,
    val searchAllResult: List<SearchResultType> = emptyList(),
    val searchSongsResult: List<SongsResult> = emptyList(),
    val searchVideosResult: List<VideosResult> = emptyList(),
    val searchAlbumsResult: List<AlbumsResult> = emptyList(),
    val searchArtistsResult: List<ArtistsResult> = emptyList(),
    val searchPlaylistsResult: List<PlaylistsResult> = emptyList(),
    val searchFeaturedPlaylistsResult: List<PlaylistsResult> = emptyList(),
    val searchPodcastsResult: List<PlaylistsResult> = emptyList(),
    val suggestQueries: List<String> = emptyList(),
    val suggestYTItems: List<SearchResultType> = emptyList(),
)

// Loại tìm kiếm
enum class SearchType {
    ALL,
    SONGS,
    VIDEOS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS,
    FEATURED_PLAYLISTS,
    PODCASTS,
}

fun SearchType.toStringRes(): StringResource =
    when (this) {
        SearchType.ALL -> Res.string.all
        SearchType.SONGS -> Res.string.songs
        SearchType.VIDEOS -> Res.string.videos
        SearchType.ALBUMS -> Res.string.albums
        SearchType.ARTISTS -> Res.string.artists
        SearchType.PLAYLISTS -> Res.string.playlists
        SearchType.FEATURED_PLAYLISTS -> Res.string.featured_playlists
        SearchType.PODCASTS -> Res.string.podcasts
    }

// UI state cho tìm kiếm
sealed class SearchScreenUIState {
    object Empty : SearchScreenUIState()

    object Loading : SearchScreenUIState()

    object Success : SearchScreenUIState()

    object Error : SearchScreenUIState()
}

class SearchViewModel(
    private val dataStoreManager: DataStoreManager,
    private val searchRepository: SearchRepository,
    private val homeRepository: HomeRepository,
) : BaseViewModel() {
    private val _searchScreenUIState = MutableStateFlow<SearchScreenUIState>(SearchScreenUIState.Empty)
    val searchScreenUIState: StateFlow<SearchScreenUIState> get() = _searchScreenUIState.asStateFlow()

    private val _searchScreenState = MutableStateFlow(SearchScreenState())
    val searchScreenState: StateFlow<SearchScreenState> get() = _searchScreenState.asStateFlow()

    private val _searchHistory: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val searchHistory: StateFlow<List<String>> get() = _searchHistory.asStateFlow()

    /** Browse categories shown on the empty search screen, the way YouTube Music itself does. */
    private val _moodAndGenres: MutableStateFlow<Mood?> = MutableStateFlow(null)
    val moodAndGenres: StateFlow<Mood?> get() = _moodAndGenres.asStateFlow()

    /** Cover art per category params, filled in as tiles scroll into view. */
    private val _moodArtwork: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    val moodArtwork: StateFlow<Map<String, String>> get() = _moodArtwork.asStateFlow()

    private val requestedArtwork = mutableSetOf<String>()

    // Keep the active query and full-search job so changing a filter cannot show stale results.
    private var activeSearchJob: Job? = null

    // Suggestions are requested for every non-empty edit. Keep only the latest request alive.
    private var suggestQueryJob: Job? = null
    private var suggestQueryGeneration: Long = 0L

    var regionCode: String? = null
    var language: String? = null

    init {
        regionCode = runBlocking { dataStoreManager.location.first() }
        language = runBlocking { dataStoreManager.getString(SELECTED_LANGUAGE).first() }
        getSearchHistory()
        getMoodAndGenres()
    }

    /**
     * Fetched once for the app's lifetime — this ViewModel is a Koin `single`, and the category
     * list changes about as often as YouTube ships a new mood, so re-fetching on every visit to
     * the search tab would be pure waste.
     */
    private fun getMoodAndGenres() {
        if (_moodAndGenres.value != null) return
        viewModelScope.launch {
            homeRepository.getMoodAndMomentsData().collect { resource ->
                if (resource is Resource.Success) {
                    _moodAndGenres.value = resource.data
                }
            }
        }
    }

    /**
     * Resolve the cover for one category. Called from the tile itself, so only categories the user
     * actually scrolls to cost a request — [requestedArtwork] keeps a tile scrolling in and out of
     * view from firing it again.
     */
    fun loadMoodArtwork(params: String) {
        if (!requestedArtwork.add(params)) return
        viewModelScope.launch {
            homeRepository.getMoodCategoryArtwork(params).collect { url ->
                if (url != null) {
                    _moodArtwork.update { it + (params to url) }
                }
            }
        }
    }

    private fun getSearchHistory() {
        viewModelScope.launch {
            searchRepository.getSearchHistory().collect { values ->
                if (values.isNotEmpty()) {
                    values.toQueryList().reversed().let { list ->
                        _searchHistory.value = list
                        log("Search history updated: $list")
                    }
                } else {
                    _searchHistory.value = emptyList()
                    log("Search history is empty")
                }
            }
        }
    }

    fun insertSearchHistory(query: String) {
        viewModelScope.launch {
            searchRepository.insertSearchHistory(SearchHistory(query = query)).collectLatest {
                Logger.d(tag, "Inserted search history: $query, $it")
                getSearchHistory()
            }
        }
    }

    fun deleteSearchHistory() {
        viewModelScope.launch {
            searchRepository.deleteSearchHistory()
            delay(1000)
            getSearchHistory()
        }
    }

    private fun prepareSearch(query: String, type: SearchType) {
        activeSearchJob?.cancel()
        _searchScreenUIState.value = SearchScreenUIState.Loading
        _searchScreenState.update { state ->
            state.copy(
                searchType = type,
                searchAllResult = emptyList(),
                searchSongsResult = emptyList(),
                searchVideosResult = emptyList(),
                searchAlbumsResult = emptyList(),
                searchArtistsResult = emptyList(),
                searchPlaylistsResult = emptyList(),
                searchFeaturedPlaylistsResult = emptyList(),
                searchPodcastsResult = emptyList(),
            )
        }
    }

    private suspend fun <T> collectSearchResults(
        request: () -> Flow<Resource<T>>,
        onSuccess: (T?) -> Unit,
    ) {
        var receivedEmission = false
        try {
            request().collect { resource ->
                receivedEmission = true
                when (resource) {
                    is Resource.Success -> {
                        onSuccess(resource.data)
                        _searchScreenUIState.value = SearchScreenUIState.Success
                    }
                    is Resource.Error -> {
                        log("Search repository error: ${resource.message}", LogLevel.ERROR)
                        _searchScreenUIState.value = SearchScreenUIState.Error
                    }
                }
            }
            if (!receivedEmission) {
                log("Search repository completed without a result emission", LogLevel.ERROR)
                _searchScreenUIState.value = SearchScreenUIState.Error
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            logSearchFailure("full search request", exception)
            _searchScreenUIState.value = SearchScreenUIState.Error
        }
    }

    fun searchSongs(query: String) {
        prepareSearch(query, SearchType.SONGS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataSong(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchSongsResult = results)
                    }
                }
            }
        }
    }

    fun searchAll(query: String) {
        prepareSearch(query, SearchType.ALL)
        activeSearchJob =
            viewModelScope.launch {
                var song = ArrayList<SongsResult>()
                val video = ArrayList<VideosResult>()
                var album = ArrayList<AlbumsResult>()
                var artist = ArrayList<ArtistsResult>()
                var playlist = ArrayList<PlaylistsResult>()
                var featuredPlaylist = ArrayList<PlaylistsResult>()
                var podcast = ArrayList<PlaylistsResult>()

                var songSucceeded = false
                var artistSucceeded = false
                var albumSucceeded = false
                var playlistSucceeded = false
                var videoSucceeded = false
                var featuredPlaylistSucceeded = false
                var podcastSucceeded = false

                // Keep the exact seven concurrent repository calls and ordering used upstream.
                // Each collector catches its own request/parsing failure so sibling categories finish.
                val job1 =
                    launch {
                        songSucceeded =
                            collectSearchCategory("songs", { searchRepository.getSearchDataSong(query) }) {
                                it?.let { values -> song = values }
                            }
                    }
                val job2 =
                    launch {
                        artistSucceeded =
                            collectSearchCategory("artists", { searchRepository.getSearchDataArtist(query) }) {
                                it?.let { values -> artist = values }
                            }
                    }
                val job3 =
                    launch {
                        albumSucceeded =
                            collectSearchCategory("albums", { searchRepository.getSearchDataAlbum(query) }) {
                                it?.let { values -> album = values }
                            }
                    }
                val job4 =
                    launch {
                        playlistSucceeded =
                            collectSearchCategory("playlists", { searchRepository.getSearchDataPlaylist(query) }) {
                                it?.let { values -> playlist = values }
                            }
                    }
                val job5 =
                    launch {
                        videoSucceeded =
                            collectSearchCategory("videos", { searchRepository.getSearchDataVideo(query) }) {
                                it?.let { values -> video.addAll(values) }
                            }
                    }
                val job6 =
                    launch {
                        featuredPlaylistSucceeded =
                            collectSearchCategory(
                                "featured playlists",
                                { searchRepository.getSearchDataFeaturedPlaylist(query) },
                            ) {
                                it?.let { values -> featuredPlaylist = values }
                            }
                    }
                val job7 =
                    launch {
                        podcastSucceeded =
                            collectSearchCategory("podcasts", { searchRepository.getSearchDataPodcast(query) }) {
                                it?.let { values -> podcast = values }
                            }
                    }

                job1.join()
                job2.join()
                job3.join()
                job4.join()
                job5.join()
                job6.join()
                job7.join()

                val successfulCategories =
                    listOf(
                        songSucceeded,
                        artistSucceeded,
                        albumSucceeded,
                        playlistSucceeded,
                        videoSucceeded,
                        featuredPlaylistSucceeded,
                        podcastSucceeded,
                    ).count { it }

                if (successfulCategories == 0) {
                    log("Search All failed: all seven category requests failed or returned no success", LogLevel.ERROR)
                    _searchScreenUIState.value = SearchScreenUIState.Error
                    return@launch
                }

                val combinedResults = ArrayList<SearchResultType>()
                if (artist.size >= 3) {
                    for (i in 0..2) {
                        combinedResults += artist[i]
                    }
                    combinedResults.addAll(song)
                    combinedResults.addAll(video)
                    combinedResults.addAll(album)
                    combinedResults.addAll(playlist)
                    combinedResults.addAll(featuredPlaylist)
                    combinedResults.addAll(podcast)
                } else {
                    combinedResults.addAll(artist)
                    combinedResults.addAll(song)
                    combinedResults.addAll(video)
                    combinedResults.addAll(album)
                    combinedResults.addAll(playlist)
                    combinedResults.addAll(featuredPlaylist)
                    combinedResults.addAll(podcast)
                }

                _searchScreenState.update { state ->
                    state.copy(
                        searchType = SearchType.ALL,
                        searchAllResult = combinedResults,
                        searchSongsResult = song,
                        searchArtistsResult = artist,
                        searchAlbumsResult = album,
                        searchPlaylistsResult = playlist,
                        searchVideosResult = video,
                        searchFeaturedPlaylistsResult = featuredPlaylist,
                        searchPodcastsResult = podcast,
                    )
                }
                _searchScreenUIState.value = SearchScreenUIState.Success
                log("Search All completed: successful categories=$successfulCategories/7, results=${combinedResults.size}")
            }
    }

    private suspend fun <T> collectSearchCategory(
        category: String,
        request: () -> Flow<Resource<T>>,
        onSuccess: (T?) -> Unit,
    ): Boolean {
        var receivedSuccess = false
        try {
            request().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        receivedSuccess = true
                        onSuccess(resource.data)
                    }
                    is Resource.Error -> {
                        log("Search category '$category' returned an error: ${resource.message}", LogLevel.ERROR)
                    }
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            logSearchFailure("$category search retrieval", exception)
        }
        return receivedSuccess
    }


    fun logSearchEvent(message: String) {
        // Persist search/IME diagnostics in App Logs, not only transient Logcat output.
        Logger.i(tag, message)
    }

    fun logSearchFailure(stage: String, throwable: Throwable) {
        log(
            "Search failure [$stage]: ${throwable.message}\n${throwable.stackTraceToString()}",
            LogLevel.ERROR,
        )
    }

    fun suggestQuery(query: String) {
        suggestQueryJob?.cancel()
        val requestGeneration = ++suggestQueryGeneration

        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            _searchScreenState.update { state ->
                state.copy(suggestQueries = emptyList(), suggestYTItems = emptyList())
            }
            return
        }

        // Remove old results immediately; only the newest request may publish results.
        _searchScreenState.update { state ->
            state.copy(suggestQueries = emptyList(), suggestYTItems = emptyList())
        }

        Logger.i(tag, "Search suggestions queued (queryLength=${normalizedQuery.length}, generation=$requestGeneration)")
        suggestQueryJob =
            viewModelScope.launch {
                try {
                    Logger.i(tag, "Search suggestions request started (generation=$requestGeneration)")
                    searchRepository.getSuggestQuery(normalizedQuery).collectLatest { values ->
                        if (requestGeneration != suggestQueryGeneration) return@collectLatest

                        when (values) {
                            is Resource.Success -> {
                                val suggestions = values.data
                                Logger.i(
                                    tag,
                                    "Search suggestions received (queries=${suggestions?.queries?.size ?: 0}, items=${suggestions?.recommendedItems?.size ?: 0}, generation=$requestGeneration)",
                                )
                                _searchScreenState.update { state ->
                                    if (requestGeneration == suggestQueryGeneration) {
                                        state.copy(
                                            suggestQueries = suggestions?.queries.orEmpty(),
                                            suggestYTItems = suggestions?.recommendedItems.orEmpty(),
                                        )
                                    } else {
                                        state
                                    }
                                }
                            }

                            is Resource.Error -> {
                                log("Search suggestions failed: ${values.message}", LogLevel.ERROR)
                                _searchScreenState.update { state ->
                                    if (requestGeneration == suggestQueryGeneration) {
                                        state.copy(suggestQueries = emptyList(), suggestYTItems = emptyList())
                                    } else {
                                        state
                                    }
                                }
                            }
                        }
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (exception: Exception) {
                    if (requestGeneration == suggestQueryGeneration) {
                        logSearchFailure("suggestion request", exception)
                        _searchScreenState.update { state ->
                            state.copy(suggestQueries = emptyList(), suggestYTItems = emptyList())
                        }
                    }
                }
            }
    }

    fun searchAlbums(query: String) {
        prepareSearch(query, SearchType.ALBUMS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataAlbum(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchAlbumsResult = results)
                    }
                }
            }
        }
    }

    fun searchFeaturedPlaylist(query: String) {
        prepareSearch(query, SearchType.FEATURED_PLAYLISTS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataFeaturedPlaylist(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchFeaturedPlaylistsResult = results)
                    }
                }
            }
        }
    }

    fun searchPodcast(query: String) {
        prepareSearch(query, SearchType.PODCASTS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataPodcast(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchPodcastsResult = results)
                    }
                }
            }
        }
    }

    fun searchArtists(query: String) {
        prepareSearch(query, SearchType.ARTISTS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataArtist(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchArtistsResult = results)
                    }
                }
            }
        }
    }

    fun searchPlaylists(query: String) {
        prepareSearch(query, SearchType.PLAYLISTS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataPlaylist(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchPlaylistsResult = results)
                    }
                }
            }
        }
    }

    fun searchVideos(query: String) {
        prepareSearch(query, SearchType.VIDEOS)
        activeSearchJob = viewModelScope.launch {
            collectSearchResults({ searchRepository.getSearchDataVideo(query) }) { results ->
                if (results != null) {
                    _searchScreenState.update { state ->
                        state.copy(searchVideosResult = results)
                    }
                }
            }
        }
    }

    fun setSearchType(
        searchType: SearchType,
        @Suppress("UNUSED_PARAMETER") requery: Boolean = true,
    ) {
        // Upstream only switches the selected category. Search All already fetched every
        // category, so changing a filter must display cached results, not launch duplicate requests.
        _searchScreenState.update { state ->
            state.copy(searchType = searchType)
        }
}