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

import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnnotatedStringConversionTest {

    private val density = Density(density = 2f, fontScale = 1f)

    @Test
    fun toSpanned_preservesText() {
        val annotated = AnnotatedString("Hello World")
        val spanned = annotated.toSpanned(density)
        assertThat(spanned.toString()).isEqualTo("Hello World")
    }

    @Test
    fun toSpanned_foregroundColor() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.Red)) { append("red") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 3, ForegroundColorSpan::class.java)
        assertThat(spans).hasLength(1)
        assertThat(spans[0].foregroundColor).isEqualTo(android.graphics.Color.RED)
        assertThat(spanned.getSpanStart(spans[0])).isEqualTo(0)
        assertThat(spanned.getSpanEnd(spans[0])).isEqualTo(3)
    }

    @Test
    fun toSpanned_fontSize() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 20.sp)) { append("big") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 3, AbsoluteSizeSpan::class.java)
        assertThat(spans).hasLength(1)
        assertThat(spans[0].size).isEqualTo(40) // 20sp * density 2
    }

    @Test
    fun toSpanned_boldWeight() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("bold") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 4, StyleSpan::class.java)
        assertThat(spans).hasLength(1)
        assertThat(spans[0].style).isEqualTo(android.graphics.Typeface.BOLD)
    }

    @Test
    fun toSpanned_italic() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("italic") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 6, StyleSpan::class.java)
        assertThat(spans).hasLength(1)
        assertThat(spans[0].style).isEqualTo(android.graphics.Typeface.ITALIC)
    }

    @Test
    fun toSpanned_background() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(background = Color.Yellow)) { append("highlight") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 9, BackgroundColorSpan::class.java)
        assertThat(spans).hasLength(1)
        assertThat(spans[0].backgroundColor).isEqualTo(android.graphics.Color.YELLOW)
    }

    @Test
    fun toSpanned_underline() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("underline") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 9, UnderlineSpan::class.java)
        assertThat(spans).hasLength(1)
    }

    @Test
    fun toSpanned_lineThrough() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                append("strikethrough")
            }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 13, StrikethroughSpan::class.java)
        assertThat(spans).hasLength(1)
    }

    @Test
    fun toSpanned_letterSpacing_em() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(letterSpacing = 0.3.em)) { append("spaced") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 6, LetterSpacingSpan::class.java)
        assertThat(spans).hasLength(1)
        val paint = TextPaint()
        spans[0].updateMeasureState(paint)
        assertThat(paint.letterSpacing).isEqualTo(0.3f)
    }

    @Test
    fun toSpanned_letterSpacing_sp_withSpanFontSize() {
        // SpanStyle fontSize=16sp = 32px (density 2), letterSpacing=1.6sp = 3.2px
        // em = 3.2 / 32 = 0.1
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 16.sp, letterSpacing = 1.6.sp)) { append("spaced") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 6, LetterSpacingSpan::class.java)
        assertThat(spans).hasLength(1)
        val paint = TextPaint()
        spans[0].updateMeasureState(paint)
        assertThat(paint.letterSpacing).isEqualTo(0.1f)
    }

    @Test
    fun toSpanned_letterSpacing_sp_withBaseFontSize() {
        // baseFontSize=16sp = 32px, letterSpacing=1.6sp = 3.2px; em = 3.2 / 32 = 0.1
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(letterSpacing = 1.6.sp)) { append("spaced") }
        }
        val spanned = annotated.toSpanned(density, baseFontSize = 16.sp)
        val spans = spanned.getSpans(0, 6, LetterSpacingSpan::class.java)
        assertThat(spans).hasLength(1)
        val paint = TextPaint()
        spans[0].updateMeasureState(paint)
        assertThat(paint.letterSpacing).isEqualTo(0.1f)
    }

    @Test
    fun toSpanned_letterSpacing_sp_withoutFontSize_skipsSpan() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(letterSpacing = 1.6.sp)) { append("spaced") }
        }
        val spanned = annotated.toSpanned(density)
        val spans = spanned.getSpans(0, 6, LetterSpacingSpan::class.java)
        assertThat(spans).isEmpty()
    }

    @Test
    fun toSpanned_multipleSpans() {
        val annotated = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.Red)) { append("red ") }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("bold") }
        }
        val spanned = annotated.toSpanned(density)
        assertThat(spanned.toString()).isEqualTo("red bold")
        val colorSpans = spanned.getSpans(0, 4, ForegroundColorSpan::class.java)
        assertThat(colorSpans).hasLength(1)
        val styleSpans = spanned.getSpans(4, 8, StyleSpan::class.java)
        assertThat(styleSpans).hasLength(1)
    }

    @Test
    fun toSpanned_noSpans() {
        val annotated = AnnotatedString("plain text")
        val spanned = annotated.toSpanned(density)
        assertThat(spanned.toString()).isEqualTo("plain text")
        assertThat(spanned.getSpans(0, 10, Any::class.java)).isEmpty()
    }
}
