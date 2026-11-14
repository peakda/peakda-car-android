package ai.pleos.playground.navidevapp.peakda.screens

import ai.pleos.playground.navidevapp.R
import ai.pleos.playground.navidevapp.peakda.PeakdaState
import ai.pleos.playground.navidevapp.peakda.Route
import ai.pleos.playground.navidevapp.peakda.components.ButtonSize
import ai.pleos.playground.navidevapp.peakda.components.ButtonType
import ai.pleos.playground.navidevapp.peakda.components.CandidateCard
import ai.pleos.playground.navidevapp.peakda.components.CountPill
import ai.pleos.playground.navidevapp.peakda.components.DepthHeader
import ai.pleos.playground.navidevapp.peakda.components.Fill
import ai.pleos.playground.navidevapp.peakda.components.FlowerChip
import ai.pleos.playground.navidevapp.peakda.components.HomeHeader
import ai.pleos.playground.navidevapp.peakda.components.IconText
import ai.pleos.playground.navidevapp.peakda.components.PagePill
import ai.pleos.playground.navidevapp.peakda.components.PeakBadge
import ai.pleos.playground.navidevapp.peakda.components.PeakLevel
import ai.pleos.playground.navidevapp.peakda.components.PeakdaButton
import ai.pleos.playground.navidevapp.peakda.components.PeakdaIcons
import ai.pleos.playground.navidevapp.peakda.components.PeakdaToast
import ai.pleos.playground.navidevapp.peakda.components.PhotoBox
import ai.pleos.playground.navidevapp.peakda.components.PillLink
import ai.pleos.playground.navidevapp.peakda.components.PlaceListRow
import ai.pleos.playground.navidevapp.peakda.components.RadiusChip
import ai.pleos.playground.navidevapp.peakda.components.WindowFrame
import ai.pleos.playground.navidevapp.peakda.components.peakdaClick
import ai.pleos.playground.navidevapp.peakda.components.scrim
import ai.pleos.playground.navidevapp.peakda.components.softCard
import ai.pleos.playground.navidevapp.peakda.components.strokeBox
import ai.pleos.playground.navidevapp.peakda.navi.NaviPlace
import ai.pleos.playground.navidevapp.peakda.navi.NaviResult
import ai.pleos.playground.navidevapp.peakda.theme.Peakda
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────── 더미 데이터

data class Place(
    val name: String,
    val region: String,
    val photo: Int,
    val level: PeakLevel,
    val minutes: Int,
    /** 내비 안내용 좌표·주소 (대략 값) */
    val latitude: Double,
    val longitude: Double,
    val address: String,
) {
    fun toNaviPlace() = NaviPlace(name, latitude, longitude, address)
}

object Dummy {
    val gongseri = Place(
        "공세리 성당 은행나무길", "충남 아산시", R.drawable.photo_gongseri, PeakLevel.Peak, 18,
        36.8835, 126.9131, "충남 아산시 인주면 공세리성당길 10",
    )
    val hyeonchungsa = Place(
        "현충사 은행나무길", "충남 아산시", R.drawable.photo_hyeonchungsa, PeakLevel.Starting, 24,
        36.8127, 127.0266, "충남 아산시 염치읍 현충사길 126",
    )
    val gokgyocheon = Place(
        "곡교천 은행나무길", "충남 아산시", R.drawable.photo_gongseri, PeakLevel.Early, 31,
        36.7963, 127.0322, "충남 아산시 염치읍 백암리",
    )
    val oeam = Place(
        "외암민속마을", "충남 아산시", R.drawable.photo_hyeonchungsa, PeakLevel.Starting, 35,
        36.7285, 127.0175, "충남 아산시 송악면 외암민속길 5",
    )
    val shinjeongho = Place(
        "신정호 은행나무길", "충남 아산시", R.drawable.photo_gongseri, PeakLevel.Early, 40,
        36.7684, 126.9925, "충남 아산시 신정로 616",
    )
    val candidates = listOf(gongseri, hyeonchungsa, gokgyocheon, oeam, shinjeongho)
}

// ─────────────────────────────────────────────────────────── 01 홈 · 지금 가장 예쁜 곳

@Composable
fun HomeScreen(s: PeakdaState) {
    val c = Peakda.colors
    val p = Dummy.gongseri
    WindowFrame(overlay = { s.toast?.let { PeakdaToast(it) } }) {
        HomeHeader(onMenu = { s.go(Route.Settings) }, onLogoLongPress = { s.go(Route.DevList) })
        // 추천 카드 (radius 28, 여백 20, 간격 14)
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .softCard(28.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PhotoBox(
                p.photo,
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .heightIn(min = 120.dp),
                20.dp,
            ) {
                Box(Modifier.fillMaxSize().background(scrim(0.28f, 0f)))
                Row(
                    Modifier.padding(start = 16.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PeakBadge(p.level)
                    FlowerChip()
                }
            }
            Text(p.name, style = PeakdaType.cardTitle, color = c.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            IconText(PeakdaIcons.Clock, "${p.minutes}분", PeakdaType.meta, c.textSecondary)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PeakdaButton("자세히", { s.select(p); s.go(Route.Detail) }, Modifier.weight(1f), ButtonType.Outline)
                PeakdaButton("안내시작", { s.select(p); s.go(Route.Confirm) }, Modifier.weight(1f), ButtonType.Primary)
            }
        }
        // 섹션 헤더
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("근처 다른 곳", style = PeakdaType.sectionTitle, color = c.textPrimary)
            PillLink("후보 3곳 비교") { s.go(Route.Compare) }
        }
        val o = Dummy.hyeonchungsa
        PlaceListRow(o.photo, o.name, o.level, o.minutes) { s.select(o); s.go(Route.Detail) }
    }
}

// ─────────────────────────────────────────────────────────── 02 후보 비교 (3곳씩)

@Composable
fun CompareScreen(s: PeakdaState) {
    val c = Peakda.colors
    var radius by remember { mutableIntStateOf(30) }
    var page by remember { mutableIntStateOf(0) }
    val all = Dummy.candidates
    val pageItems = all.drop(page * 3).take(3)
    val lastPage = (all.size - 1) / 3
    WindowFrame {
        DepthHeader("후보 비교", onBack = s::back, showMenu = true, pleosTitle = false)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("차로", style = PeakdaType.chipLabel, color = c.textSecondary)
                listOf(15, 30, 60).forEach { m ->
                    RadiusChip("${m}분", selected = radius == m) { radius = m }
                }
            }
            val from = page * 3 + 1
            val to = page * 3 + pageItems.size
            PagePill("$from–$to / ${all.size}곳")
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            pageItems.forEachIndexed { i, p ->
                CandidateCard(
                    rank = page * 3 + i + 1, photo = p.photo, title = p.name, level = p.level,
                    minutes = p.minutes, showFlower = true,
                    modifier = Modifier.width(552.dp).weight(1f),
                ) { s.select(p); s.go(Route.Detail) }
            }
            repeat(3 - pageItems.size) { Spacer(Modifier.weight(1f)) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PeakdaButton("‹ 이전", { page-- }, Modifier.weight(1f), ButtonType.Outline, enabled = page > 0)
            val remain = all.size - (page + 1) * 3
            PeakdaButton(
                if (remain > 0) "다음 ${remain}곳 ›" else "다음 ›",
                { page++ }, Modifier.weight(1f), ButtonType.Primary, enabled = page < lastPage,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────── 03 명소 상세 (+03-b, 03-c, 13)

@Composable
fun DetailScreen(s: PeakdaState) {
    val c = Peakda.colors
    val p = s.selected
    WindowFrame(overlay = { s.toast?.let { PeakdaToast(it) } }) {
        DepthHeader("명소 상세", onBack = s::back, showMenu = true, pleosTitle = false)
        // 히어로
        PhotoBox(
            p.photo,
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(min = 150.dp),
            24.dp,
        ) {
            Box(Modifier.fillMaxSize().background(scrim(0.05f, 0.70f)))
            Row(Modifier.padding(start = 16.dp, top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PeakBadge(p.level)
                FlowerChip()
            }
            HeartButton(
                filled = s.loggedIn && s.saved,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp),
            ) {
                if (!s.loggedIn) {
                    s.showToast("로그인하면 저장할 수 있어요")
                } else {
                    s.saved = !s.saved
                    if (s.saved) s.showToast("저장했어요. 휴대폰에서 볼 수 있어요")
                }
            }
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(p.name, style = PeakdaType.cardTitle, color = c.white, maxLines = 1)
                Text("${p.region} · 은행나무", style = PeakdaType.caption, color = c.white, maxLines = 1)
            }
        }
        // 정보 3칸
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(PeakdaIcons.Users, "9/12", "방문자 절정", Modifier.weight(1f))
            StatTile(PeakdaIcons.Sun, "14°", "맑음", Modifier.weight(1f))
            StatTile(PeakdaIcons.Car, "${p.minutes}분", "차로", Modifier.weight(1f))
        }
        // 방문자 기록
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("방문자 기록", style = PeakdaType.sectionTitle, color = c.textPrimary)
                CountPill(2)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("전체", style = PeakdaType.captionStrong, color = c.textSecondary)
                Icon(PeakdaIcons.ChevronRight, null, tint = c.iconSecondary, modifier = Modifier.size(24.dp))
            }
        }
        VisitRecordRow(R.drawable.photo_gongseri, "예림 · 어제", "잎이 노랗게 물들었어요", PeakLevel.Peak)
        VisitRecordRow(R.drawable.photo_hyeonchungsa, "민준 · 3일 전", "아직 초록이 조금 남아 있어요", PeakLevel.Starting)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PeakdaButton("경유지로 추가", { s.go(Route.Confirm) }, Modifier.weight(1f), ButtonType.Outline)
            PeakdaButton("목적지로 설정", { s.go(Route.Confirm) }, Modifier.weight(1f), ButtonType.Primary)
        }
    }
}

/** icon_Button(하트): 64 원, Background/Secondary, 여백 16 */
@Composable
fun HeartButton(filled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = Peakda.colors
    Box(
        modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(c.bgSecondary)
            .peakdaClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (filled) PeakdaIcons.HeartFilled else PeakdaIcons.Heart,
            if (filled) "저장됨" else "저장",
            tint = if (filled) c.pink300 else c.iconTertiary,
            modifier = Modifier.size(32.dp),
        )
    }
}

/** 정보 칸: radius 20, 14/14/14/16, 간격 2 */
@Composable
fun StatTile(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    val c = Peakda.colors
    Column(
        modifier
            .softCard(20.dp)
            .padding(start = 16.dp, top = 14.dp, end = 14.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        IconText(icon, value, PeakdaType.statValue, c.textPrimary, iconTint = c.colorSecondary)
        Text(label, style = PeakdaType.caption, color = c.textSecondary, maxLines = 1)
    }
}

/** 방문자 기록 행: radius 22, 12/16/12/12, 간격 14 */
@Composable
fun VisitRecordRow(photo: Int, who: String, memo: String, level: PeakLevel) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .softCard(22.dp)
            .padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PhotoBox(photo, Modifier.size(72.dp), 16.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(who, style = PeakdaType.value, color = c.textPrimary, maxLines = 1)
            Text(memo, style = PeakdaType.caption, color = c.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        PeakBadge(level)
    }
}

// ─────────────────────────────────────────────────────────── 04 경유지 · 목적지 확인

@Composable
fun ConfirmScreen(s: PeakdaState) {
    val c = Peakda.colors
    val p = s.selected
    val busy = s.naviBusy
    fun handle(result: NaviResult) {
        s.naviBusy = false
        when (result) {
            NaviResult.WaypointAdded -> { s.home(); s.showToast("경유지로 추가했어요") }
            is NaviResult.RouteStarted -> {
                s.home()
                s.showToast(if (result.fromWaypoint) "안내 중인 길이 없어 목적지로 안내해요" else "길 안내를 시작했어요")
            }
            NaviResult.Sent -> { s.home(); s.showToast("내비에 보냈어요. 내비를 확인해 주세요") }
            is NaviResult.Failed -> s.go(Route.NaviFail)
        }
    }
    WindowFrame(insideStroke = false) {
        DepthHeader(p.name, onBack = s::back)
        Spacer(Modifier.height(10.dp))
        val shape = RoundedCornerShape(28.dp)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(c.popupBg)
                .strokeBox(2.dp, c.popupStroke, shape)
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("길 안내에 어떻게 추가할까요?", style = PeakdaType.titleSmallBold, color = c.textMain)
            Text("지금 가는 길에 들르거나, 새 목적지로 바꿀 수 있어요.", style = PeakdaType.bodySmall, color = c.textMainSecondary)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PeakdaButton(
                    if (busy) "내비에 보내는 중…" else "경유지로 추가",
                    {
                        val navi = s.navi ?: return@PeakdaButton s.go(Route.NaviFail)
                        s.naviBusy = true
                        navi.addWaypoint(p.toNaviPlace(), ::handle)
                    },
                    Modifier.fillMaxWidth(), ButtonType.Primary, ButtonSize.Lg, enabled = !busy,
                )
                PeakdaButton(
                    "목적지로 설정",
                    {
                        val navi = s.navi ?: return@PeakdaButton s.go(Route.NaviFail)
                        s.naviBusy = true
                        navi.setDestination(p.toNaviPlace(), ::handle)
                    },
                    Modifier.fillMaxWidth(), ButtonType.Outline, enabled = !busy,
                )
                PeakdaButton("취소", s::back, Modifier.fillMaxWidth(), ButtonType.Outline, enabled = !busy)
            }
        }
        Fill()
    }
}
