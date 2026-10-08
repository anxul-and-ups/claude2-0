package com.example.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

/** Small, dependency-free helpers for the HTML viewer: formatter, highlighter and fold regions. */
object HtmlTools {

    private val voidTags = setOf(
        "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr"
    )

    // raw blocks are kept exactly as typed; comments / doctype stay one token
    private val tokenRegex = Regex(
        "<!--[\\s\\S]*?-->|<(script|style|pre|textarea)\\b[^>]*>[\\s\\S]*?</\\1\\s*>|<![^>]*>|</?[a-zA-Z][^>]*>|[^<]+",
        RegexOption.IGNORE_CASE
    )
    private val tagNameRegex = Regex("^<(/?)([a-zA-Z][\\w:-]*)")

    /** Re-indents HTML with two spaces per level. Script / style / pre blocks are left untouched. */
    fun format(source: String): String {
        val out = StringBuilder()
        var depth = 0
        fun line(text: String) {
            if (text.isBlank()) return
            out.append("  ".repeat(depth.coerceAtLeast(0))).append(text.trim()).append('\n')
        }
        for (m in tokenRegex.findAll(source)) {
            val tok = m.value
            when {
                tok.startsWith("<!--") || tok.startsWith("<!") -> line(tok)
                tok.startsWith("<") -> {
                    val nm = tagNameRegex.find(tok)
                    val name = nm?.groupValues?.get(2)?.lowercase().orEmpty()
                    val closing = nm?.groupValues?.get(1) == "/"
                    val rawBlock = name in setOf("script", "style", "pre", "textarea") && !closing && tok.contains("</", ignoreCase = true)
                    when {
                        rawBlock -> { out.append("  ".repeat(depth.coerceAtLeast(0))).append(tok.trim()).append('\n') }
                        closing -> { depth--; line(tok) }
                        tok.endsWith("/>") || name in voidTags -> line(tok)
                        else -> { line(tok); depth++ }
                    }
                }
                else -> line(tok.trim().replace(Regex("\\s+"), " "))
            }
        }
        return out.toString().trimEnd()
    }

    data class Palette(
        val tag: Color, val attr: Color, val string: Color, val comment: Color, val doctype: Color
    )

    val darkPalette = Palette(Color(0xFF7CC4FF), Color(0xFFFFB86C), Color(0xFF8BE28B), Color(0xFF7F8C8D), Color(0xFFC792EA))
    val lightPalette = Palette(Color(0xFF1565C0), Color(0xFFB45F06), Color(0xFF2E7D32), Color(0xFF8A8A8A), Color(0xFF7B1FA2))

    private val commentRegex = Regex("<!--[\\s\\S]*?-->")
    private val doctypeRegex = Regex("<![A-Za-z][^>]*>")
    private val tagRegex = Regex("</?[a-zA-Z][^>]*>")
    private val stringRegex = Regex("\"[^\"]*\"|'[^']*'")
    private val attrRegex = Regex("\\s([\\w:@.-]+)(?==)")

    /** Syntax colours for the whole document (spans survive being cut into lines). */
    fun highlight(source: String, p: Palette): AnnotatedString = buildAnnotatedString {
        append(source)
        for (m in tagRegex.findAll(source)) {
            val s = m.range.first
            val e = m.range.last + 1
            addStyle(SpanStyle(color = p.tag), s, e)
            val inner = m.value
            for (a in attrRegex.findAll(inner)) {
                val g = a.groups[1] ?: continue
                addStyle(SpanStyle(color = p.attr), s + g.range.first, s + g.range.last + 1)
            }
            for (q in stringRegex.findAll(inner)) {
                addStyle(SpanStyle(color = p.string), s + q.range.first, s + q.range.last + 1)
            }
        }
        for (m in doctypeRegex.findAll(source)) addStyle(SpanStyle(color = p.doctype), m.range.first, m.range.last + 1)
        for (m in commentRegex.findAll(source)) addStyle(SpanStyle(color = p.comment), m.range.first, m.range.last + 1)
    }

    /** startLine -> endLine for every element that spans several lines (these can be folded). */
    fun foldRegions(source: String): Map<Int, Int> {
        if (source.isEmpty()) return emptyMap()
        val lineStarts = ArrayList<Int>()
        lineStarts.add(0)
        source.forEachIndexed { i, c -> if (c == '\n') lineStarts.add(i + 1) }
        fun lineOf(offset: Int): Int {
            var lo = 0
            var hi = lineStarts.size - 1
            while (lo < hi) {
                val mid = (lo + hi + 1) / 2
                if (lineStarts[mid] <= offset) lo = mid else hi = mid - 1
            }
            return lo
        }
        val regions = LinkedHashMap<Int, Int>()
        val stack = ArrayList<Pair<String, Int>>()
        // skip comments and raw blocks so tags inside them don't confuse the stack
        for (m in tokenRegex.findAll(source)) {
            val tok = m.value
            if (!tok.startsWith("<") || tok.startsWith("<!")) continue
            val nm = tagNameRegex.find(tok) ?: continue
            val name = nm.groupValues[2].lowercase()
            val closing = nm.groupValues[1] == "/"
            val isRaw = name in setOf("script", "style", "pre", "textarea") && !closing && tok.contains("</", ignoreCase = true)
            val startLine = lineOf(m.range.first)
            if (isRaw) {
                val endLine = lineOf(m.range.last)
                if (endLine > startLine) regions[startLine] = endLine
                continue
            }
            when {
                closing -> {
                    val idx = stack.indexOfLast { it.first == name }
                    if (idx >= 0) {
                        val open = stack[idx]
                        while (stack.size > idx) stack.removeAt(stack.size - 1)
                        val endLine = lineOf(m.range.first)
                        if (endLine > open.second && !regions.containsKey(open.second)) regions[open.second] = endLine
                    }
                }
                tok.endsWith("/>") || name in voidTags -> {}
                else -> stack.add(name to startLine)
            }
        }
        return regions
    }

    val sample = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Sample page</title>
  <style>
    body { font-family: sans-serif; margin: 24px; background: #fafafa; }
    h1 { color: #e11d48; }
    .card { padding: 16px; border-radius: 12px; background: #fff; box-shadow: 0 2px 8px #0002; }
  </style>
</head>
<body>
  <h1>Hello, AU Notes!</h1>
  <div class="card">
    <p>This is a sample HTML page.</p>
    <button onclick="alert('It works!')">Tap me</button>
  </div>
</body>
</html>"""
}
