package nz.oldfolk.ui

import android.text.format.Formatter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nz.oldfolk.AppGraph
import nz.oldfolk.BuildConfig
import nz.oldfolk.data.DownloadStatus
import nz.oldfolk.data.Playlist
import nz.oldfolk.data.Track
import nz.oldfolk.playback.RadioEngine

private enum class Tab(val label: String) { RADIO("Radio"), GENRES("Genres"), PLAYLISTS("Playlists"), HISTORY("History") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OldFolkAppUi(graph: AppGraph, onBeforePlay: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(Tab.RADIO) }
    var openPlaylist by rememberSaveable { mutableStateOf<String?>(null) }
    var addTrack by remember { mutableStateOf<Track?>(null) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showAbout) { showAbout = false }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showAbout) "About" else if (tab == Tab.RADIO) "Old Folk" else tab.label) },
                navigationIcon = {
                    if (showAbout) {
                        IconButton(onClick = { showAbout = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    } else if (tab == Tab.PLAYLISTS && openPlaylist != null) {
                        IconButton(onClick = { openPlaylist = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (!showAbout) {
                        IconButton(onClick = { showAbout = true }) { Icon(Icons.Filled.Info, "About") }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t; showAbout = false; if (t != Tab.PLAYLISTS) openPlaylist = null },
                        icon = {
                            Icon(when (t) {
                                Tab.RADIO -> Icons.Filled.Radio
                                Tab.GENRES -> Icons.Filled.Tune
                                Tab.PLAYLISTS -> Icons.AutoMirrored.Filled.QueueMusic
                                Tab.HISTORY -> Icons.Filled.History
                            }, null)
                        },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (showAbout) AboutScreen(graph) else when (tab) {
                Tab.RADIO -> RadioScreen(graph, onBeforePlay, onAddToPlaylist = { addTrack = it })
                Tab.GENRES -> GenresScreen(graph)
                Tab.PLAYLISTS -> {
                    val id = openPlaylist
                    if (id == null) PlaylistsScreen(graph, onOpen = { openPlaylist = it })
                    else PlaylistDetailScreen(graph, id, onBeforePlay,
                        onPlayed = { tab = Tab.RADIO }, onDeleted = { openPlaylist = null })
                }
                Tab.HISTORY -> HistoryScreen(graph, onBeforePlay)
            }
        }
    }

    addTrack?.let { track ->
        AddToPlaylistDialog(graph, track, onDismiss = { addTrack = null })
    }
}

// ---------------------------------------------------------------------- radio

@Composable
private fun RadioScreen(graph: AppGraph, onBeforePlay: () -> Unit, onAddToPlaylist: (Track) -> Unit) {
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    val genres by graph.settings.genres.collectAsState()
    val context = LocalContext.current
    val track = state.current

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val modeLabel = if (state.mode == RadioEngine.Mode.PLAYLIST) "Playlist: ${state.playlistName}"
        else if (genres.isEmpty()) "Radio: all genres"
        else "Radio: " + genres.joinToString(", ") { list.index?.genreName(it) ?: it }
        Text(modeLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                if (track == null) {
                    Text(state.message ?: "Press play to start", style = MaterialTheme.typography.titleMedium)
                    if (list.loading && list.tracks.isEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                } else {
                    Text(track.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(6.dp))
                    Text(track.artist ?: "Unknown performer", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    InfoLine("Year", track.yearLabel)
                    InfoLine("Region", track.region)
                    InfoLine("Genre", list.index?.genreName(track.genre) ?: track.genre)
                }
            }
        }

        if (track != null && state.message != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.message!!, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(28.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            IconButton(onClick = { onBeforePlay(); graph.engine.previous() }, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Filled.SkipPrevious, "Previous track", Modifier.size(40.dp))
            }
            FilledIconButton(
                onClick = { onBeforePlay(); graph.engine.togglePlay() },
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.filledIconButtonColors(),
            ) {
                if (state.isBuffering) CircularProgressIndicator(Modifier.size(36.dp), strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.onPrimary)
                else Icon(if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    if (state.isPlaying) "Pause" else "Play", Modifier.size(44.dp))
            }
            IconButton(onClick = { onBeforePlay(); graph.engine.skip() }, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Filled.SkipNext, "Next track", Modifier.size(40.dp))
            }
        }

        if (track != null) {
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { Links.open(context, track.detailsUrl) }) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp)); Text("Archive")
                }
                TextButton(onClick = { onAddToPlaylist(track) }) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp)); Text("Save")
                }
                TextButton(onClick = { Links.open(context, Links.reportTrack(track)) }) {
                    Icon(Icons.Filled.Flag, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp)); Text("Report")
                }
            }
        }

        if (state.mode == RadioEngine.Mode.PLAYLIST) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onBeforePlay(); graph.engine.startRadio() }) {
                Icon(Icons.Filled.Radio, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp)); Text("Back to radio")
            }
        }

        if (state.upNext.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text("Up next", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
            state.upNext.forEach { t ->
                Text("${t.title} · ${t.artist ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp))
            }
        }

        if (list.loading && list.tracks.isNotEmpty() && !list.complete) {
            Spacer(Modifier.height(16.dp))
            Text("Downloading the track list in the background (${list.shardsLoaded}/${list.shardsTotal})",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        if (list.error != null && list.tracks.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Button(onClick = { graph.trackList.retry { graph.settings.genres.value } }) { Text("Retry") }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

// ---------------------------------------------------------------------- genres

@Composable
private fun GenresScreen(graph: AppGraph) {
    val list by graph.trackList.state.collectAsState()
    val selected by graph.settings.genres.collectAsState()
    val genres = list.index?.genres.orEmpty().sortedBy { it.name.lowercase() }
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            ListItem(
                headlineContent = { Text("All genres") },
                supportingContent = { Text("${list.index?.genres?.sumOf { it.count } ?: 0} tracks") },
                leadingContent = { Checkbox(checked = selected.isEmpty(), onCheckedChange = { graph.settings.setGenres(emptySet()) }) },
                modifier = Modifier.clickable { graph.settings.setGenres(emptySet()) },
            )
            HorizontalDivider()
        }
        if (genres.isEmpty()) {
            item { Text("The genre list appears once the track list has downloaded.", Modifier.padding(16.dp)) }
        }
        items(genres, key = { it.key }) { g ->
            val checked = g.key in selected
            val toggle = {
                val next = if (checked) selected - g.key else selected + g.key
                graph.settings.setGenres(next)
            }
            ListItem(
                headlineContent = { Text(g.name) },
                supportingContent = { Text("${g.count} tracks") },
                leadingContent = { Checkbox(checked = checked, onCheckedChange = { toggle() }) },
                modifier = Modifier.clickable { toggle() },
            )
        }
    }
}

// ---------------------------------------------------------------------- playlists

@Composable
private fun PlaylistsScreen(graph: AppGraph, onOpen: (String) -> Unit) {
    val playlists by graph.playlists.playlists.collectAsState()
    val downloads by graph.playlists.downloads.collectAsState()
    var creating by remember { mutableStateOf(false) }

    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            Text("Save tracks from the Radio screen. Playlist tracks are downloaded to your phone so they play offline.",
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
            Button(onClick = { creating = true }, modifier = Modifier.padding(horizontal = 16.dp)) { Text("New playlist") }
            Spacer(Modifier.height(8.dp))
        }
        items(playlists, key = { it.id }) { p ->
            val done = p.tracks.count { downloads[it.key] == DownloadStatus.Done }
            ListItem(
                headlineContent = { Text(p.name) },
                supportingContent = { Text("${p.tracks.size} tracks · $done downloaded") },
                leadingContent = { Icon(Icons.Filled.LibraryMusic, null) },
                modifier = Modifier.clickable { onOpen(p.id) },
            )
        }
    }
    if (creating) {
        NameDialog("New playlist", "", onDismiss = { creating = false }) {
            graph.playlists.create(it); creating = false
        }
    }
}

@Composable
private fun PlaylistDetailScreen(
    graph: AppGraph, id: String, onBeforePlay: () -> Unit, onPlayed: () -> Unit, onDeleted: () -> Unit,
) {
    val playlists by graph.playlists.playlists.collectAsState()
    val downloads by graph.playlists.downloads.collectAsState()
    val p = playlists.firstOrNull { it.id == id }
    if (p == null) {
        LaunchedEffect(id) { onDeleted() }
        return
    }
    var renaming by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            Text(p.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 16.dp))
            Row(Modifier.padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = p.tracks.isNotEmpty(), onClick = {
                    onBeforePlay(); graph.engine.playPlaylist(p); onPlayed()
                }) { Icon(Icons.Filled.PlayArrow, null); Text("Play") }
                OutlinedButton(enabled = p.tracks.isNotEmpty(), onClick = {
                    onBeforePlay(); graph.engine.playPlaylist(p, (p.tracks.indices).random(), shuffle = true); onPlayed()
                }) { Icon(Icons.Filled.Shuffle, null); Text("Shuffle") }
                IconButton(onClick = { renaming = true }) { Icon(Icons.Filled.Edit, "Rename") }
                IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, "Delete playlist") }
            }
            if (downloads.values.any { it is DownloadStatus.Failed }) {
                TextButton(onClick = { graph.playlists.retryFailed() }, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text("Retry failed downloads")
                }
            }
            if (p.tracks.isEmpty()) {
                Text("No tracks yet. Use \"Save\" on the Radio screen.", Modifier.padding(16.dp))
            }
        }
        itemsIndexed(p.tracks, key = { _, t -> t.key }) { index, t ->
            ListItem(
                headlineContent = { Text(t.title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                supportingContent = {
                    Column {
                        Text("${t.artist ?: "Unknown"} · ${t.yearLabel}")
                        DownloadLabel(downloads[t.key])
                    }
                },
                leadingContent = { DownloadIcon(downloads[t.key]) },
                trailingContent = {
                    IconButton(onClick = { graph.playlists.remove(p.id, t) }) { Icon(Icons.Filled.Delete, "Remove") }
                },
                modifier = Modifier.clickable { onBeforePlay(); graph.engine.playPlaylist(p, index); onPlayed() },
            )
        }
    }
    if (renaming) {
        NameDialog("Rename playlist", p.name, onDismiss = { renaming = false }) {
            graph.playlists.rename(p.id, it); renaming = false
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete \"${p.name}\"?") },
            text = { Text("Its downloaded tracks will be removed from your phone (unless another playlist has them).") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; graph.playlists.delete(p.id); onDeleted() }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun DownloadLabel(s: DownloadStatus?) {
    when (s) {
        is DownloadStatus.Failed -> Text("Download failed: ${s.reason}", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error)
        is DownloadStatus.Downloading -> LinearProgressIndicator(progress = { s.progress },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        else -> {}
    }
}

@Composable
private fun DownloadIcon(s: DownloadStatus?) {
    when (s) {
        DownloadStatus.Done -> Icon(Icons.Filled.CheckCircle, "Downloaded", tint = MaterialTheme.colorScheme.primary)
        is DownloadStatus.Failed -> Icon(Icons.Filled.ErrorOutline, "Download failed", tint = MaterialTheme.colorScheme.error)
        else -> Icon(Icons.Filled.CloudDownload, "Waiting to download")
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
                        supportingContent = { Text("${p.tracks.size} tracks") },
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

// ---------------------------------------------------------------------- about

@Composable
private fun AboutScreen(graph: AppGraph) {
    val context = LocalContext.current
    val failures by graph.failures.items.collectAsState()
    val state by graph.engine.state.collectAsState()
    val list by graph.trackList.state.collectAsState()
    var confirmReset by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Old Folk plays public-domain folk and field recordings from around the world, streamed directly from the Internet Archive.",
            style = MaterialTheme.typography.bodyLarge)
        Text("The recordings are hosted by the Internet Archive and are not covered by this app's license. " +
            "They were selected because they are believed to be in the public domain in New Zealand " +
            "(recorded or released in 1970 or earlier). Their status may differ in other countries. " +
            "Rights holders can request removal at thehowardsmith@gmail.com.",
            style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)

        HorizontalDivider()
        Text("Track list", style = MaterialTheme.typography.titleMedium)
        Text("Version ${list.index?.version ?: "-"} · ${list.tracks.size} tracks loaded " +
            "(${list.shardsLoaded}/${list.shardsTotal} parts)")
        Text("You've heard ${state.playedCount} tracks. The radio won't repeat a track until you've heard everything in your selection.")
        OutlinedButton(onClick = { confirmReset = true }) { Text("Reset listening history") }

        HorizontalDivider()
        Text("Problems", style = MaterialTheme.typography.titleMedium)
        if (failures.isEmpty()) {
            Text("No tracks have failed on this phone.")
        } else {
            Text("${failures.size} track(s) failed to play or download on this phone. " +
                "Sending them helps check whether the file is really gone or it was a connection or app problem.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    Links.open(context, Links.reportFailures(graph.failures.reportBody(BuildConfig.VERSION_NAME)))
                }) { Text("Send report") }
                OutlinedButton(onClick = { graph.failures.clear() }) { Text("Clear") }
            }
            Text("Sending opens a pre-filled GitHub issue (needs a GitHub account).", style = MaterialTheme.typography.bodySmall)
        }

        HorizontalDivider()
        Text("Storage", style = MaterialTheme.typography.titleMedium)
        Text("Streaming cache: ${Formatter.formatShortFileSize(context, graph.mediaCache.usedBytes())} " +
            "(max ${Formatter.formatShortFileSize(context, nz.oldfolk.playback.MediaCache.MAX_BYTES)}). " +
            "Holds your last 20 tracks and the next 20.")
        Text("Playlist downloads: ${Formatter.formatShortFileSize(context, graph.playlists.storageBytes())}")

        HorizontalDivider()
        TextButton(onClick = { Links.open(context, Links.repoUrl) }) { Text("Project on GitHub") }
        Text("App version ${BuildConfig.VERSION_NAME}. Code: Apache-2.0. Track list: CC0.",
            style = MaterialTheme.typography.bodySmall)
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset listening history?") },
            text = { Text("Clears the History tab, and tracks you've already heard can come up again.") },
            confirmButton = { TextButton(onClick = {
                graph.history.clear(); graph.streamHistory.clear(); confirmReset = false
            }) { Text("Reset") } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } },
        )
    }
}


// ---------------------------------------------------------------------- history

private sealed interface HistoryRow {
    data class Past(val track: Track, val time: Long) : HistoryRow
    data class Now(val track: Track) : HistoryRow
    data class Ahead(val track: Track, val queueIndex: Int) : HistoryRow
}

/**
 * Everything streamed on this phone (scroll up), the current track, and the
 * tracks queued ahead. Recent and upcoming tracks are kept in the cache; older
 * ones stream again when tapped.
 */
@Composable
private fun HistoryScreen(graph: AppGraph, onBeforePlay: () -> Unit) {
    val entries by graph.streamHistory.entries.collectAsState()
    val state by graph.engine.state.collectAsState()
    val current = state.current
    val past = if (current != null && entries.lastOrNull()?.track?.key == current.key) entries.dropLast(1) else entries
    val rows = buildList<HistoryRow> {
        past.forEach { add(HistoryRow.Past(it.track, it.time)) }
        current?.let { add(HistoryRow.Now(it)) }
        if (state.currentIndex >= 0) {
            state.queue.forEachIndexed { i, t -> if (i > state.currentIndex) add(HistoryRow.Ahead(t, i)) }
        }
    }
    if (rows.isEmpty()) {
        Text("Tracks you play will appear here.", Modifier.padding(16.dp))
        return
    }
    // Open with the current track near the top: history is above, tracks ahead below.
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (past.size - 1).coerceAtLeast(0))
    val fmt = remember { java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT) }

    LazyColumn(state = listState, contentPadding = PaddingValues(vertical = 8.dp)) {
        itemsIndexed(rows, key = { i, _ -> i }) { _, row ->
            when (row) {
                is HistoryRow.Past -> HistoryItem(graph, row.track, fmt.format(java.util.Date(row.time)), highlight = false) {
                    onBeforePlay(); graph.engine.playTrack(row.track)
                }
                is HistoryRow.Now -> {
                    HorizontalDivider()
                    HistoryItem(graph, row.track, if (state.isPlaying) "Now playing" else "Current track", highlight = true) {
                        onBeforePlay(); graph.engine.togglePlay()
                    }
                    if (state.queue.size > state.currentIndex + 1) {
                        HorizontalDivider()
                        Text("Up next", style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
                is HistoryRow.Ahead -> HistoryItem(graph, row.track, null, highlight = false) {
                    onBeforePlay(); graph.engine.playQueued(row.queueIndex)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(graph: AppGraph, t: Track, label: String?, highlight: Boolean, onClick: () -> Unit) {
    val saved = remember(t.key) { graph.playlists.isDownloaded(t) || graph.mediaCache.isFullyCached(t.streamUrl) }
    ListItem(
        headlineContent = {
            Text(t.title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                fontWeight = if (highlight) FontWeight.Bold else null)
        },
        supportingContent = {
            Text(listOfNotNull(t.artist ?: "Unknown", t.yearLabel, t.region, label).joinToString(" · "),
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        leadingContent = {
            when {
                highlight -> Icon(Icons.Filled.Equalizer, "Current", tint = MaterialTheme.colorScheme.primary)
                saved -> Icon(Icons.Filled.OfflinePin, "Saved on this phone", tint = MaterialTheme.colorScheme.primary)
                else -> Icon(Icons.Filled.CloudDownload, "Streams when played")
            }
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(if (highlight) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) else Modifier),
    )
}
