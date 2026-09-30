package com.example.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/** Loading, rotating, cropping and blurring the user's wallpaper. */
object ThemeImageProcessor {

    /** Decodes [uri] down-sampled so the longest side is about [maxSide] px. Null on failure. */
    fun loadBitmap(context: Context, uri: Uri, maxSide: Int = 1600): Bitmap? = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxSide * 2) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    } catch (e: Exception) { null }

    fun loadFile(path: String, maxSide: Int = 1600): Bitmap? = try {
        if (path.isBlank() || !File(path).exists()) null else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxSide * 2) sample *= 2
            BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
        }
    } catch (e: Exception) { null }

    fun rotate(src: Bitmap, degrees: Int): Bitmap {
        if (degrees % 360 == 0) return src
        val m = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
    }

    /** Cheap, smooth blur: repeated down-scale then up-scale. [amount] is 0..1. */
    fun blur(src: Bitmap, amount: Float): Bitmap {
        if (amount <= 0.01f) return src
        val factor = 1f + amount * 24f
        val w = (src.width / factor).toInt().coerceAtLeast(8)
        val h = (src.height / factor).toInt().coerceAtLeast(8)
        val small = Bitmap.createScaledBitmap(src, w, h, true)
        val tiny = Bitmap.createScaledBitmap(small, (w / 2).coerceAtLeast(4), (h / 2).coerceAtLeast(4), true)
        return Bitmap.createScaledBitmap(tiny, src.width, src.height, true)
    }

    /**
     * Renders the framed area exactly as the preview shows it.
     * [zoom] >= 1, [offsetX]/[offsetY] are preview-frame pixel offsets of the image centre,
     * [frameW]/[frameH] the preview frame size in px.
     */
    fun render(
        rotatedSource: Bitmap,
        frameW: Float,
        frameH: Float,
        zoom: Float,
        offsetX: Float,
        offsetY: Float,
        blurAmount: Float,
        outW: Int = 900
    ): Bitmap {
        val outH = (outW * frameH / frameW).toInt().coerceAtLeast(1)
        val k = outW / frameW
        val base = maxOf(frameW / rotatedSource.width, frameH / rotatedSource.height)
        val scale = base * zoom * k
        val m = Matrix().apply {
            postTranslate(-rotatedSource.width / 2f, -rotatedSource.height / 2f)
            postScale(scale, scale)
            postTranslate(outW / 2f + offsetX * k, outH / 2f + offsetY * k)
        }
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        Canvas(out).drawBitmap(rotatedSource, m, Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
        return blur(out, blurAmount)
    }

    fun saveJpeg(context: Context, bmp: Bitmap): String {
        val f = File(context.filesDir, "theme_bg.jpg")
        FileOutputStream(f).use { bmp.compress(Bitmap.CompressFormat.JPEG, 88, it) }
        return f.absolutePath
    }

    fun delete(context: Context) { File(context.filesDir, "theme_bg.jpg").delete() }
}
