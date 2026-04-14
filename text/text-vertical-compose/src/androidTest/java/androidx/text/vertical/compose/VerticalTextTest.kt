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

import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.BAKLAVA)
class VerticalTextTest {

    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun stringOverload_doesNotCrash() {
        rule.setContent { VerticalText(text = "テスト", style = TextStyle(fontSize = 20.sp)) }
        rule.waitForIdle()
    }

    @Test
    fun annotatedStringOverload_doesNotCrash() {
        val text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("太字") }
            append("テスト")
        }
        rule.setContent { VerticalText(text = text, style = TextStyle(fontSize = 20.sp)) }
        rule.waitForIdle()
    }

    @Test
    fun spannedOverload_doesNotCrash() {
        val paint = TextPaint().apply { textSize = 40f }
        rule.setContent { VerticalText(text = SpannableString("テスト") as Spanned, paint = paint) }
        rule.waitForIdle()
    }

    @Test
    fun stringOverload_withAllParams_doesNotCrash() {
        rule.setContent {
            VerticalText(
                text = "吾輩は猫である。名前はまだ無い。",
                style = TextStyle(fontSize = 14.sp),
                maxColumns = 2,
                minColumns = 1,
            )
        }
        rule.waitForIdle()
    }

    @Test
    fun singleCharText_doesNotCrash() {
        rule.setContent { VerticalText(text = "a") }
        rule.waitForIdle()
    }

    @Test
    fun singleCharAnnotatedString_doesNotCrash() {
        rule.setContent { VerticalText(text = AnnotatedString("a")) }
        rule.waitForIdle()
    }

    @Test
    fun longText_multipleColumns_doesNotCrash() {
        val longText =
            "吾輩は猫である。名前はまだ無い。どこで生まれたかとんと見当がつかぬ。" + "何でも薄暗いじめじめしたところでニャーニャー泣いていた事だけは記憶している。"
        rule.setContent { VerticalText(text = longText, style = TextStyle(fontSize = 16.sp)) }
        rule.waitForIdle()
    }

    @Test
    fun spannedOverload_withMaxColumns_doesNotCrash() {
        val paint = TextPaint().apply { textSize = 30f }
        val text = SpannableString("テスト文字列テスト文字列テスト文字列") as Spanned
        rule.setContent { VerticalText(text = text, paint = paint, maxColumns = 1) }
        rule.waitForIdle()
    }
}
