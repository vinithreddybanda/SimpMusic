package com.maxrave.simpmusic.ui.component

import androidx.compose.material3.Icon
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.fill.Books
import com.adamglin.phosphoricons.fill.ChartLineUp
import com.adamglin.phosphoricons.fill.House
import com.adamglin.phosphoricons.fill.MagnifyingGlass
import com.adamglin.phosphoricons.fill.Waveform
import com.adamglin.phosphoricons.regular.Books as RegularBooks
import com.adamglin.phosphoricons.regular.ChartLineUp as RegularChartLineUp
import com.adamglin.phosphoricons.regular.House as RegularHouse
import com.adamglin.phosphoricons.regular.MagnifyingGlass as RegularMagnifyingGlass
import com.adamglin.phosphoricons.regular.Waveform as RegularWaveform
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop
import com.maxrave.simpmusic.ui.navigation.destination.home.AnalyticsDestination
import com.maxrave.simpmusic.ui.navigation.destination.home.HomeDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.LibraryDestination
import com.maxrave.simpmusic.ui.navigation.destination.library.MixForYouDestination
import com.maxrave.simpmusic.ui.navigation.destination.search.SearchDestination
import com.maxrave.simpmusic.viewModel.SharedViewModel
import org.jetbrains.compose.resources.StringResource
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.analytics
import simpmusic.composeapp.generated.resources.home
import simpmusic.composeapp.generated.resources.library
import simpmusic.composeapp.generated.resources.mix
import simpmusic.composeapp.generated.resources.search
import kotlin.reflect.KClass

@Composable
expect fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any = HomeDestination,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean = false,
    showAnalyticsTab: Boolean = false,
    showMixForYouTab: Boolean = false,
    onOpenNowPlaying: () -> Unit = {},
    // False draws the same folding bar in the flat material (liquid glass turned off).
    liquidGlass: Boolean = true,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
)

sealed class BottomNavScreen(
    val ordinal: Int,
    val destination: Any,
    val title: StringResource,
    val icon: @Composable (selected: Boolean) -> Unit,
) {
    data object Home : BottomNavScreen(
        ordinal = 0,
        destination = HomeDestination,
        title = Res.string.home,
        icon = { selected ->
            Icon(
                if (selected) PhosphorIcons.Fill.House else PhosphorIcons.Regular.RegularHouse,
                contentDescription = null,
            )
        },
    )

    data object Search : BottomNavScreen(
        ordinal = 1,
        destination = SearchDestination,
        title = Res.string.search,
        icon = { selected ->
            Icon(
                if (selected) PhosphorIcons.Fill.MagnifyingGlass else PhosphorIcons.Regular.RegularMagnifyingGlass,
                contentDescription = null,
            )
        },
    )

    data object Library : BottomNavScreen(
        ordinal = 2,
        destination = LibraryDestination,
        title = Res.string.library,
        icon = { selected ->
            Icon(
                imageVector = if (selected) PhosphorIcons.Fill.Books else PhosphorIcons.Regular.RegularBooks,
                contentDescription = null,
            )
        },
    )

    // Only shown when local tracking is enabled.
    data object Analytics : BottomNavScreen(
        ordinal = 3,
        destination = AnalyticsDestination,
        title = Res.string.analytics,
        icon = { selected ->
            Icon(
                imageVector = if (selected) PhosphorIcons.Fill.ChartLineUp else PhosphorIcons.Regular.RegularChartLineUp,
                contentDescription = null,
            )
        },
    )

    // Only shown while signed in to YouTube — an anonymous session gets no mixes.
    // Labelled "Mix", not "Mix for you": the full title is the widest label in the bar and forces
    // every tab to be that wide. The screen itself still uses the full title.
    data object MixForYou : BottomNavScreen(
        ordinal = 4,
        destination = MixForYouDestination,
        title = Res.string.mix,
        icon = { selected ->
            Icon(
                imageVector = if (selected) PhosphorIcons.Fill.Waveform else PhosphorIcons.Regular.RegularWaveform,
                contentDescription = null,
            )
        },
    )
}