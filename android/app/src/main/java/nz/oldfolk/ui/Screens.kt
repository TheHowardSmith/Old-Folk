package nz.oldfolk.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nz.oldfolk.R
import android.net.Uri

import android.text.format.Formatter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import nz.oldfolk.data.formatClock
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Slider
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nz.oldfolk.AppGraph
import nz.oldfolk.BuildConfig
import nz.oldfolk.data.Genre
import nz.oldfolk.data.Playlist
import nz.oldfolk.data.Track
import nz.oldfolk.data.Updates
import nz.oldfolk.playback.MediaCache
import nz.oldfolk.playback.RadioEngine

enum class Tab(val label: String, val icon: ImageVector) {
    RADIO("Radio", Icons.Filled.Radio),
    GENRES("Genres", Icons.Filled.Tune),
    PLAYLISTS("Playlists", Icons.AutoMirrored.Filled.QueueMusic),
    HISTORY("History", Icons.Filled.History),
}

// ====================================================================== app shell (graph-bound)

@Composable
fun OldFolkAppUi(graph: AppGraph, onBeforePlay: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(Tab.RADIO) }
    var openPlaylist by rememberSaveable { mutableStateOf<String?>(null) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var addTrack by remember { mutableStateOf<Track?>(null) }
    BackHandler(enabled = showAbout || openPlaylist != null) {
        if (showAbout) showAbout = false else openPlaylist = null
    }

    AppFrame(
        tab = tab,
        title = when {
            showAbout -> "About"
            tab == Tab.RADIO -> "Old Folk Air"
            else -> tab.label
        },
        showBack = showAbout || (tab == Tab.PLAYLISTS && openPlaylist != null),
        showAboutButton = !showAbout,
        onBack = { if (showAbout) showAbout = false else openPlaylist = null },
        onAbout = { showAbout = true },
        onTab = { tab = it; showAbout = false; if (it != Tab.PLAYLISTS) openPlaylist = null },
    ) {
        if (showAbout) AboutScreen(graph)
        else when (tab) {
            Tab.RADIO -> RadioScreen(graph, onBeforePlay, onSave = { addTrack = it }, onChooseGenres = { tab = Tab.GENRES },
                onAbout = { showAbout = true })
            Tab.GENRES -> GenresScreen(graph)
            Tab.PLAYLISTS -> openPlaylist?.let { id ->
                PlaylistDetailScreen(graph, id, onBeforePlay, onPlayed = { tab = Tab.RADIO }, onGone = { openPlaylist = null })
            } ?: PlaylistsScreen(graph, onOpen = { openPlaylist = it })
            Tab.HISTORY -> HistoryScreen(graph, onBeforePlay)
        }
    }
    addTrack?.let { AddToPlaylistDialog(graph, it, onDismiss = { addTrack = null }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFrame(
    tab: Tab,
    title: String,
    showBack: Boolean,
    showAboutButton: Boolean,
    onBack: () -> Unit,
    onAbout: () -> Unit,
    onTab: (Tab) -> Unit,
    content: @Composable () -> Unit,
) {
    val rc = LocalRadioColors.current
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Scaffold(
        modifier = Modifier.screenTexture(MaterialTheme.colorScheme.background, dark),
        topBar = {
            if (title == "Old Folk Air") {
                // The radio screen has no title bar (every pixel goes to the radio):
                // just the band behind the status bar and the brass rule.
                Column(Modifier.decoBand(rc.band, rc.bandLo, rc.bandLine)) {
                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    HorizontalDivider(thickness = 2.dp, color = Color(0xFFB08D4A))
                }
            } else Column(Modifier.decoBand(rc.band, rc.bandLo, rc.bandLine)) {
                TopAppBar(
                    title = {
                        if (title == "Old Folk Air") {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "Old Folk Air" }
                                    .background(Color(0xFF3A2C22), CircleShape).border(1.dp, Color(0xFFB08A45), CircleShape)
                                    .padding(start = 3.dp, top = 3.dp, bottom = 3.dp, end = 14.dp)) {
                                Image(painterResource(R.drawable.logo_roundel), null, Modifier.size(34.dp))
                                Spacer(Modifier.width(10.dp))
                                Icon(painterResource(R.drawable.logo_wordmark), null, Modifier.height(22.dp), tint = Color(0xFFF4ECDC))
                            }
                        } else {
                            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.background(rc.band, RoundedCornerShape(12.dp)).border(1.dp, rc.bandEdge, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 2.dp))
                        }
                    },
                    navigationIcon = {
                        if (showBack) IconButton(onClick = onBack, modifier = Modifier.background(rc.band, CircleShape).border(1.dp, rc.bandEdge, CircleShape)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                    },
                    actions = {
                        if (showAboutButton) IconButton(onClick = onAbout, modifier = Modifier.padding(end = 4.dp).background(rc.band, CircleShape).border(1.dp, rc.bandEdge, CircleShape)) {
                            Icon(Icons.Filled.Info, "About", tint = rc.bandAccent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, titleContentColor = rc.onBand,
                        navigationIconContentColor = rc.onBand, actionIconContentColor = rc.onBand,
                    ),
                )
                HorizontalDivider(thickness = 2.dp, color = Color(0xFFB08D4A))   // brass rule
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 2.dp, color = Color(0xFFB08D4A))   // brass trim rail
                NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp,
                    modifier = Modifier.background(Brush.verticalGradient(listOf(rc.woodHi, rc.wood)))) {
                    Tab.entries.forEach { t ->
                        NavigationBarItem(
                            selected = tab == t,
                            onClick = { onTab(t) },
                            icon = { Icon(t.icon, null) },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = rc.onWalnut.copy(alpha = 0.16f),
                                selectedIconColor = rc.onWalnut,
                                selectedTextColor = rc.onWalnut,
                                unselectedIconColor = rc.onWalnut.copy(alpha = 0.72f),
                                unselectedTextColor = rc.onWalnut.copy(alpha = 0.72f),
                            ),
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent,   // the paper / wood texture shows through
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) { content() }
    }
}

// ====================================================================== radio

data class RadioUi(
    val station: String,
    val track: Track?,
    val genreName: String?,
    val isPlaying: Boolean,
    val isBuffering: Boolean,
    val message: String?,
    val inPlaylist: Boolean,
    val listLoading: Boolean,
    val listProgress: String?,
    val showRetry: Boolean,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
)

class RadioActions(
    val onPlayPause: () -> Unit = {},
    val onPrevious: () -> Unit = {},
    val onNext: () -> Unit = {},
    val onArchive: () -> Unit = {},
    val onSave: () -> Unit = {},
    val onReport: () -> Unit = {},
    val onBackToRadio: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onChooseGenres: () -> Unit = {},
    val onSeek: (Long) -> Unit = {},
)

@Composable
private fun RadioScreen(graph: AppGraph, onBeforePlay: () -> Unit, onSave: (Track) -> Unit, onChooseGenres: () -> Unit,
                        onAbout: () -> Unit) {
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    val genres by graph.settings.genres.collectAsState()
    val context = LocalContext.current
    val track = state.current
    var position by remember { mutableStateOf(0L to 0L) }
    LaunchedEffect(Unit) {
        while (true) { position = graph.engine.position(); kotlinx.coroutines.delay(500) }
    }
    val station = when {
        state.mode == RadioEngine.Mode.PLAYLIST -> "Playlist · ${state.playlistName}"
        genres.isEmpty() -> "Radio · all genres"
        genres.size == 1 -> "Radio · " + (list.index?.genreName(genres.first()) ?: genres.first())
        else -> "Radio · ${genres.size} genres"
    }
    RadioContent(
        RadioUi(
            station = station,
            track = track,
            genreName = track?.let { list.index?.genreName(it.genre) ?: it.genre },
            isPlaying = state.isPlaying,
            isBuffering = state.isBuffering,
            message = state.message,
            inPlaylist = state.mode == RadioEngine.Mode.PLAYLIST,
            listLoading = list.loading && list.tracks.isEmpty(),
            listProgress = if (list.loading && list.tracks.isNotEmpty() && !list.complete)
                "Fetching the track list in the background (${list.shardsLoaded}/${list.shardsTotal})" else null,
            showRetry = list.error != null && list.tracks.isEmpty(),
            positionMs = position.first,
            durationMs = position.second,
        ),
        RadioActions(
            onPlayPause = { onBeforePlay(); graph.engine.togglePlay() },
            onPrevious = { onBeforePlay(); graph.engine.previous() },
            onNext = { onBeforePlay(); graph.engine.skip() },
            onArchive = { track?.let { Links.open(context, it.detailsUrl) } },
            onSave = { track?.let(onSave) },
            onReport = { track?.let { Links.open(context, Links.reportTrack(it)) } },
            onBackToRadio = { onBeforePlay(); graph.engine.startRadio() },
            onRetry = { graph.trackList.retry { graph.settings.genres.value } },
            onChooseGenres = onChooseGenres,
            onSeek = { graph.engine.seekTo(it); position = it to position.second },
        ),
        onAbout = onAbout,
    )
}

/**
 * The radio screen fits any phone without scrolling: the dial, cover, track
 * details, knobs and action buttons are all on screen. On short screens the
 * dial is thinner, the knobs smaller and the cover shrinks to the space left;
 * in landscape the cover and dial sit beside the controls.
 */
@Composable
fun RadioContent(ui: RadioUi, actions: RadioActions, onAbout: () -> Unit = {}) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth > maxHeight && maxWidth >= 560.dp
        val h = maxHeight
        val size = when {
            wide -> if (h < 420.dp) RadioSize.Tight else RadioSize.Compact
            h < 560.dp -> RadioSize.Tight
            h < 700.dp -> RadioSize.Compact
            else -> RadioSize.Roomy
        }
        if (wide) {
            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TuningDial(year = ui.track?.year, station = ui.station, playing = ui.isPlaying, thin = true)
                    Spacer(Modifier.height(8.dp))
                    ArtSlot(ui.track?.artUrl, Modifier.weight(1f).fillMaxWidth())
                }
                Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly) {
                    NowPlaying(ui, actions, size)
                    PlayHead(ui, actions)
                    Controls(ui, actions, size)
                    ActionRow(ui, actions, onAbout)
                }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = if (size == RadioSize.Roomy) 12.dp else 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                TuningDial(year = ui.track?.year, station = ui.station, playing = ui.isPlaying,
                    thin = size != RadioSize.Roomy)
                Spacer(Modifier.height(if (size == RadioSize.Roomy) 14.dp else 8.dp))
                ArtSlot(ui.track?.artUrl, Modifier.weight(1f).fillMaxWidth())
                Spacer(Modifier.height(if (size == RadioSize.Tight) 4.dp else 10.dp))
                NowPlaying(ui, actions, size)
                PlayHead(ui, actions)
                Controls(ui, actions, size)
                ActionRow(ui, actions, onAbout)
            }
        }
    }
}

enum class RadioSize { Roomy, Compact, Tight }

/** The cover (or the drawn 78), as large as the space left allows, up to 300 dp; hidden if under 56 dp. */
@Composable
private fun ArtSlot(url: String?, modifier: Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val side = minOf(maxWidth, maxHeight, 300.dp)
        if (side >= 56.dp) AlbumArt(url, Modifier.size(side))
    }
}

@Composable
private fun NowPlaying(ui: RadioUi, actions: RadioActions, size: RadioSize) {
    val rc = LocalRadioColors.current
    val track = ui.track
    val tight = size == RadioSize.Tight
    Column(Modifier.fillMaxWidth().heightIn(min = if (tight) 64.dp else 96.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if (track == null) {
            Text(ui.message ?: "Press play to tune in", style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center)
            if (ui.listLoading) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(Modifier.width(160.dp), color = rc.glow)
            }
            if (ui.showRetry) {
                Spacer(Modifier.height(8.dp))
                Button(onClick = actions.onRetry) { Text("Retry") }
            } else {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = actions.onChooseGenres, modifier = Modifier.height(44.dp)) {
                    Icon(Icons.Filled.Tune, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp)); Text("Choose genres")
                }
            }
        } else {
            Text(track.title, style = if (tight) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center, maxLines = if (tight) 2 else 3, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(if (tight) 2.dp else 6.dp))
            Text(track.artist ?: "Unknown performer", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                maxLines = if (tight) 1 else 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(if (tight) 2.dp else 8.dp))
            val details = listOfNotNull("${track.yearLabel}  ·  ${track.region}", ui.genreName)
            if (size == RadioSize.Roomy) {
                details.forEach {
                    Text(it, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            } else {
                Text(details.joinToString("  ·  "), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (!track.onArchiveOrg) {
                Text("from the ${track.sourceName}", style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            val note = ui.message ?: ui.listProgress
            if (note != null) {
                Text(note, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

/** How far into the track we are: drag the brass pointer to jump; elapsed time left, length right. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayHead(ui: RadioUi, actions: RadioActions) {
    val rc = LocalRadioColors.current
    var dragging by remember { mutableStateOf<Float?>(null) }
    val dur = ui.durationMs
    val frac = if (dur > 0) (ui.positionMs.toFloat() / dur).coerceIn(0f, 1f) else 0f
    val shown = dragging ?: frac
    val labelStyle = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum")
    Row(Modifier.fillMaxWidth().height(36.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(if (ui.track == null) "" else formatClock(if (dur > 0) (shown * dur).toLong() else ui.positionMs),
            style = labelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(48.dp),
            textAlign = TextAlign.Start)
        val enabled = ui.track != null && dur > 0
        val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
        Slider(
            value = shown,
            onValueChange = { dragging = it },
            onValueChangeFinished = { dragging?.let { if (dur > 0) actions.onSeek((it * dur).toLong()) }; dragging = null },
            enabled = enabled,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Position in track" },
            interactionSource = interaction,
            // A thin brass line with a small round pointer, like a dial cord, not Material's thick bar.
            thumb = {
                Box(Modifier.size(if (dragging != null) 18.dp else 14.dp)
                    .background(if (enabled) rc.brass else rc.brass.copy(alpha = 0.4f), CircleShape)
                    .border(1.dp, Color(0x40000000), CircleShape))
            },
            track = {
                Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                    val y = size.height / 2
                    drawLine(rc.brass.copy(alpha = 0.25f), Offset(0f, y), Offset(size.width, y), size.height, StrokeCap.Round)
                    if (enabled) drawLine(rc.brass, Offset(0f, y), Offset(size.width * shown, y), size.height, StrokeCap.Round)
                }
            },
        )
        Text(if (ui.track == null || dur <= 0) "" else formatClock(dur),
            style = labelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(48.dp),
            textAlign = TextAlign.End)
    }
}

/** The three knobs on the grille cloth, smaller on short screens. */
@Composable
private fun Controls(ui: RadioUi, actions: RadioActions, size: RadioSize) {
    val rc = LocalRadioColors.current
    val (panel, small, big) = when (size) {
        RadioSize.Roomy -> Triple(124.dp, 66.dp, 96.dp)
        RadioSize.Compact -> Triple(106.dp, 58.dp, 84.dp)
        RadioSize.Tight -> Triple(88.dp, 50.dp, 72.dp)
    }
    WoodCabinet(Modifier.fillMaxWidth(), frame = 5.dp) {
        Box(
            Modifier.fillMaxWidth().height(panel)
                .clip(RoundedCornerShape(16.dp))
                .voxCloth(rc.voxBase, rc.voxGold, rc.voxTeal, rc.voxMaroon)
                .border(1.dp, Color(0x40000000), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(if (size == RadioSize.Tight) 16.dp else 22.dp)) {
                Knob(actions.onPrevious, "Previous track", small) { Icon(Icons.Filled.SkipPrevious, null, Modifier.size(small * 0.58f)) }
                Knob(actions.onPlayPause, if (ui.isPlaying) "Pause" else "Play", big, primary = true) {
                    if (ui.isBuffering) CircularProgressIndicator(Modifier.size(big * 0.42f), strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    else Icon(if (ui.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, null, Modifier.size(big * 0.56f))
                }
                Knob(actions.onNext, "Next track", small) { Icon(Icons.Filled.SkipNext, null, Modifier.size(small * 0.58f)) }
            }
        }
    }
}

/** Archive, Save, Report (for the current track), Back to radio (in a playlist) and About: always on screen. */
@Composable
private fun ActionRow(ui: RadioUi, actions: RadioActions, onAbout: () -> Unit) {
    val hasTrack = ui.track != null
    Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
        if (ui.inPlaylist) SmallAction(Icons.Filled.Radio, "Radio", actions.onBackToRadio)
        SmallAction(Icons.AutoMirrored.Filled.OpenInNew, "Archive", actions.onArchive, enabled = hasTrack)
        SmallAction(Icons.AutoMirrored.Filled.PlaylistAdd, "Save", actions.onSave, enabled = hasTrack)
        SmallAction(Icons.Outlined.Flag, "Report", actions.onReport, enabled = hasTrack)
        SmallAction(Icons.Filled.Info, "About", onAbout)
    }
}

/** Icon over a short label, sharing the row equally, so all of them fit even with large fonts. */
@Composable
private fun RowScope.SmallAction(icon: ImageVector, label: String, onClick: () -> Unit, enabled: Boolean = true) {
    val alpha = if (enabled) 1f else 0.4f
    TextButton(onClick = onClick, enabled = enabled, modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(20.dp), tint = LocalRadioColors.current.brass.copy(alpha = alpha))
            Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), style = PlateLabel, color = LocalRadioColors.current.brass,
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp))
}

// ====================================================================== genres

@Composable
private fun GenresScreen(graph: AppGraph) {
    val list by graph.trackList.state.collectAsState()
    val selected by graph.settings.genres.collectAsState()
    GenresContent(
        genres = list.index?.genres.orEmpty(),
        selected = selected,
        onAll = { graph.settings.setGenres(emptySet()) },
        onToggle = { key -> graph.settings.setGenres(if (key in selected) selected - key else selected + key) },
    )
}

@Composable
fun GenresContent(genres: List<Genre>, selected: Set<String>, onAll: () -> Unit, onToggle: (String) -> Unit) {
    val rc = LocalRadioColors.current
    val sorted = genres.sortedBy { it.name.lowercase() }
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        item {
            val tuned = if (selected.isEmpty()) genres.sumOf { it.count } else genres.filter { it.key in selected }.sumOf { it.count }
            DialStrip("Tuned to · " + (if (selected.isEmpty()) "all genres" else plural(selected.size, "genre")) +
                " · " + "%,d".format(tuned))
            Spacer(Modifier.height(12.dp))
            Text("Choose what the radio plays. Pick one or several.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
            GenreRow("All genres", genres.sumOf { it.count }, selected.isEmpty(), onAll, bold = true)
            HorizontalDivider(color = rc.brass.copy(alpha = 0.3f))
        }
        if (genres.isEmpty()) item { Text("Genres appear once the track list has downloaded.", Modifier.padding(16.dp)) }
        items(sorted, key = { it.key }) { g -> GenreRow(g.name, g.count, g.key in selected, { onToggle(g.key) }) }
    }
}

@Composable
private fun GenreRow(name: String, count: Int, checked: Boolean, onClick: () -> Unit, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().height(52.dp).padding(vertical = 2.dp)
        .background(if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
        .clickable(onClick = onClick).padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(if (checked) LocalRadioColors.current.glow else Color.Transparent))
        Checkbox(checked = checked, onCheckedChange = { onClick() })
        Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (bold) FontWeight.SemiBold else null,
            modifier = Modifier.weight(1f))
        Text("%,d".format(count), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ====================================================================== playlists

@Composable
private fun PlaylistsScreen(graph: AppGraph, onOpen: (String) -> Unit) {
    val playlists by graph.playlists.playlists.collectAsState()
    var creating by remember { mutableStateOf(false) }
    PlaylistsContent(playlists, onOpen, onNew = { creating = true })
    if (creating) NameDialog("New playlist", "", onDismiss = { creating = false }) { graph.playlists.create(it); creating = false }
}

@Composable
fun PlaylistsContent(
    playlists: List<Playlist>, onOpen: (String) -> Unit, onNew: () -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Text("Save tracks from the Radio screen with \"Save\". Playlists stream, like the radio.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onNew) { Text("New playlist") }
            Spacer(Modifier.height(12.dp))
        }
        items(playlists, key = { it.id }) { p ->
            val rc = LocalRadioColors.current
            Card(
                onClick = { onOpen(p.id) },
                colors = CardDefaults.cardColors(containerColor = rc.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, rc.brass.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            ) {
                ListItem(
                    headlineContent = { Text(p.name, style = TrackTitle) },
                    supportingContent = { Text(plural(p.tracks.size, "track")) },
                    leadingContent = {
                        val labels = listOf(Color(0xFFB23A1E), Color(0xFF2E5E8C), Color(0xFF3F6B3A), Color(0xFF8C6B2F))
                        RecordIcon(labels[(p.id.hashCode() and 0x7fffffff) % labels.size])
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun PlaylistDetailScreen(
    graph: AppGraph, id: String, onBeforePlay: () -> Unit, onPlayed: () -> Unit, onGone: () -> Unit,
) {
    val playlists by graph.playlists.playlists.collectAsState()
    val p = playlists.firstOrNull { it.id == id }
    if (p == null) {
        LaunchedEffect(id) { onGone() }
        return
    }
    var renaming by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    PlaylistDetailContent(
        p,
        onPlay = { i -> onBeforePlay(); graph.engine.playPlaylist(p, i); onPlayed() },
        onShuffle = { onBeforePlay(); graph.engine.playPlaylist(p, p.tracks.indices.random(), shuffle = true); onPlayed() },
        onRename = { renaming = true },
        onDelete = { confirmDelete = true },
        onRemove = { graph.playlists.remove(p.id, it) },
    )
    if (renaming) NameDialog("Rename playlist", p.name, onDismiss = { renaming = false }) { graph.playlists.rename(p.id, it); renaming = false }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete \"${p.name}\"?") },
            text = { Text("The playlist will be removed. The recordings stay in the radio's selection.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; graph.playlists.delete(p.id); onGone() }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }
}

@Composable
fun PlaylistDetailContent(
    p: Playlist,
    onPlay: (Int) -> Unit, onShuffle: () -> Unit, onRename: () -> Unit, onDelete: () -> Unit,
    onRemove: (Track) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            Text(p.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 16.dp))
            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Button(enabled = p.tracks.isNotEmpty(), onClick = { onPlay(0) }) {
                    Icon(Icons.Filled.PlayArrow, null); Text("Play")
                }
                OutlinedButton(enabled = p.tracks.isNotEmpty(), onClick = onShuffle) {
                    Icon(Icons.Filled.Shuffle, null); Spacer(Modifier.width(4.dp)); Text("Shuffle")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onRename) { Icon(Icons.Filled.Edit, "Rename") }
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "Delete playlist") }
            }
            if (p.tracks.isEmpty()) Text("No tracks yet. Use \"Save\" on the Radio screen.", Modifier.padding(16.dp))
        }
        itemsIndexed(p.tracks, key = { _, t -> t.key }) { index, t ->
            ListItem(
                headlineContent = { Text(t.title, style = TrackTitle, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                supportingContent = { Text("${t.artist ?: "Unknown"} · ${t.yearLabel}") },
                leadingContent = { Text("${index + 1}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingContent = { IconButton(onClick = { onRemove(t) }) { Icon(Icons.Filled.Delete, "Remove") } },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { onPlay(index) },
            )
        }
    }
}

@Composable
private fun AddToPlaylistDialog(graph: AppGraph, track: Track, onDismiss: () -> Unit) {
    val playlists by graph.playlists.playlists.collectAsState()
    var creating by remember { mutableStateOf(playlists.isEmpty()) }
    if (creating) {
        NameDialog("New playlist", "", onDismiss = onDismiss) { name ->
            val p = graph.playlists.create(name)
            graph.playlists.add(p.id, track)
            onDismiss()
        }
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save to playlist") },
        text = {
            LazyColumn {
                items(playlists, key = { it.id }) { p ->
                    ListItem(
                        headlineContent = { Text(p.name) },
                        supportingContent = { Text(plural(p.tracks.size, "track")) },
                        modifier = Modifier.clickable { graph.playlists.add(p.id, track); onDismiss() },
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { creating = true }) { Text("New playlist") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun NameDialog(title: String, initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Name") }) },
        confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { onConfirm(name) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

// ====================================================================== history

sealed interface HistoryRow {
    data class Past(val track: Track, val time: String) : HistoryRow
    data class Now(val track: Track) : HistoryRow
    data class Ahead(val track: Track, val queueIndex: Int) : HistoryRow
}

@Composable
private fun HistoryScreen(graph: AppGraph, onBeforePlay: () -> Unit) {
    val entries by graph.streamHistory.entries.collectAsState()
    val state by graph.engine.state.collectAsState()
    val timeFmt = remember { java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT) }
    val dayFmt = remember { java.text.SimpleDateFormat("d MMM", java.util.Locale.getDefault()) }
    val today = remember { dayFmt.format(java.util.Date()) }
    val fmt = { ms: Long -> val d = java.util.Date(ms); if (dayFmt.format(d) == today) timeFmt.format(d) else dayFmt.format(d) }
    val current = state.current
    val past = if (current != null && entries.lastOrNull()?.track?.key == current.key) entries.dropLast(1) else entries
    // Entries saved before track lengths were in the list: take the length from the current list.
    val list by graph.trackList.state.collectAsState()
    val lengths = remember(list.tracks.size) { list.tracks.associate { it.key to it.seconds } }
    val withLength = { t: Track -> if (t.seconds != null) t else t.copy(seconds = lengths[t.key]) }
    val rows = buildList {
        past.forEach { add(HistoryRow.Past(withLength(it.track), fmt(it.time))) }
        current?.let { add(HistoryRow.Now(it)) }
        if (state.currentIndex >= 0) state.queue.forEachIndexed { i, t -> if (i > state.currentIndex) add(HistoryRow.Ahead(t, i)) }
    }
    HistoryContent(
        rows = rows,
        isPlaying = state.isPlaying,
        isSaved = { t -> graph.mediaCache.isFullyCached(t.streamUrl) },
        onRow = { row ->
            onBeforePlay()
            when (row) {
                is HistoryRow.Past -> graph.engine.playTrack(row.track)
                is HistoryRow.Now -> graph.engine.togglePlay()
                is HistoryRow.Ahead -> graph.engine.playQueued(row.queueIndex)
            }
        },
    )
}

/**
 * Everything streamed on this phone (scroll up), the current track and the
 * tracks queued ahead. Recent and upcoming tracks are kept on the phone;
 * older ones stream again when tapped.
 */
@Composable
fun HistoryContent(rows: List<HistoryRow>, isPlaying: Boolean, isSaved: (Track) -> Boolean, onRow: (HistoryRow) -> Unit) {
    if (rows.isEmpty()) {
        Text("Tracks you play will appear here.", Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val nowIndex = rows.indexOfFirst { it is HistoryRow.Now }.takeIf { it >= 0 } ?: rows.size
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (nowIndex - 2).coerceAtLeast(0))
    LazyColumn(state = listState, contentPadding = PaddingValues(vertical = 8.dp)) {
        itemsIndexed(rows, key = { i, _ -> i }) { i, row ->
            when (row) {
                is HistoryRow.Past -> {
                    if (i == 0) SectionLabel("Played", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    HistoryItem(row.track, row.time, isSaved(row.track), kind = 0) { onRow(row) }
                }
                is HistoryRow.Now -> HistoryItem(row.track, if (isPlaying) "Now playing" else "Paused", true, kind = 1) { onRow(row) }
                is HistoryRow.Ahead -> {
                    if (i == nowIndex + 1) SectionLabel("Up next", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    HistoryItem(row.track, null, isSaved(row.track), kind = 2) { onRow(row) }
                }
            }
        }
    }
}

/** kind: 0 = played, 1 = current, 2 = up next. */
@Composable
private fun HistoryItem(t: Track, label: String?, saved: Boolean, kind: Int, onClick: () -> Unit) {
    val rc = LocalRadioColors.current
    val current = kind == 1
    val fade = if (kind == 0) 0.68f else 1f
    Row(
        Modifier.fillMaxWidth()
            .background(if (current) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(3.dp).height(if (current) 76.dp else 64.dp).background(if (current) rc.glow else Color.Transparent))
        ListItem(
            headlineContent = {
                Text(t.title, style = TrackTitle, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    fontWeight = if (current) FontWeight.SemiBold else null,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = fade))
            },
            supportingContent = {
                Column {
                    Text(listOfNotNull(t.artist ?: "Unknown performer", t.yearLabel.takeIf { !current },
                        t.lengthLabel.takeIf { !current }).joinToString(" · "),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = fade))
                    if (current) Text(listOfNotNull(t.yearLabel, label, t.lengthLabel).joinToString(" · "), maxLines = 1, style = MaterialTheme.typography.bodySmall,
                        color = rc.liveText, fontWeight = FontWeight.Medium)
                }
            },
            leadingContent = when {
                current -> { { Icon(Icons.Filled.GraphicEq, "Current track", Modifier.size(22.dp), tint = rc.glow) } }
                kind == 0 -> { {
                    Icon(Icons.Filled.History, "Played", Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
                } }
                kind == 2 -> { {
                    if (saved) Icon(Icons.Filled.OfflinePin, "Downloaded", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    else Icon(Icons.Filled.CloudDownload, "Will stream", Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                } }
                else -> null
            },
            trailingContent = if (kind == 0 && label != null) { {
                Text(label, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = fade))
            } } else null,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.weight(1f),
        )
    }
}

// ====================================================================== about

const val CONTACT_EMAIL = "info@oldfolkair.com"

data class AboutInfo(
    val listVersion: String,
    val tracksLoaded: Int,
    val shards: String,
    val played: Int,
    val failures: Int,
    val cache: String,
    val appVersion: String,
    val skipNoisy: Boolean = true,
    val noisySkipped: Int = 0,
    val update: UpdateUi = UpdateUi.Idle,
    val theme: String = "system",
)

sealed interface UpdateUi {
    data object Idle : UpdateUi
    data object Checking : UpdateUi
    data object UpToDate : UpdateUi
    data class Available(val version: String) : UpdateUi
    data class Failed(val reason: String) : UpdateUi
}

@Composable
private fun AboutScreen(graph: AppGraph) {
    val context = LocalContext.current
    val failures by graph.failures.items.collectAsState()
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    var confirmReset by remember { mutableStateOf(false) }
    val skipNoisy by graph.settings.skipNoisy.collectAsState()
    val theme by graph.settings.theme.collectAsState()
    var update by remember { mutableStateOf<UpdateUi>(UpdateUi.Idle) }
    val scope = rememberCoroutineScope()
    AboutContent(
        AboutInfo(
            update = update,
            theme = theme,
            skipNoisy = skipNoisy,
            noisySkipped = graph.levels.noisyCount(),
            listVersion = list.index?.version?.toString() ?: "-",
            tracksLoaded = list.tracks.size,
            shards = "${list.shardsLoaded}/${list.shardsTotal}",
            played = state.playedCount,
            failures = failures.size,
            cache = "${Formatter.formatShortFileSize(context, graph.mediaCache.usedBytes())} of " +
                Formatter.formatShortFileSize(context, MediaCache.MAX_BYTES),
            appVersion = BuildConfig.VERSION_NAME,
        ),
        onReset = { confirmReset = true },
        onSendReport = { Links.open(context, Links.reportFailures(graph.failures.reportBody(BuildConfig.VERSION_NAME))) },
        onClearFailures = { graph.failures.clear() },
        onGitHub = { Links.open(context, Links.repoUrl) },
        onSkipNoisy = { graph.settings.setSkipNoisy(it) },
        onEmail = { Links.open(context, "mailto:$CONTACT_EMAIL?subject=" + Uri.encode("Old Folk Air: our recordings")) },
        onCheckUpdate = {
            update = UpdateUi.Checking
            scope.launch {
                update = when (val r = withContext(Dispatchers.IO) { Updates.check() }) {
                    is Updates.Result.Available -> UpdateUi.Available(r.version)
                    Updates.Result.UpToDate -> UpdateUi.UpToDate
                    is Updates.Result.Failed -> UpdateUi.Failed(r.reason)
                }
            }
        },
        onDownloadUpdate = { Links.open(context, Updates.apkUrl) },
        onTheme = { graph.settings.setTheme(it) },
    )
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset listening history?") },
            text = { Text("Clears the History tab, and tracks you've already heard can come up again.") },
            confirmButton = { TextButton(onClick = { graph.history.clear(); graph.streamHistory.clear(); confirmReset = false }) { Text("Reset") } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } },
        )
    }
}

@Composable
fun AboutContent(
    info: AboutInfo, onReset: () -> Unit, onSendReport: () -> Unit, onClearFailures: () -> Unit, onGitHub: () -> Unit,
    onSkipNoisy: (Boolean) -> Unit = {},
    onEmail: () -> Unit = {},
    onCheckUpdate: () -> Unit = {},
    onDownloadUpdate: () -> Unit = {},
    onTheme: (String) -> Unit = {},
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Old Folk Air plays public-domain folk and field recordings from around the world, streamed straight from the archives that hold them: the Internet Archive, the Library of Congress, Finnish archives via Finna, the Instituto Moreira Salles and the Digital South Asia Library.",
            style = MaterialTheme.typography.bodyLarge)
        AboutCard("App updates") {
            Text("You have version ${info.appVersion}.", style = MaterialTheme.typography.bodyMedium)
            when (val u = info.update) {
                UpdateUi.Idle -> OutlinedButton(onClick = onCheckUpdate) { Text("Check for updates") }
                UpdateUi.Checking -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp)); Text("Checking…")
                }
                UpdateUi.UpToDate -> {
                    Text("This is the latest version.", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onCheckUpdate) { Text("Check again") }
                }
                is UpdateUi.Available -> {
                    Text("Version ${u.version} is available.", style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold)
                    Button(onClick = onDownloadUpdate) {
                        Icon(Icons.Filled.Download, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                        Text("Download update")
                    }
                    Text("Your browser downloads it; open the file to install it over this version. " +
                        "Your history and playlists are kept.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                is UpdateUi.Failed -> {
                    Text("Couldn't check for updates (${u.reason}).", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onCheckUpdate) { Text("Try again") }
                        TextButton(onClick = onDownloadUpdate) { Text("Download latest") }
                    }
                }
            }
        }
        AboutCard("Appearance") {
            val options = listOf("light" to "Light", "dark" to "Dark", "system" to "Auto")
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                options.forEachIndexed { i, (key, label) ->
                    SegmentedButton(
                        selected = info.theme == key,
                        onClick = { onTheme(key) },
                        shape = SegmentedButtonDefaults.itemShape(i, options.size),
                        icon = {},
                    ) { Text(label, maxLines = 1) }
                }
            }
            Text("Auto follows your phone's light or dark setting.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        AboutCard("Rights") {
            Text("The recordings are hosted by those archives and aren't covered by this app's license. " +
                "Every track was recorded or released in 1970 or earlier, isn't claimed by a rights holder in the " +
                "US Copyright Office's records, and, if made after 1925, isn't on sale or streaming in the music " +
                "stores we check. Songs by a credited writer known to have died less than 70 years ago are " +
                "left out, and we're still checking writers we couldn't trace. " +
                "Most of these recordings are old enough to be out of copyright, but some may still be protected " +
                "in some countries, so we keep checking and remove any recording on request: " +
                "email $CONTACT_EMAIL or use Report.",
                style = MaterialTheme.typography.bodyMedium)
        }
        AboutCard("For archives and libraries") {
            Text("Do you look after a collection of traditional or historical recordings? We'd love to help more " +
                "people hear them. Old Folk Air streams straight from your own servers, credits your institution on every " +
                "track with a link back to your catalogue, and never copies or re-hosts your files. If you'd like your " +
                "recordings included, or want to set conditions, please get in touch.",
                style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onEmail) { Text(CONTACT_EMAIL) }
        }
        AboutCard("Sound") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Skip very noisy recordings", style = MaterialTheme.typography.bodyLarge)
                    Text("Each track is checked for heavy surface noise when it's fetched. " +
                        "Noisy ones are skipped on the radio (playlists always play everything)." +
                        if (info.noisySkipped > 0) " ${plural(info.noisySkipped, "track")} flagged so far." else "",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.width(12.dp))
                Switch(checked = info.skipNoisy, onCheckedChange = onSkipNoisy)
            }
        }
        AboutCard("Listening") {
            Text("Track list version ${info.listVersion} · ${"%,d".format(info.tracksLoaded)} tracks (${info.shards} parts)")
            Text("You've heard ${"%,d".format(info.played)} tracks. The radio won't repeat one until you've heard everything in your selection.")
            OutlinedButton(onClick = onReset) { Text("Reset listening history") }
        }
        AboutCard("Problems") {
            if (info.failures == 0) Text("No tracks have failed on this phone.")
            else {
                Text("${plural(info.failures, "track")} failed to play or download. Sending them helps check whether the file is gone or it was a connection or app problem.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onSendReport) { Text("Send report") }
                    OutlinedButton(onClick = onClearFailures) { Text("Clear") }
                }
                Text("Opens a pre-filled GitHub issue (needs a GitHub account).", style = MaterialTheme.typography.bodySmall)
            }
        }
        AboutCard("Storage") {
            Text("Streaming cache: ${info.cache} (your last 20 tracks and the next 20). " +
                "It's encrypted and only this app can play it; recordings are never saved to the phone as files.")
        }
        TextButton(onClick = onGitHub) { Text("Project on GitHub") }
        Text("App version ${info.appVersion} · Code: Apache-2.0 · Track list: CC0",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AboutCard(title: String, content: @Composable () -> Unit) {
    val rc = LocalRadioColors.current
    Card(colors = CardDefaults.cardColors(containerColor = rc.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, rc.brass.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title.uppercase(), style = PlateLabel, color = rc.brass)
            content()
        }
    }
}
