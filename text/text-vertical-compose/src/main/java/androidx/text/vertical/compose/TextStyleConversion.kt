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
import android.text.TextPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.isSpecified

internal fun TextStyle.toTextPaint(density: Density): TextPaint {
    val paint =
        TextPaint().apply {
            isAntiAlias = true
            if (fontSize.isSpecified) {
                textSize = with(density) { fontSize.toPx() }
            }
        }

    if (color.isSpecified) {
        paint.color = color.toArgb()
    }

    val weight = fontWeight ?: FontWeight.Normal
    val italic = fontStyle == FontStyle.Italic
    val typefaceStyle =
        when {
            weight >= FontWeight.Bold && italic -> Typeface.BOLD_ITALIC
            weight >= FontWeight.Bold -> Typeface.BOLD
            italic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
    paint.typeface = Typeface.create(Typeface.DEFAULT, typefaceStyle)

    if (letterSpacing.isSpecified) {
        paint.letterSpacing =
            when {
                letterSpacing.isEm -> letterSpacing.value
                letterSpacing.isSp && fontSize.isSpecified -> {
                    // Convert sp to em using the font size: em = sp_in_px / fontSize_in_px
                    val spInPx = with(density) { letterSpacing.toPx() }
                    val fontSizeInPx = with(density) { fontSize.toPx() }
                    spInPx / fontSizeInPx
                }
                else -> 0f
            }
    }

    textDecoration?.let { decoration ->
        paint.isUnderlineText = TextDecoration.Underline in decoration
        paint.isStrikeThruText = TextDecoration.LineThrough in decoration
    }

    if (background.isSpecified) {
        paint.bgColor = background.toArgb()
    }

    shadow?.let { s ->
        paint.setShadowLayer(s.blurRadius, s.offset.x, s.offset.y, s.color.toArgb())
    }

    return paint
}

internal fun TextPaint.applyColor(color: Color) {
    if (color.isSpecified) {
        this.color = color.toArgb()
    }
}
