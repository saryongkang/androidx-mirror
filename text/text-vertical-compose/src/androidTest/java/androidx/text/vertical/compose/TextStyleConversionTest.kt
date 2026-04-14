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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.test.filters.SdkSuppress
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TextStyleConversionTest {

    private val density = Density(density = 2f, fontScale = 1f)

    @Test
    fun toTextPaint_fontSize() {
        val style = TextStyle(fontSize = 16.sp)
        val paint = style.toTextPaint(density)
        assertThat(paint.textSize).isEqualTo(32f) // 16sp * density 2
    }

    @Test
    fun toTextPaint_color() {
        val style = TextStyle(color = Color.Red)
        val paint = style.toTextPaint(density)
        assertThat(paint.color).isEqualTo(android.graphics.Color.RED)
    }

    @Test
    fun toTextPaint_fontWeight_bold() {
        val style = TextStyle(fontWeight = FontWeight.Bold)
        val paint = style.toTextPaint(density)
        assertThat(paint.typeface.isBold).isTrue()
        assertThat(paint.typeface.isItalic).isFalse()
    }

    @Test
    fun toTextPaint_fontStyle_italic() {
        val style = TextStyle(fontStyle = FontStyle.Italic)
        val paint = style.toTextPaint(density)
        assertThat(paint.typeface.isItalic).isTrue()
    }

    @Test
    fun toTextPaint_fontWeight_bold_and_italic() {
        val style = TextStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        val paint = style.toTextPaint(density)
        assertThat(paint.typeface.style).isEqualTo(Typeface.BOLD_ITALIC)
    }

    @Test
    fun toTextPaint_default_isNormalTypeface() {
        val style = TextStyle.Default
        val paint = style.toTextPaint(density)
        assertThat(paint.typeface.style).isEqualTo(Typeface.NORMAL)
    }

    @Test
    fun toTextPaint_letterSpacing_em() {
        val style = TextStyle(letterSpacing = 0.5.em)
        val paint = style.toTextPaint(density)
        assertThat(paint.letterSpacing).isEqualTo(0.5f)
    }

    @Test
    fun toTextPaint_letterSpacing_sp() {
        // fontSize=16sp = 32px (density 2), letterSpacing=1.6sp = 3.2px
        // em = 3.2 / 32 = 0.1
        val style = TextStyle(fontSize = 16.sp, letterSpacing = 1.6.sp)
        val paint = style.toTextPaint(density)
        assertThat(paint.letterSpacing).isEqualTo(0.1f)
    }

    @Test
    fun toTextPaint_letterSpacing_sp_withoutFontSize_defaultsToZero() {
        val style = TextStyle(letterSpacing = 0.5.sp)
        val paint = style.toTextPaint(density)
        assertThat(paint.letterSpacing).isEqualTo(0f)
    }

    @Test
    fun toTextPaint_underline() {
        val style = TextStyle(textDecoration = TextDecoration.Underline)
        val paint = style.toTextPaint(density)
        assertThat(paint.isUnderlineText).isTrue()
        assertThat(paint.isStrikeThruText).isFalse()
    }

    @Test
    fun toTextPaint_lineThrough() {
        val style = TextStyle(textDecoration = TextDecoration.LineThrough)
        val paint = style.toTextPaint(density)
        assertThat(paint.isStrikeThruText).isTrue()
        assertThat(paint.isUnderlineText).isFalse()
    }

    @Test
    fun toTextPaint_combinedDecoration() {
        val style =
            TextStyle(textDecoration = TextDecoration.Underline + TextDecoration.LineThrough)
        val paint = style.toTextPaint(density)
        assertThat(paint.isUnderlineText).isTrue()
        assertThat(paint.isStrikeThruText).isTrue()
    }

    @Test
    fun toTextPaint_background() {
        val style = TextStyle(background = Color.Yellow)
        val paint = style.toTextPaint(density)
        assertThat(paint.bgColor).isEqualTo(android.graphics.Color.YELLOW)
    }

    @SdkSuppress(minSdkVersion = 29)
    @Test
    fun toTextPaint_shadow() {
        val style =
            TextStyle(
                shadow = Shadow(color = Color.Black, offset = Offset(2f, 3f), blurRadius = 4f)
            )
        val paint = style.toTextPaint(density)
        assertThat(paint.shadowLayerRadius).isEqualTo(4f)
        assertThat(paint.shadowLayerDx).isEqualTo(2f)
        assertThat(paint.shadowLayerDy).isEqualTo(3f)
    }

    @Test
    fun toTextPaint_antiAlias() {
        val paint = TextStyle.Default.toTextPaint(density)
        assertThat(paint.isAntiAlias).isTrue()
    }

    @Test
    fun applyColor_overridesExistingColor() {
        val paint = TextStyle(color = Color.Blue).toTextPaint(density)
        paint.applyColor(Color.Green)
        assertThat(paint.color).isEqualTo(android.graphics.Color.GREEN)
    }

    @Test
    fun applyColor_unspecifiedDoesNotChange() {
        val paint = TextStyle(color = Color.Blue).toTextPaint(density)
        paint.applyColor(Color.Unspecified)
        assertThat(paint.color).isEqualTo(android.graphics.Color.BLUE)
    }
}
