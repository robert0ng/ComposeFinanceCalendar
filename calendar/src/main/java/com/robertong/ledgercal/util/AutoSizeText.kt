package com.robertong.ledgercal.util

// ORIGINAL AUTHOR: https://gist.github.com/inidamleader/b594d35362ebcf3cedf81055df519300
// LAST UPDATE: 10 April 2024 v4.3

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.robertong.ledgercal.util.SuggestedFontSizesStatus.Companion.rememberSuggestedFontSizesStatus
import kotlin.math.min

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: ImmutableWrapper<List<TextUnit>> = emptyList<TextUnit>().toImmutableWrapper(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = suggestedFontSizes.rememberSuggestedFontSizesStatus,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpacingRatio: Float = style.lineHeight.value / style.fontSize.value,
) {
    AutoSizeText(
        text = AnnotatedString(text),
        modifier = modifier,
        color = color,
        suggestedFontSizes = suggestedFontSizes,
        suggestedFontSizesStatus = suggestedFontSizesStatus,
        stepGranularityTextSize = stepGranularityTextSize,
        minTextSize = minTextSize,
        maxTextSize = maxTextSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        alignment = alignment,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        lineSpacingRatio = lineSpacingRatio,
    )
}

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: ImmutableWrapper<List<TextUnit>> = emptyList<TextUnit>().toImmutableWrapper(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = suggestedFontSizes.rememberSuggestedFontSizesStatus,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: ImmutableWrapper<Map<String, InlineTextContent>> = mapOf<String, InlineTextContent>().toImmutableWrapper(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpacingRatio: Float = style.lineHeight.value / style.fontSize.value,
) {
    CompositionLocalProvider(
        LocalDensity provides Density(density = LocalDensity.current.density, fontScale = 1F)
    ) {
        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = alignment,
        ) {
            val combinedTextStyle = LocalTextStyle.current + style.copy(
                color = color.takeIf { it.isSpecified } ?: style.color,
                fontStyle = fontStyle ?: style.fontStyle,
                fontWeight = fontWeight ?: style.fontWeight,
                fontFamily = fontFamily ?: style.fontFamily,
                letterSpacing = letterSpacing.takeIf { it.isSpecified } ?: style.letterSpacing,
                textDecoration = textDecoration ?: style.textDecoration,
                textAlign = when (alignment) {
                    Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> TextAlign.Start
                    Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> TextAlign.Center
                    Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> TextAlign.End
                    else -> TextAlign.Unspecified
                },
            )

            val layoutDirection = LocalLayoutDirection.current
            val density = LocalDensity.current
            val fontFamilyResolver = LocalFontFamilyResolver.current
            val textMeasurer = rememberTextMeasurer()
            val coercedLineSpacingRatio = lineSpacingRatio.takeIf { it.isFinite() && it >= 1 } ?: 1F
            val shouldMoveBackward: (TextUnit) -> Boolean = {
                shouldShrink(
                    text = text,
                    textStyle = combinedTextStyle.copy(
                        fontSize = it,
                        lineHeight = it * coercedLineSpacingRatio,
                    ),
                    maxLines = maxLines,
                    layoutDirection = layoutDirection,
                    softWrap = softWrap,
                    density = density,
                    fontFamilyResolver = fontFamilyResolver,
                    textMeasurer = textMeasurer
                )
            }

            val electedFontSize = kotlin.run {
                if (suggestedFontSizesStatus == SuggestedFontSizesStatus.VALID)
                    suggestedFontSizes.value
                else
                    remember(key1 = suggestedFontSizes) {
                        suggestedFontSizes.value
                            .filter { it.isSp }
                            .takeIf { it.isNotEmpty() }
                            ?.sortedBy { it.value }
                    }
            }
                ?.findElectedValue(shouldMoveBackward = shouldMoveBackward)
                ?: rememberCandidateFontSizesIntProgress(
                    density = density,
                    dpSize = DpSize(maxWidth, maxHeight),
                    maxTextSize = maxTextSize,
                    minTextSize = minTextSize,
                    stepGranularityTextSize = stepGranularityTextSize,
                ).findElectedValue(
                    transform = { with(density) { it.toSp() } },
                    shouldMoveBackward = shouldMoveBackward,
                )

            if (electedFontSize == 0.sp)
                Log.w(
                    "AutoSizeText",
                    """The text cannot be displayed. Please consider the following options:
                      |  1. Providing 'suggestedFontSizes' with smaller values that can be utilized.
                      |  2. Decreasing the 'stepGranularityTextSize' value.
                      |  3. Adjusting the 'minTextSize' parameter to a suitable value and ensuring the overflow parameter is set to "TextOverflow.Ellipsis".
                    """.trimMargin()
                )

            Text(
                text = text,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                inlineContent = inlineContent.value,
                onTextLayout = onTextLayout,
                style = combinedTextStyle.copy(
                    fontSize = electedFontSize,
                    lineHeight = electedFontSize * coercedLineSpacingRatio,
                ),
            )
        }
    }
}

private fun BoxWithConstraintsScope.shouldShrink(
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    layoutDirection: LayoutDirection,
    softWrap: Boolean,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    textMeasurer: TextMeasurer,
) = textMeasurer.measure(
    text = text,
    style = textStyle,
    overflow = TextOverflow.Clip,
    softWrap = softWrap,
    maxLines = maxLines,
    constraints = constraints,
    layoutDirection = layoutDirection,
    density = density,
    fontFamilyResolver = fontFamilyResolver,
).hasVisualOverflow

@Composable
private fun rememberCandidateFontSizesIntProgress(
    density: Density,
    dpSize: DpSize,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
): IntProgression {
    val max = remember(key1 = maxTextSize, key2 = dpSize, key3 = density) {
        val intSize = with(density) {
            IntSize(dpSize.width.roundToPx(), dpSize.height.roundToPx())
        }
        min(intSize.width, intSize.height).let { max ->
            maxTextSize
                .takeIf { it.isSp }
                ?.let { with(density) { it.roundToPx() } }
                ?.coerceIn(range = 0..max)
                ?: max
        }
    }

    val min = remember(key1 = minTextSize, key2 = max, key3 = density) {
        minTextSize
            .takeIf { it.isSp }
            ?.let { with(density) { it.roundToPx() } }
            ?.coerceIn(range = 0..max)
            ?: 0
    }

    val step = remember(
        stepGranularityTextSize,
        min,
        max,
        density,
    ) {
        stepGranularityTextSize
            .takeIf { it.isSp }
            ?.let { with(density) { it.roundToPx() } }
            ?.coerceIn(minimumValue = 1, maximumValue = (max - min).coerceAtLeast(1))
            ?: 1
    }

    return remember(key1 = min, key2 = max, key3 = step) {
        min..max step step
    }
}

fun <E> List<E>.findElectedValue(shouldMoveBackward: (E) -> Boolean) = run {
    indices.findElectedValue(
        transform = { this[it] },
        shouldMoveBackward = shouldMoveBackward,
    )
}

private fun <E> IntProgression.findElectedValue(
    transform: (Int) -> E,
    shouldMoveBackward: (E) -> Boolean,
) = run {
    var low = first / step
    var high = last / step
    while (low <= high) {
        val mid = low + (high - low) / 2
        if (shouldMoveBackward(transform(mid * step)))
            high = mid - 1
        else
            low = mid + 1
    }
    transform((high * step).coerceAtLeast(minimumValue = first * step))
}

enum class SuggestedFontSizesStatus {
    VALID, INVALID, UNKNOWN;

    companion object {
        val List<TextUnit>.suggestedFontSizesStatus
            get() = if (isNotEmpty() && all { it.isSp } && sortedBy { it.value } == this)
                VALID
            else
                INVALID
        val ImmutableWrapper<List<TextUnit>>.rememberSuggestedFontSizesStatus
            @Composable get() = remember(key1 = this) { value.suggestedFontSizesStatus }
    }
}
