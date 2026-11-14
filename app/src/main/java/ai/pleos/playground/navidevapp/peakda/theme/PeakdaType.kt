@file:OptIn(ExperimentalTextApi::class)

package ai.pleos.playground.navidevapp.peakda.theme

import ai.pleos.playground.navidevapp.R
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private fun asta(weight: Int) = Font(
    R.font.asta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Asta Sans (가변 폰트) — 본문·제목 전체 */
val AstaSans = FontFamily(asta(400), asta(500), asta(600), asta(700))

/** Pretendard — 뱃지·꽃 뱃지 */
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

/** Advent Pro Expanded SemiBold — "Peakda" 워드마크 */
val AdventProExpanded = FontFamily(
    Font(
        R.font.advent_pro,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.width(125f),
        ),
    ),
)

private val trimNone = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

/**
 * Figma 글자 스타일. 단위는 Figma px 그대로(PeakdaTheme 에서 1px = 1dp = 1sp 로 맞춘다).
 * @param lineHeight 배수(1.3 = 130%) 또는 px 값(>4 이면 px 로 본다)
 */
fun figmaText(
    family: FontFamily,
    weight: Int,
    size: Float,
    lineHeight: Float,
    letterSpacingPercent: Float = 0f,
): TextStyle = TextStyle(
    fontFamily = family,
    fontWeight = FontWeight(weight),
    fontSize = size.sp,
    lineHeight = (if (lineHeight > 4f) lineHeight else size * lineHeight).sp,
    letterSpacing = (letterSpacingPercent / 100f).em,
    lineHeightStyle = trimNone,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
)

object PeakdaType {
    // PLEOS 글자 스타일(신규 화면)
    val titleLargeBold = figmaText(AstaSans, 700, 40f, 52f)    // PLEOS / Title / Large / Bold
    val titleMediumBold = figmaText(AstaSans, 700, 36f, 44f)   // PLEOS / Title / Medium / Bold
    val titleSmallBold = figmaText(AstaSans, 700, 32f, 40f)    // PLEOS / Title / Small / Bold
    val bodySmall = figmaText(AstaSans, 400, 26f, 34f)         // PLEOS / Body / Small / Normal
    val bodySmallStrong = figmaText(AstaSans, 600, 26f, 34f)   // PLEOS / Body / Small / Strong
    val bodyXSmall = figmaText(AstaSans, 400, 24f, 28f)

    // PEAKDA 화면 스타일(01·02·03·05·12·21)
    val depthTitle = figmaText(AstaSans, 600, 36f, 44f)        // 헤더 제목(구형)
    val cardTitle = figmaText(AstaSans, 700, 32f, 1.3f)        // 추천 카드 이름
    val headline = figmaText(AstaSans, 700, 34f, 1.3f)         // "이 중에 어디로 갈까요?"
    val sectionTitle = figmaText(AstaSans, 700, 28f, 1.3f)
    val itemTitle = figmaText(AstaSans, 600, 28f, 1.3f)
    val statValue = figmaText(AstaSans, 700, 28f, 1.3f)
    val time = figmaText(AstaSans, 700, 26f, 1.3f)
    val value = figmaText(AstaSans, 600, 26f, 1.3f)
    val meta = figmaText(AstaSans, 400, 26f, 1.3f)
    val body = figmaText(AstaSans, 400, 26f, 1.3f)
    val caption = figmaText(AstaSans, 400, 24f, 1.3f)
    val captionStrong = figmaText(AstaSans, 600, 24f, 1.3f)
    val chip = figmaText(AstaSans, 600, 24f, 1.5f)
    val chipLabel = figmaText(AstaSans, 600, 24f, 1.0f)
    val count = figmaText(AstaSans, 600, 22f, 1.4f, 1f)
    val rank = figmaText(AstaSans, 700, 24f, 1.3f)
    val button = figmaText(AstaSans, 600, 28f, 1.4f)
    val toast = figmaText(AstaSans, 600, 28f, 36f)

    // 뱃지
    val badge = figmaText(Pretendard, 500, 24f, 1.4f, 1f)
    val flowerChip = figmaText(Pretendard, 600, 20f, 1.35f)

    // 워드마크
    val wordmark = figmaText(AdventProExpanded, 600, 53.33f, 1.0f, -4f)
}
