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
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified

internal fun AnnotatedString.toSpanned(
    density: Density,
    baseFontSize: TextUnit = TextUnit.Unspecified,
): Spanned {
    val sb = SpannableStringBuilder(text)
    for (range in spanStyles) {
        applySpanStyle(sb, range.item, range.start, range.end, density, baseFontSize)
    }
    return sb
}

private fun applySpanStyle(
    sb: SpannableStringBuilder,
    style: SpanStyle,
    start: Int,
    end: Int,
    density: Density,
    baseFontSize: TextUnit,
) {
    val flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

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
        sb.setSpan(StyleSpan(typefaceStyle), start, end, flag)
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
        val letterSpacing =
            when {
                style.letterSpacing.isEm -> style.letterSpacing.value
                style.letterSpacing.isSp -> {
                    // Convert sp to em using the effective font size (span's own, else base).
                    val effectiveFontSize =
                        if (style.fontSize.isSpecified) style.fontSize else baseFontSize
                    if (effectiveFontSize.isSpecified) {
                        with(density) { style.letterSpacing.toPx() / effectiveFontSize.toPx() }
                    } else {
                        null
                    }
                }
                else -> null
            }
        if (letterSpacing != null) {
            sb.setSpan(LetterSpacingSpan(letterSpacing), start, end, flag)
        }
    }
}

/**
 * A span that applies letter spacing to the affected text range.
 *
 * @property value The letter spacing to apply, expressed in em units (relative to the font size).
 */
internal class LetterSpacingSpan(private val value: Float) : MetricAffectingSpan() {
    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.letterSpacing = value
    }

    override fun updateDrawState(tp: TextPaint) {
        tp.letterSpacing = value
    }
}
