package ai.pleos.playground.navidevapp.peakda.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Figma 변수 두 모음을 그대로 옮긴 색 토큰.
 *  - 소문자 이름(background/primary, text/primary …) = "Main UI" 모음
 *  - 대문자 이름(Background/Primary, Text/Primary …) = "PLEOS / Color" 모음
 * Light/Dark 는 같은 화면에서 모드만 바꾼다(⑬ 전제).
 */
@Immutable
data class PeakdaColors(
    val isDark: Boolean,
    // Main UI
    val windowBg: Color,            // background/primary
    val surfaceSoft: Color,         // background/secondary (헤더 아이콘 버튼, 설정 행, QR 자리)
    val popupBg: Color,             // background/popup
    val popupStroke: Color,         // basic/200
    val textMain: Color,            // text/primary
    val textMainSecondary: Color,   // text/secondary (84%)
    val textMainTertiary: Color,    // text/tertiary (64%)
    // PLEOS / Color
    val bgPrimary: Color,           // Background/Primary (Outline 버튼 바탕)
    val bgSecondary: Color,         // Background/Secondary (카드, 목록 행, 알약)
    val borderPrimary: Color,       // Border/Primary
    val borderSecondary: Color,     // Border/Secondary
    val textPrimary: Color,         // Text/Primary
    val textSecondary: Color,       // Text/Secondary
    val textTertiary: Color,        // Text/Tertiary
    val textInverse: Color,         // Text/Primary inverse
    val iconSecondary: Color,       // Icon/Secondary
    val iconTertiary: Color,        // Icon/Tertiary
    val colorPrimary: Color,        // Colors/Primary (분홍)
    val colorSecondary: Color,      // Colors/Secondary (초록)
    val colorTertiary: Color,       // Colors/Tertiary (선택 칸 바탕, 이르다 뱃지 바탕)
    // PEAKDA
    val wordmark: Color,            // peakda/brand/wordmark
    val brandPrimary: Color,        // peakda/brand/primary (음성 마이크·파형)
    val rankCircle: Color,          // peakda/button/primary/enabled
    val chartTrack: Color,
    val outsideWindow: Color,       // 창 바깥(모서리 밖) — 검증 화면용
    // 공통(모드 무관)
    val pink300: Color = Color(0xFFFF7F92),
    val pink400: Color = Color(0xFFF7576B),
    val pink50: Color = Color(0xFFFFF0F2),
    val green50: Color = Color(0xFFF0F8E8),   // 이르다 뱃지 바탕(모드 무관)
    val sunflower: Color = Color(0xFFFFB800),
    val sunflowerChipBg: Color = Color(0xFFE0A800).copy(alpha = 0.20f),
    val white: Color = Color(0xFFFFFFFF),
    val toastBg: Color = Color(0xFF131417).copy(alpha = 0.84f),
)

val LightPeakdaColors = PeakdaColors(
    isDark = false,
    windowBg = Color(0xFFFFFFFF),
    surfaceSoft = Color(0xFFF7F8FA),
    popupBg = Color(0xFFFFFFFF),
    popupStroke = Color(0xFFE0E1E6),
    textMain = Color(0xFF131417),
    textMainSecondary = Color(0xFF131417).copy(alpha = 0.84f),
    textMainTertiary = Color(0xFF131417).copy(alpha = 0.64f),
    bgPrimary = Color(0xFFFFFFFF),
    bgSecondary = Color(0xFFF8F9FB),
    borderPrimary = Color(0xFFF0F2F5),
    borderSecondary = Color(0xFFD0D4DB),
    textPrimary = Color(0xFF1A1D24),
    textSecondary = Color(0xFF4E5666),
    textTertiary = Color(0xFF8C95A4),
    textInverse = Color(0xFFF8F9FB),
    iconSecondary = Color(0xFF4E5666),
    iconTertiary = Color(0xFF8C95A4),
    colorPrimary = Color(0xFFFF7F92),
    colorSecondary = Color(0xFF8DC468),
    colorTertiary = Color(0xFFF0F8E8),
    wordmark = Color(0xFF285011),
    brandPrimary = Color(0xFFEC3248),
    rankCircle = Color(0xFF4A8E24),
    chartTrack = Color(0xFFE8EAEE),
    outsideWindow = Color(0xFFFFFFFF),
)

val DarkPeakdaColors = PeakdaColors(
    isDark = true,
    windowBg = Color(0xFF313236),
    surfaceSoft = Color(0xFF1D1E21),
    popupBg = Color(0xFF131417),
    popupStroke = Color(0xFF44464E),
    textMain = Color(0xFFFFFFFF),
    textMainSecondary = Color(0xFFFFFFFF).copy(alpha = 0.84f),
    textMainTertiary = Color(0xFFFFFFFF).copy(alpha = 0.64f),
    bgPrimary = Color(0xFF2C313A),
    bgSecondary = Color(0xFF1A1D24),
    borderPrimary = Color(0xFF363D4A),
    borderSecondary = Color(0xFF4E5666),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA8B0BC),
    textTertiary = Color(0xFF8C95A4),
    textInverse = Color(0xFFF8F9FB),
    iconSecondary = Color(0xFFA8B0BC),
    iconTertiary = Color(0xFF8C95A4),
    colorPrimary = Color(0xFFFF7F92),
    colorSecondary = Color(0xFF8DC468),
    colorTertiary = Color(0xFF1A3608),
    wordmark = Color(0xFFA9D188),
    brandPrimary = Color(0xFFFF7F92),
    rankCircle = Color(0xFF8DC468),
    chartTrack = Color(0xFF363D4A),
    outsideWindow = Color(0xFF131417),
)

val LocalPeakdaColors = staticCompositionLocalOf { LightPeakdaColors }
