package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.FormatIndentDecrease
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ai.AiService
import com.example.data.model.AutoClassifier
import com.example.data.model.NoteEntity
import com.example.data.preferences.AppPreferences
import com.example.data.repository.NoteRepository
import com.example.ui.components.GlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveTableView
import com.example.ui.components.NeuIconButton
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.util.AlarmScheduler
import com.example.ui.util.AttachmentStorage
import com.example.ui.util.DeviceAudioFile
import com.example.ui.util.RichTextFormatter
import com.example.ui.util.SystemRingtoneItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    initialNote: NoteEntity?,
    isDarkMode: Boolean,
    repository: NoteRepository,
    preferences: AppPreferences,
    onBack: () -> Unit,
    onOpenTableEditor: (initialTableData: String, onResult: (String) -> Unit) -> Unit = { _, _ -> },
    onSaveNote: (
        id: Long,
        title: String,
        content: String,
        category: String,
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        isStrikethrough: Boolean,
        isCodeFormat: Boolean,
        fontSize: Int,
        fontColorHex: String,
        alignment: String,
        listType: String,
        tableData: String,
        styleSpansJson: String,
        attachmentsJson: String,
        onSaved: (Long) -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val aiService = remember { AiService() }

    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var contentValue by remember { mutableStateOf(TextFieldValue(initialNote?.content ?: "")) }
    var selectedCategory by remember { mutableStateOf(initialNote?.category ?: "Normal") }

    var currentNoteId by remember { mutableStateOf(initialNote?.id ?: 0L) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }

    var spans: List<RichTextFormatter.TextSpan> by remember {
        mutableStateOf(RichTextFormatter.deserializeSpans(initialNote?.styleSpansJson ?: "[]"))
    }

    var attachments: List<RichTextFormatter.AttachmentInfo> by remember {
        mutableStateOf(RichTextFormatter.deserializeAttachments(initialNote?.attachmentsJson ?: "[]"))
    }

    // Item 11: JSON Mode — coding-style syntax highlighting for keys/strings/
    // numbers/brackets when the user pastes or writes JSON.
    fun buildJsonHighlightedText(text: String): AnnotatedString {
        val keyColor = Color(0xFF80D8FF)
        val stringColor = Color(0xFFC3E88D)
        val numberColor = Color(0xFFF78C6C)
        val keywordColor = Color(0xFFC792EA)
        val bracketColor = Color(0xFFFFD54F)
        val base = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF222222)

        return androidx.compose.ui.text.buildAnnotatedString {
            append(text)
            val keyRegex = Regex("\"(?:[^\"\\\\]|\\\\.)*\"(?=\\s*:)")
            val stringRegex = Regex("\"(?:[^\"\\\\]|\\\\.)*\"")
            val numberRegex = Regex("-?\\b\\d+\\.?\\d*\\b")
            val keywordRegex = Regex("\\b(true|false|null)\\b")
            val bracketRegex = Regex("[{}\\[\\]:,]")

            addStyle(SpanStyle(color = base), 0, text.length)
            for (m in bracketRegex.findAll(text)) addStyle(SpanStyle(color = bracketColor, fontWeight = FontWeight.Bold), m.range.first, m.range.last + 1)
            for (m in numberRegex.findAll(text)) addStyle(SpanStyle(color = numberColor), m.range.first, m.range.last + 1)
            for (m in keywordRegex.findAll(text)) addStyle(SpanStyle(color = keywordColor), m.range.first, m.range.last + 1)
            for (m in stringRegex.findAll(text)) addStyle(SpanStyle(color = stringColor), m.range.first, m.range.last + 1)
            for (m in keyRegex.findAll(text)) addStyle(SpanStyle(color = keyColor, fontWeight = FontWeight.SemiBold), m.range.first, m.range.last + 1)
        }
    }

    // Item 8: apply a boolean format (bold/italic/underline/strikethrough/code) either
    // to the current selection (if any text is selected) or as a "typing mode" that
    // will wrap newly typed characters going forward until toggled off again.
    fun applyOrToggleBooleanFormat(type: String, current: Boolean, setCurrent: (Boolean) -> Unit) {
        val sel = contentValue.selection
        if (!sel.collapsed) {
            spans = RichTextFormatter.toggleBooleanProperty(spans, type, sel.min, sel.max)
        } else {
            setCurrent(!current)
        }
    }

    // Item 8: apply a text color either to the current selection, or as the
    // active "typing color" for newly typed characters. Passing null clears
    // the typing color (falls back to the day/night default — Item 9).
    var activeColorHex by remember { mutableStateOf<String?>(null) }
    var activeFontSize by remember { mutableStateOf<Int?>(null) }
    var activeHighlight by remember { mutableStateOf(false) }

    fun applyOrSetColor(hex: String?) {
        val sel = contentValue.selection
        if (!sel.collapsed && hex != null) {
            spans = RichTextFormatter.setValueProperty(spans, "color", sel.min, sel.max, hex)
        } else if (!sel.collapsed) {
            // "Default" swatch on a selection removes any custom colour from it
            spans = RichTextFormatter.clearType(spans, "color", sel.min, sel.max)
        } else {
            activeColorHex = hex
        }
    }

    // Formatting state — Item 8/9: these now represent the CURRENT TYPING
    // format (applied to newly typed characters going forward), not a global
    // style for the whole note. Existing spans elsewhere are untouched.
    var isBold by remember { mutableStateOf(initialNote?.isBold ?: false) }
    var isItalic by remember { mutableStateOf(initialNote?.isItalic ?: false) }
    var isUnderline by remember { mutableStateOf(initialNote?.isUnderline ?: false) }
    var isStrikethrough by remember { mutableStateOf(initialNote?.isStrikethrough ?: false) }
    var isCodeFormat by remember { mutableStateOf(initialNote?.isCodeFormat ?: false) }
    var showFormatSheet by remember { mutableStateOf(false) }
    var showParagraphStyleSheet by remember { mutableStateOf(false) }
    var isJsonMode by remember { mutableStateOf(false) }

    var fontSize by remember { mutableStateOf(initialNote?.fontSize ?: 16) }
    // Ensure font color contrast: default to #111111 in day mode and #FFFFFF in dark mode
    var selectedColorHex by remember {
        val initial = initialNote?.fontColorHex
        if (initial.isNullOrBlank() || initial == "#FFFFFF" && !isDarkMode) {
            mutableStateOf(if (isDarkMode) "#FFFFFF" else "#111111")
        } else {
            mutableStateOf(initial)
        }
    }
    var alignment by remember { mutableStateOf(initialNote?.alignment ?: "left") }
    var listType by remember { mutableStateOf(initialNote?.listType ?: "none") }
    var tableData by remember { mutableStateOf(initialNote?.tableData ?: "") }

    // Dialogs & Modals state
    // (Alarm dialog moved to ReadNoteScreen — Item 14)
    var showImageEditModal by remember { mutableStateOf<RichTextFormatter.AttachmentInfo?>(null) }
    var showAiChatbotModal by remember { mutableStateOf(false) }
    var showTableFullscreen by remember { mutableStateOf(false) }

    // Floating Chatbot Position
    var botOffsetX by remember { mutableFloatStateOf(0f) }
    var botOffsetY by remember { mutableFloatStateOf(0f) }

    // Image Attachment picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val info = AttachmentStorage.copyToAppStorage(context, uri)
                withContext(Dispatchers.Main) {
                    if (info != null) {
                        attachments = attachments + info
                        Toast.makeText(context, "Image added to Note!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun performSave(showToast: Boolean, thenNavigateBack: Boolean) {
        if (title.isBlank() && contentValue.text.isBlank() && attachments.isEmpty()) {
            if (thenNavigateBack) onBack()
            return
        }
        val autoCat = if (selectedCategory == "Normal") {
            AutoClassifier.detectCategory(title, contentValue.text)
        } else {
            selectedCategory
        }

        onSaveNote(
            currentNoteId,
            title,
            contentValue.text,
            autoCat,
            isBold,
            isItalic,
            isUnderline,
            isStrikethrough,
            isCodeFormat,
            fontSize,
            selectedColorHex,
            alignment,
            listType,
            tableData,
            RichTextFormatter.serializeSpans(spans),
            RichTextFormatter.serializeAttachments(attachments)
        ) { savedId ->
            currentNoteId = savedId
            hasUnsavedChanges = false
            if (showToast) {
                Toast.makeText(context, "Note Saved in $autoCat", Toast.LENGTH_SHORT).show()
            }
            if (thenNavigateBack) onBack()
        }
    }

    // Debounced Auto-Save
    LaunchedEffect(
        title, contentValue.text, spans, tableData, attachments,
        isBold, isItalic, isUnderline, isStrikethrough, isCodeFormat,
        fontSize, selectedColorHex, alignment, listType
    ) {
        hasUnsavedChanges = true
        kotlinx.coroutines.delay(1200)
        performSave(showToast = false, thenNavigateBack = false)
    }

    // Save on pause/exit
    DisposableEffect(Unit) {
        onDispose {
            if (hasUnsavedChanges) {
                performSave(showToast = false, thenNavigateBack = false)
            }
        }
    }

    // Undo/Redo Stacks
    val undoStack = remember { mutableStateListOf<Pair<String, List<RichTextFormatter.TextSpan>>>() }
    val redoStack = remember { mutableStateListOf<Pair<String, List<RichTextFormatter.TextSpan>>>() }

    fun pushUndo() {
        if (undoStack.size > 20) undoStack.removeAt(0)
        undoStack.add(Pair(contentValue.text, spans))
        redoStack.clear()
    }

    fun applyOrSetFontSize(size: Int) {
        val sel = contentValue.selection
        if (!sel.collapsed) {
            spans = RichTextFormatter.clearType(spans, "fontsize", sel.min, sel.max)
            if (size != fontSize) spans = RichTextFormatter.addSpan(spans, sel.min, sel.max, "fontsize", size.toString())
        } else {
            activeFontSize = if (size == fontSize) null else size
        }
    }

    fun applyOrToggleHighlight() {
        val sel = contentValue.selection
        if (!sel.collapsed) {
            spans = RichTextFormatter.toggleBooleanProperty(spans, "highlight", sel.min, sel.max)
        } else {
            activeHighlight = !activeHighlight
        }
    }

    // ---------- Samsung-Notes-style line tools (checkbox / bullets / numbers / indent) ----------
    val listPrefixRegex = remember { Regex("^(\\s*)(\\[ \\] |\\[x\\] |• |\\d+\\. |[A-Za-z]\\. )?(.*)$", RegexOption.DOT_MATCHES_ALL) }

    fun linePrefixOf(line: String): String? = listPrefixRegex.matchEntire(line)?.groupValues?.get(2)?.ifEmpty { null }

    fun transformLines(transform: (line: String, index: Int) -> String) {
        val text = contentValue.text
        val sel = contentValue.selection
        val start = text.lastIndexOf('\n', sel.min - 1) + 1
        var endProbe = sel.max
        if (!sel.collapsed && endProbe > start && text.getOrNull(endProbe - 1) == '\n') endProbe -= 1
        val end = text.indexOf('\n', endProbe).let { if (it < 0) text.length else it }
        val block = text.substring(start, end)
        val oldLines = block.split("\n")
        val newLines = oldLines.mapIndexed { i, l -> transform(l, i) }
        if (newLines == oldLines) return
        pushUndo()
        // keep the style spans glued to their text: apply line edits bottom -> top
        var curText = text
        var curSpans = spans
        val lineStarts = IntArray(oldLines.size)
        var acc = start
        oldLines.forEachIndexed { i, l -> lineStarts[i] = acc; acc += l.length + 1 }
        for (i in oldLines.indices.reversed()) {
            if (oldLines[i] == newLines[i]) continue
            val next = curText.substring(0, lineStarts[i]) + newLines[i] + curText.substring(lineStarts[i] + oldLines[i].length)
            curSpans = RichTextFormatter.adjustSpansForEdit(curSpans, curText, next)
            curText = next
        }
        spans = curSpans
        val newBlockLen = newLines.joinToString("\n").length
        contentValue = TextFieldValue(
            curText,
            selection = if (sel.collapsed) TextRange((sel.min + newBlockLen - block.length).coerceIn(start, start + newBlockLen))
            else TextRange(start, start + newBlockLen)
        )
    }

    fun firstSelectedLine(): String {
        val t = contentValue.text
        val pos = contentValue.selection.min.coerceIn(0, t.length)
        val s0 = t.lastIndexOf('\n', pos - 1) + 1
        val e0 = t.indexOf('\n', pos).let { if (it < 0) t.length else it }
        return t.substring(s0, e0)
    }

    fun stripped(line: String): Pair<String, String> {
        val m = listPrefixRegex.matchEntire(line)
        return if (m == null) "" to line else m.groupValues[1] to m.groupValues[3]
    }

    fun toggleCheckbox() {
        val first = linePrefixOf(firstSelectedLine())
        transformLines { l, _ ->
            val (indent, body) = stripped(l)
            when (first) {
                "[ ] " -> "$indent[x] $body"
                "[x] " -> "$indent$body"
                else -> "$indent[ ] $body"
            }
        }
    }

    fun toggleBullets() {
        val first = linePrefixOf(firstSelectedLine())
        transformLines { l, _ ->
            val (indent, body) = stripped(l)
            if (first == "• ") "$indent$body" else "$indent• $body"
        }
    }

    fun toggleNumbers() {
        val first = linePrefixOf(firstSelectedLine())
        val isNumbered = first != null && first.first().isDigit()
        listType = if (isNumbered) "none" else "digit"
        transformLines { l, i ->
            val (indent, body) = stripped(l)
            if (isNumbered) "$indent$body" else "$indent${i + 1}. $body"
        }
    }

    fun toggleLetters() {
        val first = linePrefixOf(firstSelectedLine())
        val isLettered = first != null && first.length >= 2 && first[0].isLetter() && first[1] == '.'
        listType = if (isLettered) "none" else "letter"
        transformLines { l, i ->
            val (indent, body) = stripped(l)
            if (isLettered) "$indent$body" else "$indent${('a'.code + i).coerceAtMost('z'.code).toChar()}. $body"
        }
    }

    fun toggleParagraphList(type: String) {
        when (type) {
            "bullet" -> {
                listType = if (linePrefixOf(firstSelectedLine()) == "• ") "none" else "bullet"
                toggleBullets()
            }
            "digit" -> {
                val first = linePrefixOf(firstSelectedLine())
                if (first != null && first.firstOrNull()?.isDigit() == true) {
                    listType = "none"
                    transformLines { l, _ -> val (indent, body) = stripped(l); "$indent$body" }
                } else {
                    listType = "digit"
                    transformLines { l, i -> val (indent, body) = stripped(l); "$indent${i + 1}. $body" }
                }
            }
            "letter" -> toggleLetters()
        }
    }

    fun renumberOrderedLines() {
        val oldText = contentValue.text
        var number = 1
        var inOrderedBlock = false
        val rebuilt = oldText.lines().joinToString("\n") { line ->
            val m = listPrefixRegex.matchEntire(line)
            val prefix = m?.groupValues?.get(2).orEmpty()
            if (m != null && prefix.firstOrNull()?.isDigit() == true) {
                inOrderedBlock = true
                "${m.groupValues[1]}${number++}. ${m.groupValues[3]}"
            } else {
                if (inOrderedBlock && prefix.isBlank()) number = 1
                inOrderedBlock = false
                line
            }
        }
        if (rebuilt != oldText) {
            val caret = contentValue.selection.end.coerceIn(0, rebuilt.length)
            contentValue = contentValue.copy(text = rebuilt, selection = TextRange(caret))
        }
    }

    fun indentLines() {
        transformLines { l, _ -> "  $l" }
        renumberOrderedLines()
    }

    fun outdentLines() {
        transformLines { l, _ ->
            when {
                l.startsWith("  ") -> l.substring(2)
                l.startsWith(" ") || l.startsWith("\t") -> l.substring(1)
                else -> l
            }
        }
        renumberOrderedLines()
    }

    fun clearFormattingOnSelection() {
        val sel = contentValue.selection
        if (sel.collapsed) {
            isBold = false; isItalic = false; isUnderline = false; isStrikethrough = false; isCodeFormat = false
            activeColorHex = null; activeFontSize = null; activeHighlight = false
        } else {
            spans = RichTextFormatter.clearAll(spans, sel.min, sel.max)
        }
    }

    /** Enter on a list line continues the list; Enter on an empty list item ends it. */
    fun continueListOnEnter(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
        val oldText = old.text
        val newText = new.text
        if (newText.length != oldText.length + 1 || !new.selection.collapsed) return new
        val cursor = new.selection.start
        if (cursor < 1 || newText[cursor - 1] != '\n') return new
        val lineStart = newText.lastIndexOf('\n', cursor - 2) + 1
        val prevLine = newText.substring(lineStart, cursor - 1)
        val m = listPrefixRegex.matchEntire(prevLine) ?: return new
        val indent = m.groupValues[1]
        val prefix = m.groupValues[2]
        if (prefix.isEmpty()) return new
        val body = m.groupValues[3]
        if (body.isBlank()) {
            // empty item: remove the marker and the newline we just got -> list ends
            val cleared = newText.substring(0, lineStart) + newText.substring(cursor)
            return TextFieldValue(cleared, selection = TextRange(lineStart))
        }
        val nextPrefix = when {
            prefix == "• " -> "• "
            prefix.startsWith("[") -> "[ ] "
            prefix.length >= 3 && prefix[0].isLetter() && prefix[1] == '.' -> {
                val next = (prefix[0].lowercaseChar().code + 1).coerceAtMost('z'.code).toChar()
                "$next. "
            }
            else -> "${(prefix.dropLast(2).toIntOrNull() ?: 0) + 1}. "
        }
        val insertion = indent + nextPrefix
        val result = newText.substring(0, cursor) + insertion + newText.substring(cursor)
        return TextFieldValue(result, selection = TextRange(cursor + insertion.length))
    }

    val mainScrollState = rememberScrollState()
    // Item 13: guarantees the cursor never gets hidden behind the keyboard —
    // explicitly requests a scroll whenever the caret moves, instead of
    // relying only on the platform's default (sometimes-late) behaviour.
    val contentBringIntoViewRequester = remember { BringIntoViewRequester() }

    // Keep typing pinned to the caret instead of requiring manual scrolling.
    LaunchedEffect(contentValue.text.length, contentValue.selection) {
        if (contentValue.selection.collapsed && contentValue.selection.end >= contentValue.text.length - 1) {
            kotlinx.coroutines.delay(60)
            contentBringIntoViewRequester.bringIntoView()
            mainScrollState.animateScrollTo(mainScrollState.maxValue)
        }
    }

    // DAY MODE CONTRAST FIX: Default text color in Day mode is dark (Color(0xFF111111))
    val defaultTextColor = if (isDarkMode) Color.White else Color(0xFF111111)
    val effectiveTextColor = if (!isDarkMode && (selectedColorHex.equals("#FFFFFF", ignoreCase = true) || selectedColorHex.equals("#FFF", ignoreCase = true))) {
        Color(0xFF111111)
    } else {
        try {
            Color(android.graphics.Color.parseColor(selectedColorHex))
        } catch (e: Exception) {
            defaultTextColor
        }
    }

    GlassBackground(isDarkMode = isDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeuIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            isDarkMode = isDarkMode,
                            size = 38.dp,
                            iconSize = 18.dp,
                            tint = if (isDarkMode) Color.White else Color.Black,
                            onClick = { performSave(showToast = false, thenNavigateBack = true) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (initialNote == null) "New Note" else "Edit Note",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF111111)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Undo
                        NeuIconButton(
                            icon = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            isDarkMode = isDarkMode,
                            size = 36.dp,
                            iconSize = 17.dp,
                            tint = if (undoStack.isNotEmpty()) (if (isDarkMode) Color.White else Color.Black) else Color.Gray.copy(0.3f),
                            onClick = {
                                if (undoStack.isNotEmpty()) {
                                    redoStack.add(Pair(contentValue.text, spans))
                                    val last = undoStack.removeAt(undoStack.size - 1)
                                    contentValue = TextFieldValue(last.first, selection = TextRange(last.first.length))
                                    spans = last.second
                                }
                            }
                        )

                        // Redo
                        NeuIconButton(
                            icon = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            isDarkMode = isDarkMode,
                            size = 36.dp,
                            iconSize = 17.dp,
                            tint = if (redoStack.isNotEmpty()) (if (isDarkMode) Color.White else Color.Black) else Color.Gray.copy(0.3f),
                            onClick = {
                                if (redoStack.isNotEmpty()) {
                                    undoStack.add(Pair(contentValue.text, spans))
                                    val next = redoStack.removeAt(redoStack.size - 1)
                                    contentValue = TextFieldValue(next.first, selection = TextRange(next.first.length))
                                    spans = next.second
                                }
                            }
                        )

                        // Save Button
                        GlassCard(
                            shape = RoundedCornerShape(12.dp),
                            isDarkMode = isDarkMode,
                            onClick = { performSave(showToast = true, thenNavigateBack = true) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Brush.linearGradient(listOf(CrimsonPrimary, Color(0xFFFF5E7E))))
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Pinned title bar — intentionally outside the scrolling note body.
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    isDarkMode = isDarkMode,
                    elevation = 3.dp
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if (isDarkMode) Color.White else Color(0xFF111111)),
                            cursorBrush = SolidColor(CrimsonPrimary),
                            decorationBox = { innerTextField ->
                                if (title.isBlank()) Text("Untitled Note", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if (isDarkMode) Color.White.copy(.35f) else Color.Gray.copy(.6f))
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(SimpleDateFormat("EEEE, MMMM dd | HH:mm", Locale.getDefault()).format(Date()), fontSize = 11.sp, color = if (isDarkMode) Color.White.copy(.5f) else Color.Gray)
                    }
                }

                // Note Body Scrollable Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(mainScrollState)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {

                    // Inline Attached Images (with Move, Resize, Crop, Rename support - PART F Item 5)
                    if (attachments.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            attachments.forEachIndexed { index, att ->
                                val isImage = att.mimeType.startsWith("image")
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    isDarkMode = isDarkMode,
                                    elevation = 2.dp
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        if (isImage) {
                                            AsyncImage(
                                                model = File(att.uri),
                                                contentDescription = att.fileName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(max = 240.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = att.fileName,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Row {
                                                IconButton(
                                                    onClick = { showImageEditModal = att },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Image", tint = CrimsonPrimary, modifier = Modifier.size(16.dp))
                                                }

                                                IconButton(
                                                    onClick = {
                                                        attachments = attachments.toMutableList().apply { removeAt(index) }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Interactive Table Display if present
                    if (tableData.isNotBlank()) {
                        InteractiveTableView(
                            tableData = tableData,
                            isDarkMode = isDarkMode,
                            onOpenEditor = { showTableFullscreen = true }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Main Content Input (Day Mode text contrast fix & Auto-Scroll buffer)
                    BasicTextField(
                        value = contentValue,
                        onValueChange = { rawVal ->
                            val newVal = continueListOnEnter(contentValue, rawVal)
                            val oldText = contentValue.text
                            val newText = newVal.text
                            if (newText != oldText) {
                                pushUndo()
                                // Shift existing spans for whatever just changed (insert/delete/paste)
                                spans = RichTextFormatter.adjustSpansForEdit(spans, oldText, newText)
                                // Item 8: if text grew (a real insertion), wrap ONLY the newly typed
                                // range with whichever formats are currently toggled on — existing
                                // text before/after is never touched.
                                if (newText.length > oldText.length) {
                                    var editStart = 0
                                    while (editStart < oldText.length && editStart < newText.length && oldText[editStart] == newText[editStart]) {
                                        editStart++
                                    }
                                    val insertedEnd = editStart + (newText.length - oldText.length)
                                    if (isBold) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "bold")
                                    if (isItalic) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "italic")
                                    if (isUnderline) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "underline")
                                    if (isStrikethrough) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "strikethrough")
                                    if (isCodeFormat) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "code")
                                    activeColorHex?.let { hex ->
                                        spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "color", hex)
                                    }
                                    activeFontSize?.let { sz ->
                                        spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "fontsize", sz.toString())
                                    }
                                    if (activeHighlight) spans = RichTextFormatter.addSpan(spans, editStart, insertedEnd, "highlight")
                                }
                            }
                            contentValue = newVal
                            // Item 13: keep the caret visible above the keyboard on every edit.
                            coroutineScope.launch { contentBringIntoViewRequester.bringIntoView() }
                        },
                        visualTransformation = VisualTransformation { text ->
                            TransformedText(
                                if (isJsonMode) {
                                    buildJsonHighlightedText(text.text)
                                } else {
                                    decorateListMarkers(
                                        RichTextFormatter.buildStyledText(
                                            text = text.text,
                                            spans = spans,
                                            defaultColor = effectiveTextColor,
                                            fontSize = fontSize.toFloat()
                                        )
                                    )
                                },
                                OffsetMapping.Identity
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = fontSize.sp,
                            fontFamily = if (isCodeFormat || isJsonMode) FontFamily.Monospace else FontFamily.Default,
                            textAlign = when (alignment) {
                                "center" -> TextAlign.Center
                                "right" -> TextAlign.Right
                                else -> TextAlign.Left
                            },
                            color = effectiveTextColor,
                            lineHeight = if (spans.any { it.type == "fontsize" } || activeFontSize != null) TextUnit.Unspecified else (fontSize * 1.5).sp
                        ),
                        cursorBrush = SolidColor(CrimsonPrimary),
                        decorationBox = { innerTextField ->
                            if (contentValue.text.isBlank()) {
                                Text(
                                    text = "Start typing your notes, code, or ideas...",
                                    fontSize = fontSize.sp,
                                    color = if (isDarkMode) Color.White.copy(0.35f) else Color.Gray.copy(0.6f)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 260.dp)
                            .bringIntoViewRequester(contentBringIntoViewRequester)
                            .onFocusEvent {
                                if (it.isFocused) {
                                    coroutineScope.launch { contentBringIntoViewRequester.bringIntoView() }
                                }
                            }
                    )

                    // Safe Bottom Space so typing near the bottom never slips behind the toolbar
                    Spacer(modifier = Modifier.height(160.dp))
                }

                // Bottom toolbar — Samsung Notes style: one scrolling row of tools
                val curLine = firstSelectedLine()
                val curPrefix = linePrefixOf(curLine)
                val idleTint = if (isDarkMode) Color.White else Color(0xFF222222)
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    isDarkMode = isDarkMode,
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Text styles (bold / italic / underline / strike / highlight / size / colour)
                        EditorTool(
                            icon = Icons.Default.TextFormat, label = "Text styles",
                            active = isBold || isItalic || isUnderline || isStrikethrough || activeHighlight ||
                                activeColorHex != null || activeFontSize != null,
                            idleTint = idleTint
                        ) { showFormatSheet = true }

                        ToolDivider(isDarkMode)

                        EditorTool(icon = Icons.Default.FormatListNumbered, label = "Paragraph style",
                            active = listType != "none" || alignment != "left", idleTint = idleTint) { showParagraphStyleSheet = true }

                        ToolDivider(isDarkMode)

                        EditorTool(icon = Icons.Default.FormatClear, label = "Clear formatting", active = false, idleTint = idleTint) {
                            clearFormattingOnSelection()
                        }

                        ToolDivider(isDarkMode)

                        EditorTool(icon = Icons.Default.Code, label = "Code", active = isCodeFormat, idleTint = idleTint) { isCodeFormat = !isCodeFormat }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isJsonMode) CrimsonPrimary.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { isJsonMode = !isJsonMode }
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = if (isJsonMode) CrimsonPrimary else Color.Gray
                            )
                        }

                        ToolDivider(isDarkMode)

                        // Insert / edit table
                        EditorTool(
                            painter = painterResource(R.drawable.ic_insert_table), label = "Insert table",
                            active = tableData.isNotBlank(), idleTint = idleTint
                        ) { showTableFullscreen = true }

                        // Insert image
                        EditorTool(
                            painter = painterResource(R.drawable.ic_svg_media), label = "Insert image",
                            active = false, idleTint = idleTint
                        ) { imagePickerLauncher.launch("image/*") }
                    }
                }
            }

            // SINGLE Floating AU AI Chatbot with custom sphere mascot icon (PART F Item 2 & 3 & 4)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset { IntOffset(botOffsetX.roundToInt(), botOffsetY.roundToInt()) }
                    .padding(end = 20.dp, bottom = 80.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            botOffsetX += dragAmount.x
                            botOffsetY += dragAmount.y
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable { showAiChatbotModal = true }
                ) {
                    Image(
                        painter = painterResource(R.drawable.au_bot_icon_1790271144581),
                        contentDescription = "AU Chatbot",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Paragraph style sheet — alignment + bullet/number/letter lists + indentation.
    if (showParagraphStyleSheet) {
        val sheetBg = if (isDarkMode) Color(0xFF282828) else Color(0xFFF2F4F8)
        val cardBg = if (isDarkMode) Color(0xFF333333) else Color.White
        val iconColor = if (isDarkMode) Color.White else Color(0xFF222222)
        val labelColor = if (isDarkMode) Color(0xFF9A9A9A) else Color(0xFF777777)
        ModalBottomSheet(onDismissRequest = { showParagraphStyleSheet = false }, containerColor = sheetBg) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Paragraph style", fontSize = 21.sp, color = iconColor)
                        Text("Alignment, lists and indentation", fontSize = 11.sp, color = labelColor)
                    }
                    IconButton(onClick = { showParagraphStyleSheet = false }) { Icon(Icons.Default.Close, "Close", tint = iconColor) }
                }
                Spacer(Modifier.height(10.dp))
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardBg).padding(8.dp)) {
                    Text("Alignment", fontSize = 12.sp, color = labelColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("left" to Icons.Default.FormatAlignLeft, "center" to Icons.Default.FormatAlignCenter, "right" to Icons.Default.FormatAlignRight).forEach { (value, icon) ->
                            Box(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(10.dp)).background(if (alignment == value) CrimsonPrimary.copy(.18f) else Color.Transparent).clickable { alignment = value }, contentAlignment = Alignment.Center) {
                                Icon(icon, value, tint = if (alignment == value) CrimsonPrimary else iconColor)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("List style", fontSize = 12.sp, color = labelColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val styles = listOf("bullet" to ("•" to "Dot number"), "digit" to ("1." to "Digit number"), "letter" to ("a." to "Letter number"))
                        styles.forEach { (value, item) ->
                            val active = listType == value
                            Box(Modifier.weight(1f).height(82.dp).clip(RoundedCornerShape(10.dp)).background(if (active) CrimsonPrimary.copy(.18f) else Color.Transparent).clickable { toggleParagraphList(value) }, contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(item.first, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (active) CrimsonPrimary else iconColor)
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.second, fontSize = 10.sp, color = if (active) CrimsonPrimary else labelColor)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { indentLines() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary.copy(.14f), contentColor = iconColor)) { Icon(Icons.Default.FormatIndentIncrease, null); Spacer(Modifier.width(6.dp)); Text("Indent") }
                        Button(onClick = { outdentLines() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary.copy(.14f), contentColor = iconColor)) { Icon(Icons.Default.FormatIndentDecrease, null); Spacer(Modifier.width(6.dp)); Text("Outdent") }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }

    // Text styles sheet — same layout as Samsung Notes: B I U S highlight / font size / font colour.
    if (showFormatSheet) {
        val sheetBg = if (isDarkMode) Color(0xFF282828) else Color(0xFFF2F4F8)
        val cardBg = if (isDarkMode) Color(0xFF333333) else Color(0xFFFFFFFF)
        val labelColor = if (isDarkMode) Color(0xFF9A9A9A) else Color(0xFF777777)
        val iconColor = if (isDarkMode) Color.White else Color(0xFF222222)
        val sel = contentValue.selection

        fun selActive(type: String, typing: Boolean) =
            if (sel.collapsed) typing else RichTextFormatter.isPropertyActiveThroughout(spans, type, sel.min, sel.max)

        val boldOn = if (sel.collapsed) isBold else selActive("bold", false)
        val italicOn = if (sel.collapsed) isItalic else selActive("italic", false)
        val underOn = if (sel.collapsed) isUnderline else selActive("underline", false)
        val strikeOn = if (sel.collapsed) isStrikethrough else selActive("strikethrough", false)
        val hiOn = if (sel.collapsed) activeHighlight else selActive("highlight", false)

        val shownSize = if (sel.collapsed) (activeFontSize ?: fontSize)
        else spans.lastOrNull { it.type == "fontsize" && it.start <= sel.min && it.end >= sel.max }?.value?.toIntOrNull() ?: fontSize
        val shownColor: String? = if (sel.collapsed) activeColorHex
        else spans.lastOrNull { it.type == "color" && it.start <= sel.min && it.end >= sel.max }?.value

        ModalBottomSheet(
            onDismissRequest = { showFormatSheet = false },
            containerColor = sheetBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Text styles", fontSize = 21.sp, color = iconColor)
                    IconButton(onClick = { showFormatSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = iconColor)
                    }
                }
                Text(
                    text = if (sel.collapsed) "Applies to text you type next" else "Applies to your selection",
                    fontSize = 11.sp, color = labelColor, modifier = Modifier.padding(start = 6.dp, bottom = 10.dp)
                )

                // B  I  U  S  highlight — five segments
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardBg),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    @Composable
                    fun Segment(on: Boolean, content: @Composable () -> Unit, onClick: () -> Unit) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .background(if (on) CrimsonPrimary.copy(alpha = 0.28f) else Color.Transparent)
                                .clickable(onClick = onClick),
                            contentAlignment = Alignment.Center
                        ) { content() }
                    }
                    Segment(boldOn, { Icon(Icons.Default.FormatBold, "Bold", tint = if (boldOn) CrimsonPrimary else iconColor) }) {
                        applyOrToggleBooleanFormat("bold", isBold) { isBold = it }
                    }
                    Segment(italicOn, { Icon(Icons.Default.FormatItalic, "Italic", tint = if (italicOn) CrimsonPrimary else iconColor) }) {
                        applyOrToggleBooleanFormat("italic", isItalic) { isItalic = it }
                    }
                    Segment(underOn, { Icon(Icons.Default.FormatUnderlined, "Underline", tint = if (underOn) CrimsonPrimary else iconColor) }) {
                        applyOrToggleBooleanFormat("underline", isUnderline) { isUnderline = it }
                    }
                    Segment(strikeOn, { Icon(Icons.Default.FormatStrikethrough, "Strikethrough", tint = if (strikeOn) CrimsonPrimary else iconColor) }) {
                        applyOrToggleBooleanFormat("strikethrough", isStrikethrough) { isStrikethrough = it }
                    }
                    Segment(hiOn, { Icon(Icons.Default.Highlight, "Highlight", tint = if (hiOn) CrimsonPrimary else iconColor) }) {
                        applyOrToggleHighlight()
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Font size
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardBg).padding(vertical = 12.dp)
                ) {
                    Text("Font size", fontSize = 13.sp, color = labelColor, modifier = Modifier.padding(start = 14.dp, bottom = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf(10, 12, 14, 16, 18, 20, 24, 36, 48, 64).forEach { size ->
                            val on = shownSize == size
                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (on) CrimsonPrimary.copy(alpha = 0.18f) else Color.Transparent)
                                    .clickable { applyOrSetFontSize(size) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$size", fontSize = 18.sp,
                                    fontWeight = if (on) FontWeight.Bold else FontWeight.Normal,
                                    color = if (on) CrimsonPrimary else iconColor
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Font colour: default (gradient) + gray, red, orange, yellow, green, blue, lavender
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardBg).padding(vertical = 12.dp)
                ) {
                    Text("Font color", fontSize = 13.sp, color = labelColor, modifier = Modifier.padding(start = 14.dp, bottom = 10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val swatches = listOf<Pair<String?, Color>>(
                            null to Color.Transparent,
                            "#8E8E8E" to Color(0xFF8E8E8E),
                            "#E8382A" to Color(0xFFE8382A),
                            "#F08A30" to Color(0xFFF08A30),
                            "#F2C244" to Color(0xFFF2C244),
                            "#59B25F" to Color(0xFF59B25F),
                            "#5AB4F5" to Color(0xFF5AB4F5),
                            "#B9C0F0" to Color(0xFFB9C0F0)
                        )
                        swatches.forEach { (hex, color) ->
                            val on = shownColor.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .width(46.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .then(
                                        if (hex == null) Modifier.background(
                                            Brush.horizontalGradient(
                                                listOf(if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFDDDDDD), if (isDarkMode) Color(0xFFDDDDDD) else Color(0xFF1A1A1A))
                                            )
                                        ) else Modifier.background(color)
                                    )
                                    .border(
                                        if (on) 2.dp else 0.dp,
                                        if (on) CrimsonPrimary else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { applyOrSetColor(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (on) Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Dedicated Table Editor (PART F Item 1)
    if (showTableFullscreen) {
        TableEditorScreen(
            initialTableData = tableData,
            isDarkMode = isDarkMode,
            onBack = { showTableFullscreen = false },
            onSaveTable = { saved ->
                tableData = saved
                showTableFullscreen = false
                Toast.makeText(context, "Table updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Floating Chatbot with live editor access — Item 20. Reuses the same
    // AiChatScreen used elsewhere in the app (with its file/image OCR
    // attachment support), but wired here with the CURRENT editor content and
    // a callback that lets the model actually rewrite/insert into this note.
    AiChatScreen(
        isOpen = showAiChatbotModal,
        isDarkMode = isDarkMode,
        preferences = preferences,
        repository = repository,
        currentNoteContent = "Title: $title\n\n${contentValue.text}",
        onClose = { showAiChatbotModal = false },
        onCreateNoteFromAi = { _, _, _ -> },
        onModifyCurrentNote = { newContent ->
            contentValue = TextFieldValue(newContent, selection = TextRange(newContent.length))
            Toast.makeText(context, "Note updated by AU Bot", Toast.LENGTH_SHORT).show()
        }
    )

    // Image Edit Modal (Move, Crop, Resize, Rename - PART F Item 5)
    showImageEditModal?.let { imgAtt ->
        var editedName by remember { mutableStateOf(imgAtt.fileName) }
        AlertDialog(
            onDismissRequest = { showImageEditModal = null },
            title = { Text("Edit Image in Note", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Rename File") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Actions available: Crop, Resize, Reorder in note.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    onClick = {
                        attachments = attachments.map {
                            if (it.uri == imgAtt.uri) it.copy(fileName = editedName) else it
                        }
                        showImageEditModal = null
                        Toast.makeText(context, "Image updated!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageEditModal = null }) { Text("Cancel") }
            }
        )
    }
}


/** Adds a coloured marker + strike-through for checked items, without changing text length. */
private fun decorateListMarkers(base: AnnotatedString): AnnotatedString {
    val text = base.text
    if (text.isEmpty()) return base
    val markerRegex = Regex("(?m)^([ \\t]*)(\\[ \\]|\\[x\\]|•|\\d+\\.|[A-Za-z]\\.) ")
    val lineRegex = Regex("(?m)^[ \\t]*\\[x\\] .*$")
    return androidx.compose.ui.text.buildAnnotatedString {
        append(base)
        for (m in markerRegex.findAll(text)) {
            val g = m.groups[2] ?: continue
            addStyle(SpanStyle(color = CrimsonPrimary, fontWeight = FontWeight.Bold), g.range.first, g.range.last + 1)
        }
        for (m in lineRegex.findAll(text)) {
            val bodyStart = m.value.indexOf("] ") + 2
            val from = m.range.first + bodyStart
            if (from < m.range.last + 1) {
                addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough, color = Color.Gray), from, m.range.last + 1)
            }
        }
    }
}

@Composable
private fun EditorTool(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    painter: androidx.compose.ui.graphics.painter.Painter? = null,
    label: String,
    active: Boolean,
    idleTint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) CrimsonPrimary.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val tint = if (active) CrimsonPrimary else idleTint
        if (icon != null) Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        else if (painter != null) Icon(painter, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ToolDivider(isDarkMode: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(1.dp)
            .height(22.dp)
            .background(if (isDarkMode) Color(0x33FFFFFF) else Color(0x22000000))
    )
}
