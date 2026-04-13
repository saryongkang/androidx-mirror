/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.text.vertical.compose

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.isSpecified

internal fun AnnotatedString.toSpanned(density: Density): Spanned {
    val sb = SpannableStringBuilder(text)
    for (range in spanStyles) {
        applySpanStyle(sb, range.item, range.start, range.end, density)
    }
    return sb
}

private fun applySpanStyle(
    sb: SpannableStringBuilder,
    style: SpanStyle,
    start: Int,
    end: Int,
    density: Density,
) {
    val flag = Spanned.SPAN_INCLUSIVE_EXCLUSIVE

    if (style.color.isSpecified) {
        sb.setSpan(ForegroundColorSpan(style.color.toArgb()), start, end, flag)
    }

    if (style.fontSize.isSpecified) {
        val px = with(density) { style.fontSize.toPx() }.toInt()
        sb.setSpan(AbsoluteSizeSpan(px), start, end, flag)
    }

    val weight = style.fontWeight
    val italic = style.fontStyle == FontStyle.Italic
    if (weight != null || italic) {
        val typefaceStyle =
            when {
                (weight ?: FontWeight.Normal) >= FontWeight.Bold && italic -> Typeface.BOLD_ITALIC
                (weight ?: FontWeight.Normal) >= FontWeight.Bold -> Typeface.BOLD
                italic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
        sb.setSpan(android.text.style.StyleSpan(typefaceStyle), start, end, flag)
    }

    if (style.background.isSpecified) {
        sb.setSpan(BackgroundColorSpan(style.background.toArgb()), start, end, flag)
    }

    style.textDecoration?.let { decoration ->
        if (TextDecoration.Underline in decoration) {
            sb.setSpan(UnderlineSpan(), start, end, flag)
        }
        if (TextDecoration.LineThrough in decoration) {
            sb.setSpan(StrikethroughSpan(), start, end, flag)
        }
    }

    if (style.letterSpacing.isSpecified) {
        sb.setSpan(LetterSpacingSpan(style.letterSpacing.value), start, end, flag)
    }
}

internal class LetterSpacingSpan(private val letterSpacing: Float) : MetricAffectingSpan() {
    override fun updateMeasureState(textPaint: android.text.TextPaint) {
        textPaint.letterSpacing = letterSpacing
    }

    override fun updateDrawState(tp: android.text.TextPaint) {
        tp.letterSpacing = letterSpacing
    }
}
