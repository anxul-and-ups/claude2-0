package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.repository.NoteRepository
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.util.HtmlTools
import com.example.ui.util.ImageMarkers
import kotlinx.coroutines.delay

private enum class HtmlMode { EDIT, PREVIEW, HIGHLIGHT }

private const val PREFS = "au_html_viewer"
private const val MAX_IMPORT_BYTES = 4 * 1024 * 1024

/**
 * HTML viewer: a line-numbered editor plus Preview (full page), Highlight (full page, foldable),
 * Format, Expand / Collapse all, Sample, Clear, Import from file / notes, Export to file and a
 * viewer-only dark mode. The draft is kept between app launches.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlViewerScreen(
    repository: NoteRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var text by remember { mutableStateOf(prefs.getString("draft", "") ?: "") }
    var viewerDark by remember { mutableStateOf(prefs.getBoolean("dark", true)) }
    var mode by remember { mutableStateOf(HtmlMode.EDIT) }
    var panelOpen by remember { mutableStateOf(false) }
    var collapsed by remember { mutableStateOf(setOf<Int>()) }
    var showNotePicker by remember { mutableStateOf(false) }
    var pendingNoteText by remember { mutableStateOf<String?>(null) }
    var confirmClear by remember { mutableStateOf(false) }

    // Debounced draft save: one write after typing pauses, never one per keystroke
    LaunchedEffect(text) {
        delay(600)
        prefs.edit().putString("draft", text).apply()
    }

    val bg = if (viewerDark) Color(0xFF1B1D22) else Color(0xFFF2F3F5)
    val gutterBg = if (viewerDark) Color(0xFF23262C) else Color(0xFFE6E8EB)
    val fg = if (viewerDark) Color(0xFFE6E6E6) else Color(0xFF1A1A1A)
    val muted = if (viewerDark) Color(0xFF7D8590) else Color(0xFF8A8F98)
    val panelBg = Color(0xFF303236)

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
                if (bytes.size > MAX_IMPORT_BYTES) {
                    Toast.makeText(context, "File is too large (max 4 MB)", Toast.LENGTH_SHORT).show()
                } else {
                    text = bytes.toString(Charsets.UTF_8)
                    collapsed = emptySet()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not read file", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/html")) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray(Charsets.UTF_8)) }
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not save file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler(enabled = mode != HtmlMode.EDIT || panelOpen) {
        if (panelOpen) panelOpen = false else mode = HtmlMode.EDIT
    }

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
            // ---- top bar ----
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (mode != HtmlMode.EDIT) mode = HtmlMode.EDIT else onBack() }) {
                    Icon(
                        if (mode == HtmlMode.EDIT) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                        "Back", tint = fg
                    )
                }
                Text(
                    when (mode) {
                        HtmlMode.EDIT -> "HTML Viewer"
                        HtmlMode.PREVIEW -> "Preview"
                        HtmlMode.HIGHLIGHT -> "Highlight"
                    },
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = fg, modifier = Modifier.weight(1f)
                )
                if (mode == HtmlMode.EDIT) {
                    IconButton(onClick = { panelOpen = !panelOpen }) { Icon(Icons.Default.Menu, "Tools", tint = fg) }
                }
            }

            when (mode) {
                HtmlMode.EDIT -> {
                    val lineCount = remember(text) { text.count { it == '\n' } + 1 }
                    val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp, color = fg)
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    ) {
                        // line-number gutter (no soft wrap, so numbers always line up with the text)
                        Box(modifier = Modifier.background(gutterBg).padding(horizontal = 8.dp, vertical = 8.dp)) {
                            Text(
                                text = remember(lineCount) { (1..lineCount).joinToString("\n") },
                                style = style.copy(color = muted),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                        Box(modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()).padding(8.dp)) {
                            BasicTextField(
                                value = text,
                                onValueChange = { text = it },
                                textStyle = style,
                                cursorBrush = SolidColor(CrimsonPrimary),
                                softWrap = false,
                                modifier = Modifier.widthIn(min = 600.dp).height((lineCount * 19 + 40).dp.coerceAtLeast(300.dp)),
                                decorationBox = { inner ->
                                    if (text.isEmpty()) Text("Put your HTML text here", style = style.copy(color = muted))
                                    inner()
                                }
                            )
                        }
                    }
                }

                HtmlMode.PREVIEW -> {
                    AndroidView(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.allowFileAccess = false
                                settings.allowContentAccess = false
                                webViewClient = WebViewClient()
                                loadDataWithBaseURL("about:blank", text, "text/html", "utf-8", null)
                            }
                        },
                        onRelease = { it.destroy() }
                    )
                }

                HtmlMode.HIGHLIGHT -> {
                    val palette = if (viewerDark) HtmlTools.darkPalette else HtmlTools.lightPalette
                    val styled = remember(text, viewerDark) { HtmlTools.highlight(text, palette) }
                    val regions = remember(text) { HtmlTools.foldRegions(text) }
                    val lines = remember(text) { text.split("\n") }
                    val starts = remember(text) {
                        val a = IntArray(lines.size); var acc = 0
                        lines.forEachIndexed { i, l -> a[i] = acc; acc += l.length + 1 }; a
                    }
                    val style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp, color = fg)
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp)
                    ) {
                        var i = 0
                        while (i < lines.size) {
                            val end = regions[i]
                            val isFold = end != null
                            val isCollapsed = isFold && i in collapsed
                            val lineIndex = i
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    "${i + 1}", style = style.copy(color = muted),
                                    modifier = Modifier.width(44.dp).padding(end = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                                Box(
                                    modifier = Modifier.width(18.dp).clickable(enabled = isFold) {
                                        collapsed = if (lineIndex in collapsed) collapsed - lineIndex else collapsed + lineIndex
                                    }
                                ) {
                                    if (isFold) Text(if (isCollapsed) "▸" else "▾", style = style.copy(color = muted))
                                }
                                val from = starts[i]
                                val to = (from + lines[i].length).coerceAtMost(styled.length)
                                Text(
                                    text = if (from <= to) styled.subSequence(from, to) else androidx.compose.ui.text.AnnotatedString(""),
                                    style = style, softWrap = false
                                )
                                if (isCollapsed) Text("  ⋯ ", style = style.copy(color = muted))
                            }
                            i = if (isCollapsed) (end ?: i) + 1 else i + 1
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }

        // ---- slide-in tool panel (same list as the original web viewer) ----
        if (panelOpen && mode == HtmlMode.EDIT) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0x66000000)).clickable { panelOpen = false }
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(270.dp)
                    .background(panelBg)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    "HTML Viewer", color = Color.White.copy(0.9f), fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 6.dp)
                )
                PanelButton(Icons.Default.Visibility, "Preview (Full page)") { panelOpen = false; mode = HtmlMode.PREVIEW }
                PanelButton(Icons.Default.Code, "Highlight (Full page)") { panelOpen = false; collapsed = emptySet(); mode = HtmlMode.HIGHLIGHT }
                PanelButton(Icons.Default.FormatAlignLeft, "Format") {
                    text = HtmlTools.format(text); collapsed = emptySet(); panelOpen = false
                }
                PanelButton(Icons.Default.UnfoldMore, "Expand all") {
                    collapsed = emptySet(); panelOpen = false; mode = HtmlMode.HIGHLIGHT
                }
                PanelButton(Icons.Default.UnfoldLess, "Collapse all") {
                    collapsed = HtmlTools.foldRegions(text).keys; panelOpen = false; mode = HtmlMode.HIGHLIGHT
                }
                PanelButton(Icons.Default.Description, "Sample") { text = HtmlTools.sample; collapsed = emptySet(); panelOpen = false }
                PanelButton(Icons.Default.DeleteSweep, "Clear") { panelOpen = false; if (text.isNotEmpty()) confirmClear = true }
                PanelButton(Icons.Default.FileOpen, "Import from file") {
                    panelOpen = false
                    importLauncher.launch(arrayOf("text/html", "text/plain", "application/xhtml+xml", "*/*"))
                }
                PanelButton(Icons.Default.StickyNote2, "Import from notes") { panelOpen = false; showNotePicker = true }
                PanelButton(Icons.Default.Save, "Export to file") { panelOpen = false; exportLauncher.launch("page.html") }
                PanelButton(if (viewerDark) Icons.Default.LightMode else Icons.Default.DarkMode, if (viewerDark) "Light mode" else "Dark mode") {
                    viewerDark = !viewerDark
                    prefs.edit().putBoolean("dark", viewerDark).apply()
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear editor?") },
            text = { Text("All text in the HTML editor will be removed.") },
            confirmButton = {
                TextButton(onClick = { text = ""; collapsed = emptySet(); confirmClear = false }) {
                    Text("Clear", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } }
        )
    }

    if (showNotePicker) {
        val notes by repository.allActiveNotes.collectAsState(initial = emptyList())
        var query by remember { mutableStateOf("") }
        val shown = remember(notes, query) {
            notes.filter { query.isBlank() || it.title.contains(query, true) || it.content.contains(query, true) }.take(60)
        }
        AlertDialog(
            onDismissRequest = { showNotePicker = false },
            title = { Text("Import from notes") },
            text = {
                Column {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it }, singleLine = true,
                        label = { Text("Search notes") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.height(280.dp).verticalScroll(rememberScrollState())) {
                        if (shown.isEmpty()) Text("No notes found", color = Color.Gray, fontSize = 13.sp)
                        shown.forEach { note ->
                            Column(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        val body = ImageMarkers.strip(note.content)
                                        showNotePicker = false
                                        if (text.isBlank()) { text = body; collapsed = emptySet() } else pendingNoteText = body
                                    }
                                    .padding(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Text(note.title.ifBlank { "Untitled" }, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    ImageMarkers.strip(note.content).take(70).replace("\n", " "),
                                    fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showNotePicker = false }) { Text("Cancel") } }
        )
    }

    pendingNoteText?.let { body ->
        AlertDialog(
            onDismissRequest = { pendingNoteText = null },
            title = { Text("Editor already has text") },
            text = { Text("Replace it with the note, or add the note below it?") },
            confirmButton = {
                TextButton(onClick = { text = body; collapsed = emptySet(); pendingNoteText = null }) { Text("Replace", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { pendingNoteText = null }) { Text("Cancel") }
                    TextButton(onClick = { text = text.trimEnd() + "\n" + body; collapsed = emptySet(); pendingNoteText = null }) { Text("Append") }
                }
            }
        )
    }
}

@Composable
private fun PanelButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFDDDDDD))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF333333), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = Color(0xFF222222), fontSize = 14.sp)
    }
}
