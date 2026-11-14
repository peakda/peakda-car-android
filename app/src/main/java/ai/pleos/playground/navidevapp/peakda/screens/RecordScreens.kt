package ai.pleos.playground.navidevapp.peakda.screens

import ai.pleos.playground.navidevapp.peakda.PeakdaState
import ai.pleos.playground.navidevapp.peakda.components.ButtonSize
import ai.pleos.playground.navidevapp.peakda.components.ButtonType
import ai.pleos.playground.navidevapp.peakda.components.DepthHeader
import ai.pleos.playground.navidevapp.peakda.components.Fill
import ai.pleos.playground.navidevapp.peakda.components.PeakdaButton
import ai.pleos.playground.navidevapp.peakda.components.PeakdaToast
import ai.pleos.playground.navidevapp.peakda.components.PlaceSummaryRow
import ai.pleos.playground.navidevapp.peakda.components.WindowFrame
import ai.pleos.playground.navidevapp.peakda.theme.Peakda
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val levels = listOf("이르다", "시작", "절정", "늦었다")

/** 4칸 선택(2x2, lg 96, 간격 14). 고른 칸은 Selected(초록 바탕 + 굵은 테두리) */
@Composable
private fun LevelGrid(selected: Int?, modifier: Modifier = Modifier, onPick: (Int) -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)) {
        for (row in 0..1) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                for (col in 0..1) {
                    val i = row * 2 + col
                    PeakdaButton(
                        levels[i], { onPick(i) }, Modifier.weight(1f),
                        if (selected == i) ButtonType.Selected else ButtonType.Outline,
                        ButtonSize.Lg,
                    )
                }
            }
        }
    }
}

@Composable
private fun Headline(title: String, body: String) {
    val c = Peakda.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = PeakdaType.headline, color = c.textPrimary)
        Text(body, style = PeakdaType.meta, color = c.textSecondary)
    }
}

// ─────────────────────────────────────────────────────────── 05 절정도 기록 (+05-b, 05-c)

@Composable
fun RecordScreen(s: PeakdaState) {
    val p = Dummy.gongseri
    WindowFrame(overlay = { s.toast?.let { PeakdaToast(it) } }) {
        DepthHeader("절정도 기록", onBack = s::back, showMenu = true, pleosTitle = false)
        PlaceSummaryRow(p.photo, p.name, "${p.region} · 지금 방문 중")
        Headline("지금, 얼마나 예뻐요?", "본 대로 하나만 골라주세요")
        LevelGrid(s.recordChoice, Modifier.fillMaxWidth().weight(1f)) {
            if (!s.recordDone) s.recordChoice = it
        }
        Box(Modifier.fillMaxWidth().height(96.dp)) {
            // 기록 뒤에는 이 자리에 토스트가 겹쳐 뜬다(05-b, 05-c)
            if (!s.recordDone) {
                PeakdaButton(
                    "기록하기",
                    {
                        s.recordDone = true
                        s.showToast(
                            if (s.loggedIn) "기록했어요. 고마워요!"
                            else "기록했어요. 로그인하면 내 기록에 남아요"
                        )
                    },
                    Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg,
                    enabled = s.recordChoice != null,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────── 21 출발 시 자동 기록

@Composable
fun AutoRecordScreen(s: PeakdaState) {
    val c = Peakda.colors
    val p = Dummy.gongseri
    WindowFrame {
        DepthHeader("방금 다녀온 곳", onBack = s::back, showMenu = true, pleosTitle = false)
        PlaceSummaryRow(p.photo, p.name, "${p.region} · 방금 출발")
        Headline("지금, 얼마나 예뻤어요?", "한 번 누르면 바로 기록돼요")
        // 한 번 누르면 바로 기록되고 닫힌다(별도 저장 버튼 없음)
        LevelGrid(null, Modifier.fillMaxWidth().weight(1f)) { s.home() }
        Text(
            "차가 움직이면 자동으로 닫혀요",
            style = PeakdaType.bodyXSmall,
            color = c.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        PeakdaButton("나중에 할게요", s::back, Modifier.fillMaxWidth(), ButtonType.Outline, ButtonSize.Lg)
    }
}

// ─────────────────────────────────────────────────────────── 06 예측 캘린더

private data class WeekBar(val label: String, val ratio: Float, val peak: Boolean = false)

private val sampleWeeks = listOf(
    WeekBar("10월 2주", 0.097f),
    WeekBar("10월 3주", 0.292f),
    WeekBar("10월 4주", 0.648f, peak = true),
    WeekBar("11월 1주", 0.356f),
    WeekBar("11월 2주", 0.130f),
)

@Composable
fun CalendarScreen(s: PeakdaState) {
    val c = Peakda.colors
    WindowFrame(insideStroke = false) {
        DepthHeader("예측 캘린더", onBack = s::back)
        Column(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("예년 절정은 10월 4주예요", style = PeakdaType.titleSmallBold, color = c.textMain)
            Text("올해는 예년보다 2일 일러요 (샘플 값)", style = PeakdaType.bodySmall, color = c.textMainSecondary)
            Column(
                Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                sampleWeeks.forEach { w ->
                    Row(
                        Modifier.fillMaxWidth().height(34.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(w.label, style = PeakdaType.value, color = c.textMain, modifier = Modifier.width(132.dp))
                        Box(
                            Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(CircleShape)
                                .background(c.chartTrack)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(w.ratio)
                                    .clip(CircleShape)
                                    .background(if (w.peak) c.colorPrimary else c.colorSecondary)
                            )
                        }
                    }
                }
            }
        }
    }
}
