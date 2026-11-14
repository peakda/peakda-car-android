package ai.pleos.playground.navidevapp.peakda.screens

import ai.pleos.playground.navidevapp.R
import ai.pleos.playground.navidevapp.peakda.PeakdaState
import ai.pleos.playground.navidevapp.peakda.Route
import ai.pleos.playground.navidevapp.peakda.components.ButtonSize
import ai.pleos.playground.navidevapp.peakda.components.ButtonType
import ai.pleos.playground.navidevapp.peakda.components.CandidateCard
import ai.pleos.playground.navidevapp.peakda.components.CenterMessage
import ai.pleos.playground.navidevapp.peakda.components.CircleGraphic
import ai.pleos.playground.navidevapp.peakda.components.DepthHeader
import ai.pleos.playground.navidevapp.peakda.components.Fill
import ai.pleos.playground.navidevapp.peakda.components.PeakdaButton
import ai.pleos.playground.navidevapp.peakda.components.PeakdaIcons
import ai.pleos.playground.navidevapp.peakda.components.WindowFrame
import ai.pleos.playground.navidevapp.peakda.components.peakdaClick
import ai.pleos.playground.navidevapp.peakda.components.softCard
import ai.pleos.playground.navidevapp.peakda.theme.Peakda
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaType
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────── 07 · 09 글 두 줄만(터치 UI 없음)

@Composable
fun StopLockScreen(@Suppress("UNUSED_PARAMETER") s: PeakdaState) {
    WindowFrame(insideStroke = false) {
        CenterMessage("정차 중에도 잠겨요", "주차하면 이어서 볼 수 있어요", Modifier.weight(1f))
    }
}

@Composable
fun DrivingScreen(@Suppress("UNUSED_PARAMETER") s: PeakdaState) {
    WindowFrame(insideStroke = false) {
        CenterMessage("말로 물어보세요", "\"근처 단풍 명소 알려줘\"라고 말해보세요", Modifier.weight(1f))
    }
}

// ─────────────────────────────────────────────────────────── 08 주차 복귀 · 이어서 보기

@Composable
fun ResumeScreen(s: PeakdaState) {
    WindowFrame(insideStroke = false) {
        CenterMessage("이어서 볼까요?", "방금 보던 ${Dummy.gongseri.name}이에요", Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PeakdaButton("이어서 보기", { s.replace(Route.Detail) }, Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg)
            PeakdaButton("홈으로 가기", s::home, Modifier.fillMaxWidth(), ButtonType.Outline)
        }
    }
}

// ─────────────────────────────────────────────────────────── 10 음성 · 듣는 중

private val waveHeights = listOf(24, 48, 72, 40, 64, 32, 56)

@Composable
fun ListeningScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("음성 검색", onBack = s::back)
        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(c.brandPrimary)
                    .peakdaClick { s.replace(Route.Thinking) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                waveHeights.forEach { h ->
                    Box(
                        Modifier
                            .width(10.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(c.brandPrimary)
                    )
                }
            }
            Text("듣고 있어요", style = PeakdaType.titleSmallBold, color = c.textMain, textAlign = TextAlign.Center)
            Text("예) 근처 단풍 명소 알려줘", style = PeakdaType.bodySmall, color = c.textMainSecondary, textAlign = TextAlign.Center)
        }
        PeakdaButton("중단", s::back, Modifier.fillMaxWidth(), ButtonType.Outline)
    }
}

// ─────────────────────────────────────────────────────────── 11 음성 · 생각 중

@Composable
fun ThinkingScreen(s: PeakdaState, autoAdvance: Boolean) {
    val c = Peakda.colors
    if (autoAdvance) {
        LaunchedEffect(Unit) {
            delay(2500)
            s.replace(Route.VoiceResults)
        }
    }
    val rotation by rememberInfiniteTransition(label = "spin").animateFloat(
        0f, 360f, infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart), label = "spin",
    )
    WindowFrame(insideStroke = false) {
        DepthHeader("음성 검색", onBack = s::back)
        Column(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Canvas(
                Modifier
                    .size(120.dp)
                    .graphicsLayer { rotationZ = if (autoAdvance) rotation else 0f }
            ) {
                val w = 10.dp.toPx()
                val inset = w / 2
                val arcSize = androidx.compose.ui.geometry.Size(size.width - w, size.height - w)
                val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
                drawArc(c.popupStroke, 0f, 360f, false, topLeft, arcSize, style = Stroke(w))
                drawArc(c.brandPrimary, -90f, 160f, false, topLeft, arcSize, style = Stroke(w, cap = StrokeCap.Round))
            }
            Text("찾는 중이에요", style = PeakdaType.titleSmallBold, color = c.textMain, textAlign = TextAlign.Center)
            Text("근처 단풍 명소를 보고 있어요", style = PeakdaType.bodySmall, color = c.textMainSecondary, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(10.dp))
        PeakdaButton("중단", s::back, Modifier.fillMaxWidth(), ButtonType.Outline)
    }
}

// ─────────────────────────────────────────────────────────── 12 음성 · 후보 결과

@Composable
fun VoiceResultsScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame {
        DepthHeader("음성 검색", onBack = s::back, showMenu = true, pleosTitle = false)
        // 말한 문장
        Row(
            Modifier
                .fillMaxWidth()
                .softCard(9999.dp)
                .padding(start = 16.dp, top = 16.dp, end = 22.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(c.rankCircle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(PeakdaIcons.Mic, null, tint = c.white, modifier = Modifier.size(28.dp))
            }
            Text("“근처 은행나무 명소 알려줘”", style = PeakdaType.body, color = c.textPrimary, maxLines = 1)
        }
        Text("이 중에 어디로 갈까요?", style = PeakdaType.headline, color = c.textPrimary)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Dummy.candidates.take(3).forEachIndexed { i, p ->
                CandidateCard(
                    rank = i + 1, photo = p.photo, title = p.name, level = p.level, minutes = p.minutes,
                    showFlower = false, modifier = Modifier.width(552.dp).weight(1f),
                ) { s.go(Route.VoiceDetail) }
            }
        }
        PeakdaButton("다음 페이지", { s.go(Route.Compare) }, Modifier.fillMaxWidth(), ButtonType.Outline)
    }
}

// ─────────────────────────────────────────────────────────── 14 음성 · 추가 질문

@Composable
fun VoiceAskScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("음성 검색", onBack = s::back)
        Spacer(Modifier.height(10.dp))
        Text(
            "어느 지역에서 찾을까요?", style = PeakdaType.titleSmallBold, color = c.textMain,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "말하거나 눌러서 고르세요", style = PeakdaType.bodySmall, color = c.textMainSecondary,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("아산 근처", "충남 전체", "전국").forEach {
                PeakdaButton(it, { s.replace(Route.VoiceResults) }, Modifier.fillMaxWidth(), ButtonType.Outline)
            }
        }
        Fill()
    }
}

// ─────────────────────────────────────────────────────────── 15 로그인 (선택)

@Composable
fun LoginScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("로그인", onBack = s::back)
        CenterMessage(
            "휴대폰으로 연결해요",
            "로그인하면 기록과 즐겨찾기가 이어져요. 안 해도 모두 쓸 수 있어요",
            Modifier.weight(1f),
        ) {
            Box(
                Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(c.surfaceSoft)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("QR", style = PeakdaType.titleLargeBold, color = c.textMainTertiary)
            }
        }
        PeakdaButton("나중에 하기", s::back, Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg)
    }
}

// ─────────────────────────────────────────────────────────── 16 설정

@Composable
fun SettingsScreen(s: PeakdaState) {
    WindowFrame(insideStroke = false) {
        DepthHeader("설정", onBack = s::back)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsRow("화면 모드", "차량 설정을 따라요", s.themeMode.label) { s.cycleTheme() }
            SettingsRow("근처 추천 카드", "시동 걸 때 홈에 보여줘요", if (s.recommendCardOn) "켜짐" else "꺼짐") {
                s.recommendCardOn = !s.recommendCardOn
            }
            SettingsRow(
                "계정",
                if (s.loggedIn) "휴대폰과 연결했어요" else "로그인하지 않았어요",
                if (s.loggedIn) "해제" else "연결",
            ) { s.go(Route.Login) }
        }
        Fill()
    }
}

/** PLEOS 목록 행: radius 20, 20/24, 간격 16, background/secondary */
@Composable
fun SettingsRow(title: String, sub: String, value: String, onClick: () -> Unit) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(c.surfaceSoft)
            .peakdaClick(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = PeakdaType.bodySmallStrong, color = c.textMain)
            Text(sub, style = PeakdaType.bodyXSmall, color = c.textMainSecondary)
        }
        Text(value, style = PeakdaType.bodySmallStrong, color = c.textMain)
    }
}

// ─────────────────────────────────────────────────────────── 17 위치 권한 · 지역 선택

@Composable
fun RegionScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("지역 선택", onBack = s::back)
        Spacer(Modifier.height(10.dp))
        Text(
            "지역을 골라주세요", style = PeakdaType.titleSmallBold, color = c.textMain,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "내 위치를 쓰면 가까운 곳부터 보여드려요", style = PeakdaType.bodySmall, color = c.textMainSecondary,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("아산 근처" to "충남", "서울·경기" to "강원", "전북" to "경북").forEach { (a, b) ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PeakdaButton(a, { s.go(Route.Compare) }, Modifier.weight(1f), ButtonType.Outline)
                    PeakdaButton(b, { s.go(Route.Compare) }, Modifier.weight(1f), ButtonType.Outline)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        PeakdaButton("내 위치 사용", s::home, Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg)
        Fill()
    }
}

// ─────────────────────────────────────────────────────────── 18 · 19 · 20 오류

@Composable
private fun ErrorLayout(
    s: PeakdaState,
    header: String,
    title: String,
    body: String,
    graphic: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
) {
    WindowFrame(insideStroke = false) {
        DepthHeader(header, onBack = s::back)
        CenterMessage(title, body, Modifier.weight(1f), graphic)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { buttons() }
    }
}

@Composable
private fun Exclamation() = CircleGraphic {
    Text("!", style = PeakdaType.titleLargeBold, color = Peakda.colors.textMain)
}

@Composable
fun EmptyScreen(s: PeakdaState) = ErrorLayout(
    s, "안내", "아직 정보가 없어요", "예년에는 11월 첫째 주가 절정이었어요",
    graphic = { CircleGraphic { Image(painterResource(R.drawable.flower_cherry), null, Modifier.size(72.dp)) } },
) {
    PeakdaButton("다른 명소 보기", { s.go(Route.Compare) }, Modifier.fillMaxWidth(), ButtonType.Outline)
}

@Composable
fun NetworkErrorScreen(s: PeakdaState) = ErrorLayout(
    s, "안내", "연결이 불안정해요", "저장된 마지막 정보를 보여드려요 (1시간 전)", graphic = { Exclamation() },
) {
    PeakdaButton("다시 시도", s::back, Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg)
    PeakdaButton("저장된 정보 보기", s::home, Modifier.fillMaxWidth(), ButtonType.Outline)
}

@Composable
fun NaviFailScreen(s: PeakdaState) = ErrorLayout(
    s, "길 안내", "내비에 보내지 못했어요", "연결을 확인하고 다시 시도해 주세요", graphic = { Exclamation() },
) {
    PeakdaButton("다시 시도", s::back, Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg)
}
