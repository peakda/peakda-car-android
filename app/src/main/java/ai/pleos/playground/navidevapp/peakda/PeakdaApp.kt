package ai.pleos.playground.navidevapp.peakda

import ai.pleos.playground.navidevapp.peakda.components.DepthHeader
import ai.pleos.playground.navidevapp.peakda.components.WindowFrame
import ai.pleos.playground.navidevapp.peakda.components.peakdaClick
import ai.pleos.playground.navidevapp.peakda.screens.AutoRecordScreen
import ai.pleos.playground.navidevapp.peakda.screens.CalendarScreen
import ai.pleos.playground.navidevapp.peakda.screens.CompareScreen
import ai.pleos.playground.navidevapp.peakda.screens.ConfirmScreen
import ai.pleos.playground.navidevapp.peakda.screens.DetailScreen
import ai.pleos.playground.navidevapp.peakda.screens.DrivingScreen
import ai.pleos.playground.navidevapp.peakda.screens.EmptyScreen
import ai.pleos.playground.navidevapp.peakda.screens.HomeScreen
import ai.pleos.playground.navidevapp.peakda.screens.ListeningScreen
import ai.pleos.playground.navidevapp.peakda.screens.LoginScreen
import ai.pleos.playground.navidevapp.peakda.screens.NaviFailScreen
import ai.pleos.playground.navidevapp.peakda.screens.NetworkErrorScreen
import ai.pleos.playground.navidevapp.peakda.screens.RecordScreen
import ai.pleos.playground.navidevapp.peakda.screens.RegionScreen
import ai.pleos.playground.navidevapp.peakda.screens.ResumeScreen
import ai.pleos.playground.navidevapp.peakda.screens.SettingsScreen
import ai.pleos.playground.navidevapp.peakda.screens.StopLockScreen
import ai.pleos.playground.navidevapp.peakda.screens.ThinkingScreen
import ai.pleos.playground.navidevapp.peakda.screens.VoiceAskScreen
import ai.pleos.playground.navidevapp.peakda.screens.VoiceResultsScreen
import ai.pleos.playground.navidevapp.peakda.theme.Peakda
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaTheme
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaType
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * 검증·데모용 시작 옵션(adb 딥링크).
 * @param variant "b"/"c" = 03-b·03-c·05-b·05-c 상태로 시작
 */
data class StartOptions(
    val route: Route = Route.Home,
    val variant: String? = null,
    val fromDeepLink: Boolean = false,
)

@Composable
fun PeakdaApp(start: StartOptions, emulateWindow: Boolean, initialTheme: ai.pleos.playground.navidevapp.peakda.theme.ThemeMode) {
    val s = remember(start, initialTheme) {
        PeakdaState(start.route).apply {
            debugTheme = initialTheme.takeIf { it != ai.pleos.playground.navidevapp.peakda.theme.ThemeMode.System }
            keepToast = start.fromDeepLink
            applyVariant(start)
        }
    }

    // 토스트는 자동으로 사라진다(검증 딥링크에서는 유지)
    LaunchedEffect(s.toast) {
        if (s.toast != null && !s.keepToast) {
            delay(3000)
            s.toast = null
        }
    }
    BackHandler(enabled = s.stack.size > 1) { s.back() }

    val view = LocalView.current
    val lastBounds = remember { mutableStateOf("") }
    // 화면이 그려진 뒤 한 번 알린다(검증 스크립트가 이 줄을 기다렸다가 캡처한다)
    LaunchedEffect(s) {
        delay(1200)
        Log.i("PeakdaWindow", "bounds=${lastBounds.value}")
    }
    PeakdaTheme(s.debugTheme ?: s.themeMode) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .background(Peakda.colors.outsideWindow),
            contentAlignment = Alignment.Center,
        ) {
            // 에뮬레이터처럼 창이 628x855 와 다르면, 628x855 로 배치한 뒤 들어가도록 비율만 줄인다(배치는 Figma 와 같게 유지).
            val fit = minOf(1f, maxWidth.value / 628f, maxHeight.value / 855f)
            Box(
                Modifier
                    .then(
                        if (emulateWindow) {
                            Modifier
                                .requiredSize(628.dp, 855.dp)
                                .graphicsLayer { scaleX = fit; scaleY = fit }
                        } else {
                            Modifier.fillMaxSize()
                        }
                    )
                    .onGloballyPositioned {
                        // 검증 스크립트가 캡처 영역을 자르는 데 쓴다(화면 좌표)
                        val b = it.boundsInWindow()
                        val loc = IntArray(2).also { a -> view.getLocationOnScreen(a) }
                        lastBounds.value = "${loc[0] + b.left.toInt()},${loc[1] + b.top.toInt()}," +
                            "${loc[0] + b.right.toInt()},${loc[1] + b.bottom.toInt()}"
                    }
            ) {
                RouteContent(s, autoAdvance = !start.fromDeepLink)
            }
        }
    }
}

private fun PeakdaState.applyVariant(o: StartOptions) {
    when (o.route to o.variant) {
        Route.Detail to "b" -> { loggedIn = false; showToast("로그인하면 저장할 수 있어요") }
        Route.Detail to "c" -> { loggedIn = true; saved = true; showToast("저장했어요. 휴대폰에서 볼 수 있어요") }
        Route.Record to null -> { recordChoice = 2 }
        Route.Record to "b" -> { loggedIn = true; recordChoice = 2; recordDone = true; showToast("기록했어요. 고마워요!") }
        Route.Record to "c" -> { loggedIn = false; recordChoice = 2; recordDone = true; showToast("기록했어요. 로그인하면 내 기록에 남아요") }
        else -> Unit
    }
}

@Composable
private fun RouteContent(s: PeakdaState, autoAdvance: Boolean) {
    when (s.current) {
        Route.Home -> HomeScreen(s)
        Route.Compare -> CompareScreen(s)
        Route.Detail, Route.VoiceDetail -> DetailScreen(s)
        Route.Confirm -> ConfirmScreen(s)
        Route.Record -> RecordScreen(s)
        Route.Calendar -> CalendarScreen(s)
        Route.StopLock -> StopLockScreen(s)
        Route.Resume -> ResumeScreen(s)
        Route.Driving -> DrivingScreen(s)
        Route.Listening -> ListeningScreen(s)
        Route.Thinking -> ThinkingScreen(s, autoAdvance)
        Route.VoiceResults -> VoiceResultsScreen(s)
        Route.VoiceAsk -> VoiceAskScreen(s)
        Route.Login -> LoginScreen(s)
        Route.Settings -> SettingsScreen(s)
        Route.Region -> RegionScreen(s)
        Route.Empty -> EmptyScreen(s)
        Route.NetworkError -> NetworkErrorScreen(s)
        Route.NaviFail -> NaviFailScreen(s)
        Route.AutoRecord -> AutoRecordScreen(s)
        Route.DevList -> DevListScreen(s)
    }
}

/** 개발용 화면 목록(홈 로고 길게 누르기). Figma 에 없는 화면이라 실제 흐름에는 노출하지 않는다. */
@Composable
private fun DevListScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("화면 목록 (개발용)", onBack = s::back)
        LazyColumn(Modifier.weight(1f)) {
            items(Route.entries.filter { it != Route.DevList }) { r ->
                Text(
                    "${r.id}  ${r.label}",
                    style = PeakdaType.bodySmall,
                    color = c.textMain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .peakdaClick { s.go(r) }
                        .padding(vertical = 16.dp),
                )
            }
        }
    }
}
