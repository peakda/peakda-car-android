package ai.pleos.playground.navidevapp.peakda.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Density

enum class ThemeMode(val label: String) { System("따르기"), Light("라이트"), Dark("다크") }

/**
 * PEAKDA 테마.
 * Figma 는 Connect-L(1920x1080) 실제 화소 기준이라, 1 Figma px = 1 px 이 되도록 밀도를 1로 고정한다.
 * 그래서 코드의 dp/sp 값은 Figma 숫자를 그대로 쓴다. 글자 크기 배율도 1로 고정(창이 좁아져도 줄이지 않음).
 */
@Composable
fun PeakdaTheme(
    mode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    CompositionLocalProvider(
        LocalPeakdaColors provides if (dark) DarkPeakdaColors else LightPeakdaColors,
        androidx.compose.ui.platform.LocalDensity provides Density(density = 1f, fontScale = 1f),
        content = content,
    )
}

object Peakda {
    val colors: PeakdaColors
        @Composable get() = LocalPeakdaColors.current
}
