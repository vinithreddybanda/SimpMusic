package com.maxrave.simpmusic.ui.icon

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.fill.*
import com.adamglin.phosphoricons.regular.*

/**
 * Compatibility receiver for the existing icon call sites. Every legacy Material Symbols entry
 * point is resolved to its Phosphor equivalent in [ImageVector.Builder.buildSimpIcon].
 */
object SimpIcons {}

internal fun ImageVector.Builder.buildSimpIcon(): ImageVector {
    val legacy = build()
    return when (legacy.name) {
        "AccessAlarm" -> PhosphorIcons.Regular.Alarm
        "Add" -> PhosphorIcons.Regular.Plus
        "AddCircleOutline" -> PhosphorIcons.Regular.PlusCircle
        "AddPhotoAlternate" -> PhosphorIcons.Regular.Image
        "Album" -> PhosphorIcons.Regular.Disc
        "ArrowBackIosNew" -> PhosphorIcons.Regular.CaretLeft
        "ArrowForwardIos" -> PhosphorIcons.Regular.CaretRight
        "ArrowOutward" -> PhosphorIcons.Regular.ArrowUpRight
        "AutoGraph" -> PhosphorIcons.Regular.ChartLineUp
        "AvTimer" -> PhosphorIcons.Regular.Timer
        "CalendarToday" -> PhosphorIcons.Regular.CalendarBlank
        "Check" -> PhosphorIcons.Regular.Check
        "CheckCircle" -> PhosphorIcons.Regular.CheckCircle
        "Close" -> PhosphorIcons.Regular.X
        "CloudOff" -> PhosphorIcons.Regular.CloudSlash
        "CommentOutline" -> PhosphorIcons.Regular.ChatCircle
        "ContentCopy" -> PhosphorIcons.Regular.Copy
        "Cookie" -> PhosphorIcons.Regular.Cookie
        "Delete" -> PhosphorIcons.Regular.Trash
        "Done" -> PhosphorIcons.Regular.Check
        "Download" -> PhosphorIcons.Regular.Download
        "DownloadForOffline" -> PhosphorIcons.Regular.DownloadSimple
        "DownloadForOfflineOutlined" -> PhosphorIcons.Regular.DownloadSimple
        "Downloading" -> PhosphorIcons.Regular.DownloadSimple
        "DragHandle" -> PhosphorIcons.Regular.DotsSixVertical
        "Edit" -> PhosphorIcons.Regular.PencilSimple
        "Error" -> PhosphorIcons.Regular.WarningCircle
        "Explicit" -> PhosphorIcons.Regular.TextT
        "FastForward" -> PhosphorIcons.Regular.FastForward
        "FastRewind" -> PhosphorIcons.Regular.Rewind
        "Favorite" -> PhosphorIcons.Fill.Heart
        "FavoriteBorder" -> PhosphorIcons.Regular.Heart
        "FormatListBulleted" -> PhosphorIcons.Regular.ListBullets
        "Forward5" -> PhosphorIcons.Regular.ArrowClockwise
        "Fullscreen" -> PhosphorIcons.Regular.ArrowsOut
        "FullscreenExit" -> PhosphorIcons.Regular.ArrowsIn
        "GraphicEq" -> PhosphorIcons.Regular.Waveform
        "Groups" -> PhosphorIcons.Regular.UsersThree
        "Headphones" -> PhosphorIcons.Regular.Headphones
        "Help" -> PhosphorIcons.Regular.Question
        "History" -> PhosphorIcons.Regular.ClockCounterClockwise
        "Home" -> PhosphorIcons.Regular.House
        "Info" -> PhosphorIcons.Regular.Info
        "Insights" -> PhosphorIcons.Regular.ChartLineUp
        "IosShare" -> PhosphorIcons.Regular.ShareNetwork
        "KeyboardArrowDown" -> PhosphorIcons.Regular.CaretDown
        "KeyboardDoubleArrowDown" -> PhosphorIcons.Regular.CaretDoubleDown
        "KeyboardDoubleArrowLeft" -> PhosphorIcons.Regular.CaretDoubleLeft
        "KeyboardDoubleArrowRight" -> PhosphorIcons.Regular.CaretDoubleRight
        "KeyboardDoubleArrowUp" -> PhosphorIcons.Regular.CaretDoubleUp
        "LeftPanelClose" -> PhosphorIcons.Regular.Sidebar
        "LeftPanelOpen" -> PhosphorIcons.Regular.SidebarSimple
        "LibraryMusic" -> PhosphorIcons.Regular.Books
        "LogoDev" -> PhosphorIcons.Regular.DevToLogo
        "Logout" -> PhosphorIcons.Regular.SignOut
        "Lyrics" -> PhosphorIcons.Regular.TextAlignLeft
        "MicOff" -> PhosphorIcons.Regular.MicrophoneSlash
        "MoreVert" -> PhosphorIcons.Regular.DotsThreeVertical
        "Movie" -> PhosphorIcons.Regular.FilmStrip
        "MovieOff" -> PhosphorIcons.Regular.FilmSlate
        "MusicCast" -> PhosphorIcons.Regular.Airplay
        "MusicNote" -> PhosphorIcons.Regular.MusicNote
        "MusicOff" -> PhosphorIcons.Fill.SpeakerSlash
        "Notifications" -> PhosphorIcons.Regular.Bell
        "OpenInFull" -> PhosphorIcons.Regular.ArrowsOut
        "OpenInNew" -> PhosphorIcons.Regular.ArrowSquareOut
        "Pause" -> PhosphorIcons.Fill.Pause
        "PauseCircle" -> PhosphorIcons.Fill.PauseCircle
        "PeopleAlt" -> PhosphorIcons.Regular.Users
        "Person" -> PhosphorIcons.Regular.Person
        "PersonAdd" -> PhosphorIcons.Regular.UserPlus
        "PictureInPictureAlt" -> PhosphorIcons.Regular.PictureInPicture
        "PlayArrow" -> PhosphorIcons.Fill.Play
        "PlayCircle" -> PhosphorIcons.Fill.PlayCircle
        "PlaylistAdd" -> PhosphorIcons.Regular.Playlist
        "PushPin" -> PhosphorIcons.Regular.PushPin
        "QueueMusic" -> PhosphorIcons.Regular.Queue
        "ReceiptLong" -> PhosphorIcons.Regular.Receipt
        "Refresh" -> PhosphorIcons.Regular.ArrowClockwise
        "Remove" -> PhosphorIcons.Regular.Minus
        "Repeat" -> PhosphorIcons.Regular.Repeat
        "RepeatOne" -> PhosphorIcons.Regular.RepeatOnce
        "Replay" -> PhosphorIcons.Regular.ArrowCounterClockwise
        "Replay5" -> PhosphorIcons.Regular.ArrowCounterClockwise
        "RestartAlt" -> PhosphorIcons.Regular.ArrowCounterClockwise
        "RssFeed" -> PhosphorIcons.Regular.Rss
        "Search" -> PhosphorIcons.Regular.MagnifyingGlass
        "SelectAll" -> PhosphorIcons.Regular.SelectionAll
        "Send" -> PhosphorIcons.Regular.PaperPlaneTilt
        "Sensors" -> PhosphorIcons.Regular.Waveform
        "Settings" -> PhosphorIcons.Regular.Gear
        "Share" -> PhosphorIcons.Regular.Share
        "Shuffle" -> PhosphorIcons.Regular.Shuffle
        "SkipNext" -> PhosphorIcons.Regular.SkipForward
        "SkipPrevious" -> PhosphorIcons.Regular.SkipBack
        "Smartphone" -> PhosphorIcons.Regular.DeviceMobile
        "Sort" -> PhosphorIcons.Regular.SortAscending
        "Speaker" -> PhosphorIcons.Regular.SpeakerHigh
        "Speed" -> PhosphorIcons.Regular.Gauge
        "Star" -> PhosphorIcons.Fill.Star
        "StarBorder" -> PhosphorIcons.Regular.Star
        "Subtitles" -> PhosphorIcons.Regular.ClosedCaptioning
        "SubtitlesOff" -> PhosphorIcons.Regular.ClosedCaptioning
        "Sync" -> PhosphorIcons.Regular.ArrowsClockwise
        "SyncDisabled" -> PhosphorIcons.Regular.ArrowsClockwise
        "ThumbDown" -> PhosphorIcons.Fill.ThumbsDown
        "ThumbDownOutline" -> PhosphorIcons.Regular.ThumbsDown
        "ThumbUp" -> PhosphorIcons.Fill.ThumbsUp
        "ThumbUpOutline" -> PhosphorIcons.Regular.ThumbsUp
        "ThumbsUpDown" -> PhosphorIcons.Regular.ThumbsUp
        "TipsAndUpdates" -> PhosphorIcons.Regular.Lightbulb
        "TrendingUp" -> PhosphorIcons.Regular.TrendUp
        "Tune" -> PhosphorIcons.Regular.SlidersHorizontal
        "UnfoldLess" -> PhosphorIcons.Regular.CaretUp
        "UnfoldMore" -> PhosphorIcons.Regular.CaretDown
        "Update" -> PhosphorIcons.Regular.ArrowClockwise
        "VolumeDown" -> PhosphorIcons.Regular.SpeakerLow
        "VolumeOff" -> PhosphorIcons.Regular.SpeakerSlash
        "VolumeUp" -> PhosphorIcons.Regular.SpeakerHigh
        else -> legacy
    }
}
