package com.palatin.mercurial.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import kotlin.math.ln
import kotlin.math.pow


fun Long.fileSize(): String {
    if (this < 1024) return "$this B"
    val exp = (ln(this.toDouble()) / ln(1024.0)).toInt()
    return String.format("%.1f %sB", this / this.toDouble().pow(exp.toDouble()), "KMGTPE"[exp - 1])
}

fun String.paintString(@ColorInt color: Int): SpannableString {
    return SpannableString(this).apply {
        setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}