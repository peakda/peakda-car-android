package ai.pleos.playground.navidevapp

import ai.pleos.playground.navidevapp.peakda.PeakdaApp
import ai.pleos.playground.navidevapp.peakda.Route
import ai.pleos.playground.navidevapp.peakda.StartOptions
import ai.pleos.playground.navidevapp.peakda.theme.ThemeMode
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

/**
 * PEAKDA 화면 UI.
 * 검증용 딥링크(디버그):
 *   adb shell am start -n ai.pleos.playground.sdk.navidevapp/ai.pleos.playground.navidevapp.MainActivity \
 *     --es screen 03 --es variant b --es theme dark
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        options.value = readOptions(intent)
        setContent {
            val (start, theme) = options.value
            // 디버그 빌드에서는 큰 화면(에뮬레이터)에서도 628x855 창 크기로 보여준다.
            PeakdaApp(start = start, emulateWindow = BuildConfig.DEBUG, initialTheme = theme)
        }
    }

    /** 검증 스크립트가 앱을 다시 띄우지 않고 화면만 바꿀 수 있게 한다(singleTop). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        options.value = readOptions(intent)
    }

    private val options = mutableStateOf(StartOptions() to ThemeMode.System)

    private fun readOptions(intent: Intent): Pair<StartOptions, ThemeMode> {
        val screenId = intent.getStringExtra("screen")
        val start = StartOptions(
            route = Route.fromId(screenId) ?: Route.Home,
            variant = intent.getStringExtra("variant"),
            fromDeepLink = screenId != null,
        )
        val theme = when (intent.getStringExtra("theme")) {
            "dark" -> ThemeMode.Dark
            "light" -> ThemeMode.Light
            else -> ThemeMode.System
        }
        return start to theme
    }
}
