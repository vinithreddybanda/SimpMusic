# Liquid Glass UI/UX Migration Checklist

Branch: `dev`
Reference implementation: existing floating liquid-glass navigation dock.
Scope: app-wide Android/KMP Compose UI. Preserve playback, navigation, search behavior, data flows, accessibility, and performance.

## Non-negotiable design constraints

- Keep the existing navigation dock's dimensions, shape, material, blur/refraction, animation, gestures, and placement unchanged. It is the reference, not a redesign target.
- Glass-only visual system. Do not reintroduce Material, SoundCloud, or alternate theme selectors.
- Do not replace functional content with decorative glass cards. Use glass for controls, navigation, transient surfaces, and selected containers; keep text and artwork legible.
- Preserve all existing routes and features. No changes to playback/search algorithms as part of visual migration.
- No fake Dynamic Island overlays or unnecessary overlay permissions.
- Reuse the shared liquid-glass primitives and backdrop sources. Never put a glass surface inside the backdrop source it samples; that can create a RuntimeShader feedback loop.
- Keep motion short, interruptible, and performant. Respect reduced-motion/accessibility settings and keyboard/insets.
- Every screen must cover loading, empty, error, selected, pressed, disabled, and scrolled states, not just its default screenshot.

## Screen inventory

Status labels:
- [x] Existing glass treatment is present in at least part of this screen; still requires consistency review.
- [ ] Needs full visual audit and glass migration where appropriate.

### Primary navigation

- [x] Home — `ui/screen/home/HomeScreen.kt` (glass backdrop/chip source exists; audit section cards, headers, filters, and scroll states).
- [x] Library — `ui/screen/library/LibraryScreen.kt` (glass backdrop source exists; audit tabs, filters, selection, and content surfaces).
- [x] Search — `ui/screen/other/SearchScreen.kt` (glass chips/surfaces exist; audit search field, suggestions, results, keyboard transition, and backdrop layering).
- [x] Navigation dock — `ui/component/LiquidGlassAppBottomNavigationBar.kt` (LOCKED: do not change its visual design or behavior).
- [ ] More/overflow destination and all secondary navigation entries (trace actual route destinations; keep dock unchanged).

### Home and discovery

- [ ] Mood discovery — `ui/screen/home/MoodScreen.kt`; mood tiles, category chips, detail/selection states.
- [ ] Browse/discover — `ui/screen/other/BrowseScreen.kt`; category controls, shelves, filters, loading/empty states.
- [ ] Mix for You — `ui/screen/library/MixForYouScreen.kt`; mix tiles, actions, refresh states.
- [ ] Dynamic playlists — `ui/screen/library/LibraryDynamicPlaylistScreen.kt`.
- [ ] Library Wrapped tab — `ui/screen/library/LibraryWrappedTab.kt`.
- [ ] Home audio effects — `ui/screen/home/AudioEffectsSection.kt`.
- [ ] Equalizer — `ui/screen/home/EqualizerSection.kt`, `EqualizerPresets.kt`, `AutoEqPicker.kt`.
- [ ] Home notifications — `ui/screen/home/NotificationScreen.kt`.

### Music and media detail

- [x] Album detail — `ui/screen/other/AlbumScreen.kt` (glass artwork/header controls exist; migrate remaining controls/sheets and verify scroll states).
- [x] Artist detail — `ui/screen/other/ArtistScreen.kt` (glass artwork/header controls exist; migrate remaining cards/actions).
- [x] Playlist detail — `ui/screen/other/PlaylistScreen.kt` (glass artwork/header controls exist; migrate remaining cards/actions).
- [ ] More albums — `ui/screen/other/MoreAlbumsScreen.kt`.
- [ ] Podcasts — `ui/screen/other/PodcastsScreen.kt`; podcast/episode rows and playback actions.
- [ ] Local playlist detail — `ui/screen/library/LocalPlaylistScreen.kt` (some glass header controls exist; audit remaining content, menus, dialogs).
- [ ] Full-screen player — `ui/screen/player/FullscreenPlayer.kt`.
- [ ] Now Playing — `ui/screen/player/NowPlayingScreen.kt`; transport controls, seekbar, queue, lyrics, output/device sheet, overflow.
- [ ] Player content variants — `ui/screen/player/content/NowPlayingContentAppleMusic.kt`, `NowPlayingContentM3Expressive.kt`, `NowPlayingContentSpotify.kt`, `NowPlayingContentState.kt`, `NowPlayingExpressiveCards.kt`. Remove/retire alternate theme UI where still reachable; keep one glass-only player experience.
- [ ] Apple Music player subcomponents — `ui/screen/player/content/applemusic/AppleMusicOutputSheet.kt`, `AppleMusicQueueView.kt`, `AppleMusicLyricsView.kt`, `AppleMusicLyricStrip.kt`, shared player components.
- [ ] Queue and lyrics interactions, including fullscreen lyrics, lyrics offset controls, translation/selection, and share flows.

### Library, selection, and creation

- [ ] Local playlists — `ui/screen/library/LocalPlaylistScreen.kt` and create/edit flows.
- [ ] Selected songs toolbar/sheet — `ui/component/selection/SelectedSongsTopAppBar.kt`, `SelectedSongsBottomSheet.kt`.
- [ ] Create sheet — `ui/component/CreateSheet.kt`.
- [ ] Add-to-playlist modal and other playlist-management sheets.
- [ ] Drag/reorder, multi-select, contextual action menus, and confirmation states.

### Social, account, and utility screens

- [ ] Listen Together — `ui/screen/home/ListenTogetherScreen.kt`; room, member actions, error cards, dialogs.
- [ ] Listen Together settings — `ui/screen/home/ListenTogetherSettingsScreen.kt`.
- [ ] Settings — `ui/screen/home/SettingScreen.kt`; all rows, switches, dropdowns, nested pages, import/export, account/sync sheets and dialogs.
- [ ] Analytics — `ui/screen/home/analytics/AnalyticsScreen.kt`, charts and listening receipt.
- [ ] Wrapped — `ui/screen/home/wrapped/WrappedScreen.kt`, `WrappedNotEnoughDataScreen.kt`, all Wrapped cards and share states.
- [ ] Login — `ui/screen/login/LogInScreen.kt`.
- [ ] Spotify login — `ui/screen/login/SpotifyLoginScreen.kt`.
- [ ] Last.fm login — `ui/screen/login/LastfmLoginScreen.kt`.
- [ ] Discord login/OAuth — `ui/screen/login/DiscordLoginScreen.kt`, `DiscordOAuthLoginScreen.kt`.
- [ ] App logs — `ui/screen/other/AppLogScreen.kt`, `AppLogFiles.kt`.
- [ ] Credits — `ui/screen/other/CreditScreen.kt`.
- [ ] Unofficial build information — `ui/screen/other/UnofficialBuildScreen.kt`.

## Shared component migration

These shared components affect multiple screens and should be standardized before or alongside page-level work:

- [ ] App bars and collapsing headers — `NormalAppBar.kt`, `CollapsingToolbar.kt`, desktop sidebar where applicable.
- [ ] Mini-player — `ui/screen/MiniPlayer.kt`; align its glass language with the dock without changing dock geometry.
- [ ] Buttons and icon buttons — `RippleIconButton.kt`, `DropdownButton.kt`, player controls.
- [ ] Chips, tabs, segmented filters — `ChipGroup.kt`, tab/filter implementations.
- [ ] Cards and list rows — `AdapterItems.kt`, `FullWidthItems.kt`, `LibraryItem.kt`, `GridLibraryPlaylist.kt`, `LibraryTilingBox.kt`, `MoodCategoryCard.kt`, `PodcastEpisodeItem.kt`, `WrappedEntryCard.kt`.
- [ ] Bottom sheets — `ModalBottomSheet.kt`, `AddLyricsSheet.kt`, `CommentsBottomSheet.kt`, `ShareImageSheet.kt`, `lyrics/ShareLyricsSheet.kt`, `taste/ShareTasteSheet.kt`, selection and create sheets.
- [ ] Dialogs and popups — `LoadingDialog.kt`, `ReviewDialog.kt`, `ImageCropperDialog.kt`, `ShareSavedLyricsDialog.kt`, `VoteLyricsDialog.kt`, `FootgunsStarDialog.kt`, `BlogPromoDialog.kt` if still reachable, plus inline AlertDialogs/BasicAlertDialogs.
- [ ] Menus, tooltips, dropdowns, overflow and contextual actions.
- [ ] Loading, shimmer, empty, offline, and error states — `CenterLoadingBox.kt`, `Shimmer.kt`, `OfflineErrorState.kt`, `EndOfPage.kt`.
- [ ] Badges, playback indicators, lyrics controls, queue items, and selection states.
- [ ] Share/export cards and image-capture sheets.
- [ ] Desktop sidebar and adaptive layouts, ensuring mobile dock is not altered.

## Cross-app interaction and polish checklist

- [ ] Status bar, navigation bar, edge-to-edge, display cutout and gesture insets.
- [ ] Keyboard appearance: search field and suggestions docked at the correct measured IME top; no overlap with mini-player or navigation dock.
- [ ] Scroll behavior: compact/fading headers, dock/mini-player coexistence, backdrop source bounds, no abrupt opaque header blocks.
- [ ] Back navigation, predictive back, sheet dismissal, nested navigation and restoration.
- [ ] Touch feedback, press/hold, swipe gestures, drag/reorder, haptics and animation interruption.
- [ ] Dynamic colors/artwork sampling and readable contrast in both light and dark glass contexts.
- [ ] Accessibility: content descriptions, semantics, focus order, touch targets, font scaling and reduced motion.
- [ ] Performance: avoid nested/overlapping backdrop captures, excess blur layers, per-item shaders, recomposition churn and unbounded offscreen rendering.
- [ ] Verify all screens on small and large phones, landscape/tablet and desktop targets where supported.

## Migration order

1. Lock and regression-test the dock; document its exact behavior as the visual reference.
2. Standardize reusable glass primitives, app bars, buttons, chips, cards, sheets, dialogs, and menus.
3. Convert primary navigation screens: Home, Library, Search, Browse, More.
4. Convert media detail screens and the player/queue/lyrics surfaces.
5. Convert settings, account/login, social, analytics, Wrapped, and utility screens.
6. Run route-by-route visual and interaction regression tests; verify performance and build before marking any item complete.

## Completion rule

A screen is complete only after its main content, nested surfaces, menus, sheets, dialogs, loading/empty/error states, selected/pressed states, keyboard/inset behavior, and scrolled states have been reviewed. A glass header alone does not count as a converted screen.
