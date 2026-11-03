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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
            Tab.RADIO -> RadioScreen(graph, onBeforePlay, onSave = { addTrack = it }, onChooseGenres = { tab = Tab.GENRES })
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
            Column(Modifier.decoBand(rc.band, rc.bandLo, rc.bandLine)) {
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
)

@Composable
private fun RadioScreen(graph: AppGraph, onBeforePlay: () -> Unit, onSave: (Track) -> Unit, onChooseGenres: () -> Unit) {
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    val genres by graph.settings.genres.collectAsState()
    val context = LocalContext.current
    val track = state.current
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
        ),
    )
}

@Composable
fun RadioContent(ui: RadioUi, actions: RadioActions) {
    val rc = LocalRadioColors.current
    val track = ui.track
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TuningDial(year = track?.year, station = ui.station, playing = ui.isPlaying)
        Spacer(Modifier.height(16.dp))
        AlbumArt(track?.artUrl, Modifier.size(268.dp))
        Spacer(Modifier.height(12.dp))

        // Now playing.
        Column(Modifier.fillMaxWidth().heightIn(min = 112.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            if (track == null) {
                Text(ui.message ?: "Press play to tune in", style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center)
                if (ui.listLoading) {
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator(Modifier.width(160.dp), color = rc.glow)
                }
                if (ui.showRetry) {
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = actions.onRetry) { Text("Retry") }
                } else {
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = actions.onChooseGenres, modifier = Modifier.height(48.dp)) {
                        Icon(Icons.Filled.Tune, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp)); Text("Choose genres")
                    }
                }
            } else {
                Text(track.title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center,
                    maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Text(track.artist ?: "Unknown performer", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
                Text("${track.yearLabel}  ·  ${track.region}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                ui.genreName?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
                if (!track.onArchiveOrg) {
                    Text("from the ${track.sourceName}", style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }

        if (track != null && ui.message != null) {
            Text(ui.message, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }

        // Controls on a faint speaker grille.
        Spacer(Modifier.height(12.dp))
        WoodCabinet(Modifier.fillMaxWidth(), frame = 5.dp) {
        Box(
            Modifier.fillMaxWidth().height(124.dp)
                .clip(RoundedCornerShape(16.dp))
                .voxCloth(rc.voxBase, rc.voxGold, rc.voxTeal, rc.voxMaroon)
                .border(1.dp, Color(0x40000000), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                Knob(actions.onPrevious, "Previous track", 66.dp) { Icon(Icons.Filled.SkipPrevious, null, Modifier.size(38.dp)) }
                Knob(actions.onPlayPause, if (ui.isPlaying) "Pause" else "Play", 96.dp, primary = true) {
                    if (ui.isBuffering) CircularProgressIndicator(Modifier.size(40.dp), strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    else Icon(if (ui.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, null, Modifier.size(54.dp))
                }
                Knob(actions.onNext, "Next track", 66.dp) { Icon(Icons.Filled.SkipNext, null, Modifier.size(38.dp)) }
            }
        }
        }

        if (track != null) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                SmallAction(Icons.AutoMirrored.Filled.OpenInNew, "Archive", actions.onArchive)
                SmallAction(Icons.AutoMirrored.Filled.PlaylistAdd, "Save", actions.onSave)
                SmallAction(Icons.Outlined.Flag, "Report", actions.onReport)
            }
        }
        if (ui.inPlaylist) {
            OutlinedButton(onClick = actions.onBackToRadio) {
                Icon(Icons.Filled.Radio, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Back to radio")
            }
        }

        ui.listProgress?.let {
            Spacer(Modifier.height(14.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SmallAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(icon, null, Modifier.size(18.dp), tint = LocalRadioColors.current.brass)
        Spacer(Modifier.width(6.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurface)
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
    val rows = buildList {
        past.forEach { add(HistoryRow.Past(it.track, fmt(it.time))) }
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
                    Text(listOfNotNull(t.artist ?: "Unknown performer", t.yearLabel.takeIf { !current }).joinToString(" · "),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = fade))
                    if (current) Text("${t.yearLabel} · $label", maxLines = 1, style = MaterialTheme.typography.bodySmall,
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
)

@Composable
private fun AboutScreen(graph: AppGraph) {
    val context = LocalContext.current
    val failures by graph.failures.items.collectAsState()
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    var confirmReset by remember { mutableStateOf(false) }
    val skipNoisy by graph.settings.skipNoisy.collectAsState()
    AboutContent(
        AboutInfo(
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
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Old Folk Air plays public-domain folk and field recordings from around the world, streamed straight from the archives that hold them: the Internet Archive, the Library of Congress, Finnish archives via Finna, the Instituto Moreira Salles and the Digital South Asia Library.",
            style = MaterialTheme.typography.bodyLarge)
        AboutCard("Rights") {
            Text("The recordings are hosted by those archives and aren't covered by this app's license. " +
                "Every track was recorded or released in 1970 or earlier, isn't claimed by a rights holder in the " +
                "US Copyright Office's records, and, if made after 1925, isn't on sale or streaming in the music " +
                "stores we check. Songs credited to a writer are included only when every writer is known to have " +
                "died at least 70 years ago; traditional and uncredited songs are included. " +
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
