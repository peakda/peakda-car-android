package ai.pleos.playground.navidevapp.peakda.components

import ai.pleos.playground.navidevapp.R
import ai.pleos.playground.navidevapp.peakda.theme.Peakda
import ai.pleos.playground.navidevapp.peakda.theme.PeakdaType
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────── 창 프레임

/**
 * PEAKDA 창(628x855) 안쪽 프레임. radius 24, 테두리 2, 안쪽 여백 24.
 * @param insideStroke true = 테두리가 안쪽(01·02·03·05·12·21, 내용 폭 576),
 *                     false = 테두리가 바깥(04·06~11·14~20, 내용 폭 580)
 */
@Composable
fun WindowFrame(
    modifier: Modifier = Modifier,
    insideStroke: Boolean = true,
    gap: Dp = if (insideStroke) 14.dp else 24.dp,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = Peakda.colors
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier
            .fillMaxSize()
            .clip(shape)
            .background(c.windowBg)
            .border(2.dp, c.borderSecondary, shape)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(if (insideStroke) 26.dp else 24.dp),
            verticalArrangement = Arrangement.spacedBy(gap),
            content = content,
        )
        overlay()
    }
}

// ─────────────────────────────────────────────────────────── 클릭

@Composable
fun Modifier.peakdaClick(enabled: Boolean = true, onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = androidx.compose.material.ripple.rememberRipple(),
        enabled = enabled,
        role = Role.Button,
        onClick = onClick,
    )

/** 길게 누르기(개발용 화면 목록 진입 등 숨은 동작) */
fun Modifier.peakdaLongPress(onLongPress: () -> Unit): Modifier =
    this.pointerInput(Unit) {
        detectTapGestures(onLongPress = { onLongPress() })
    }

// ─────────────────────────────────────────────────────────── 헤더

/** 82x82, radius 24, background/secondary 바탕의 헤더 아이콘 버튼 */
@Composable
fun HeaderIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val c = Peakda.colors
    Box(
        Modifier
            .size(82.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(c.surfaceSoft)
            .peakdaClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = c.textPrimary, modifier = Modifier.size(36.dp))
    }
}

/** 홈 헤더(로고 + 워드마크). 576x96. Figma 01 에서는 메뉴 표시가 꺼져 있다(메뉴 표시 = false). */
@Composable
fun HomeHeader(onMenu: () -> Unit, onLogoLongPress: () -> Unit = {}, showMenu: Boolean = false) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .height(96.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.22.dp),
            modifier = Modifier.peakdaLongPress(onLogoLongPress),
        ) {
            Image(
                painterResource(R.drawable.peakda_logo), "PEAKDA",
                modifier = Modifier.size(61.dp, 56.89.dp),
                contentScale = ContentScale.Fit,
            )
            Text("Peakda", style = PeakdaType.wordmark, color = c.wordmark)
        }
        if (showMenu) HeaderIconButton(PeakdaIcons.More, "메뉴", onMenu)
    }
}

/**
 * 내비 / Depth 헤더. 96 높이, 간격 16: [뒤로] 제목 [메뉴]
 * @param pleosTitle true = PLEOS Title/Medium/Bold(text/primary), false = 구형 Asta 600(Text/Primary)
 */
@Composable
fun DepthHeader(
    title: String,
    onBack: () -> Unit,
    showMenu: Boolean = false,
    pleosTitle: Boolean = true,
    onMenu: () -> Unit = {},
) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .height(96.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeaderIconButton(PeakdaIcons.ChevronLeft, "뒤로", onBack)
        Text(
            title,
            style = if (pleosTitle) PeakdaType.titleMediumBold else PeakdaType.depthTitle,
            color = if (pleosTitle) c.textMain else c.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (showMenu) HeaderIconButton(PeakdaIcons.More, "메뉴", onMenu)
    }
}

// ─────────────────────────────────────────────────────────── 버튼

enum class ButtonType { Primary, Outline, Selected }
enum class ButtonSize(val height: Dp, val radius: Dp) { Md(80.dp, 24.dp), Lg(96.dp, 32.dp) }

/** PLEOS button. md 80 / lg 96, 좌우 여백 32, 글자 Asta 600 28 */
@Composable
fun PeakdaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.Outline,
    size: ButtonSize = ButtonSize.Md,
    enabled: Boolean = true,
) {
    val c = Peakda.colors
    val shape = RoundedCornerShape(size.radius)
    val bg = when (type) {
        ButtonType.Primary -> c.colorSecondary
        ButtonType.Outline -> c.bgPrimary
        ButtonType.Selected -> c.colorTertiary
    }
    val textColor = when (type) {
        ButtonType.Primary -> c.textInverse
        ButtonType.Outline -> c.textSecondary
        ButtonType.Selected -> c.textSecondary
    }
    Box(
        modifier
            .height(size.height)
            .clip(shape)
            .background(bg)
            .then(
                when (type) {
                    ButtonType.Primary -> Modifier
                    ButtonType.Outline -> Modifier.border(2.dp, c.borderSecondary, shape)
                    ButtonType.Selected -> Modifier.border(4.dp, c.colorSecondary, shape)
                }
            )
            .peakdaClick(enabled, onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = PeakdaType.button, color = textColor, maxLines = 1)
    }
}

// ─────────────────────────────────────────────────────────── 뱃지·칩

/** 절정도 5단계 라벨(퍼센트 없음). Figma 에 나온 3종만 정의 */
enum class PeakLevel(val label: String) { Early("이르다"), Starting("피기시작"), Peak("절정") }

/** Badge: 높이 40, radius 8, 좌우 12, Pretendard 500 24 */
@Composable
fun PeakBadge(level: PeakLevel, modifier: Modifier = Modifier) {
    val c = Peakda.colors
    val (bg, fg) = when (level) {
        PeakLevel.Peak -> c.pink300 to c.white
        PeakLevel.Starting -> c.pink50 to c.colorPrimary
        PeakLevel.Early -> c.green50 to c.colorSecondary
    }
    Box(
        modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(level.label, style = PeakdaType.badge, color = fg)
    }
}

/** 꽃 뱃지/은행나무: 높이 40, 알약, 좌우 15, 간격 5, #E0A800 20% */
@Composable
fun FlowerChip(label: String = "은행나무", modifier: Modifier = Modifier) {
    val c = Peakda.colors
    Row(
        modifier
            .height(40.dp)
            .clip(CircleShape)
            .background(c.sunflowerChipBg)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Image(painterResource(R.drawable.flower_ginkgo), null, Modifier.size(26.67.dp))
        Text(label, style = PeakdaType.flowerChip, color = c.sunflower)
    }
}

/** 만개 카운트: 분홍(Pink/400) 알약, 숫자 Asta 600 22 */
@Composable
fun CountPill(count: Int) {
    val c = Peakda.colors
    Box(
        Modifier
            .clip(CircleShape)
            .background(c.pink400)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("$count", style = PeakdaType.count, color = c.textInverse)
    }
}

/** 알약 링크(후보 3곳 비교 ›): 높이 56, 좌 22 우 16, 간격 4 */
@Composable
fun PillLink(text: String, onClick: () -> Unit) {
    val c = Peakda.colors
    Row(
        Modifier
            .height(56.dp)
            .clip(CircleShape)
            .background(c.bgSecondary)
            .strokeBox(2.dp, c.borderPrimary, CircleShape)
            .peakdaClick(onClick = onClick)
            .padding(start = 22.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text, style = PeakdaType.captionStrong, color = c.textPrimary)
        Icon(PeakdaIcons.ChevronRight, null, tint = c.iconSecondary, modifier = Modifier.size(24.dp))
    }
}

/** 반경 칩(Category_chip): 높이 50.4, 알약, 좌우 14.4 */
@Composable
fun RadiusChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val c = Peakda.colors
    Box(
        Modifier
            .height(50.4.dp)
            .clip(CircleShape)
            .background(if (selected) c.colorSecondary else Color.Transparent)
            .peakdaClick(onClick = onClick)
            .padding(horizontal = 14.4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = PeakdaType.chip, color = if (selected) c.textInverse else c.textTertiary)
    }
}

/** 페이지 표시 알약: 위아래 8, 좌우 18 */
@Composable
fun PagePill(text: String) {
    val c = Peakda.colors
    Box(
        Modifier
            .clip(CircleShape)
            .background(c.bgSecondary)
            .strokeBox(2.dp, c.borderPrimary, CircleShape)
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Text(text, style = PeakdaType.captionStrong, color = c.textSecondary)
    }
}

/** 후보 번호: 48 원, 초록 바탕, 흰 링 4, 그림자 y2 blur8 45% */
@Composable
fun RankCircle(n: Int, modifier: Modifier = Modifier) {
    val c = Peakda.colors
    Box(
        modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.45f), spotColor = Color.Black.copy(alpha = 0.45f))
            .clip(CircleShape)
            .background(c.rankCircle)
            .border(4.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("$n", style = PeakdaType.rank, color = Color.White)
    }
}

// ─────────────────────────────────────────────────────────── 줄·카드 공통

@Composable
fun IconText(
    icon: ImageVector,
    text: String,
    style: TextStyle,
    color: Color,
    iconSize: Dp = 26.dp,
    iconTint: Color = Peakda.colors.iconTertiary,
    gap: Dp = 8.dp,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(gap)) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(iconSize))
        Text(text, style = style, color = color, maxLines = 1)
    }
}

/**
 * Figma 오토 레이아웃은 테두리 두께를 크기에 포함한다(예: 목록 행 118 = 여백 16+82+16 + 테두리 2x2).
 * 그래서 테두리를 그린 뒤 같은 두께만큼 안쪽 여백을 더한다.
 */
fun Modifier.strokeBox(width: Dp, color: Color, shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.border(width, color, shape).padding(width)

/** 카드·행 공통 바탕: Background/Secondary + Border/Primary 2 */
@Composable
fun Modifier.softCard(radius: Dp): Modifier {
    val c = Peakda.colors
    val shape = RoundedCornerShape(radius)
    return this
        .clip(shape)
        .background(c.bgSecondary)
        .strokeBox(2.dp, c.borderPrimary, shape)
}

@Composable
fun PhotoBox(
    res: Int,
    modifier: Modifier,
    radius: Dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier.clip(RoundedCornerShape(radius))) {
        Image(painterResource(res), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        content()
    }
}

/** 사진 위 검은 그라데이션 */
fun scrim(top: Float, bottom: Float) = Brush.verticalGradient(
    listOf(Color.Black.copy(alpha = top), Color.Black.copy(alpha = bottom))
)

/** 홈 '근처 다른 곳' 목록 행: radius 24, 16/20/16/16, 간격 16, 썸네일 72 */
@Composable
fun PlaceListRow(
    photo: Int,
    title: String,
    level: PeakLevel,
    minutes: Int,
    onClick: () -> Unit,
) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .softCard(24.dp)
            .peakdaClick(onClick = onClick)
            .padding(start = 16.dp, top = 16.dp, end = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PhotoBox(photo, Modifier.size(72.dp), 16.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = PeakdaType.itemTitle, color = c.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeakBadge(level)
                FlowerChip()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${minutes}분", style = PeakdaType.value, color = c.textPrimary)
            Icon(PeakdaIcons.ChevronRight, null, tint = c.iconSecondary, modifier = Modifier.size(24.dp))
        }
    }
}

/**
 * 후보 카드(02·12). radius 24, 14/18/14/14, 간격 16. 썸네일 폭 124, 번호 원.
 * @param showFlower 02 는 꽃 뱃지 줄 + 별도 시간 줄, 12 는 뱃지와 시간을 한 줄에
 */
@Composable
fun CandidateCard(
    rank: Int,
    photo: Int,
    title: String,
    level: PeakLevel,
    minutes: Int,
    showFlower: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = Peakda.colors
    Row(
        modifier
            .fillMaxWidth()
            .softCard(24.dp)
            .peakdaClick(onClick = onClick)
            .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PhotoBox(photo, Modifier.width(124.dp).fillMaxHeight(), 18.dp) {
            RankCircle(rank, Modifier.offset(8.dp, 8.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = PeakdaType.itemTitle, color = c.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val time = @Composable {
                IconText(PeakdaIcons.Clock, "차로 ${minutes}분", PeakdaType.time, c.textPrimary, iconSize = 24.dp, gap = 6.dp)
            }
            if (showFlower) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { PeakBadge(level); FlowerChip() }
                time()
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    PeakBadge(level); time()
                }
            }
        }
        Icon(PeakdaIcons.ChevronRight, null, tint = c.iconTertiary, modifier = Modifier.size(32.dp))
    }
}

/** 장소 요약 행(05·21): radius 22, 12/18/12/12, 간격 14, 썸네일 64 */
@Composable
fun PlaceSummaryRow(photo: Int, name: String, sub: String) {
    val c = Peakda.colors
    Row(
        Modifier
            .fillMaxWidth()
            .softCard(22.dp)
            .padding(start = 12.dp, top = 12.dp, end = 18.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PhotoBox(photo, Modifier.size(64.dp), 14.dp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(name, style = PeakdaType.itemTitle, color = c.textPrimary, maxLines = 1)
            Text(sub, style = PeakdaType.caption, color = c.textSecondary, maxLines = 1)
        }
    }
}

// ─────────────────────────────────────────────────────────── 토스트

/** 토스트: 580x80, radius 24, #131417 84%, Asta 600 28 흰색. 창 아래쪽에 겹쳐 뜬다 */
@Composable
fun BoxScope.PeakdaToast(text: String) {
    val c = Peakda.colors
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(c.toastBg)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = PeakdaType.toast, color = Color.White, maxLines = 1, textAlign = TextAlign.Center)
    }
}

// ─────────────────────────────────────────────────────────── 가운데 안내(07~11·15·18~20)

@Composable
fun CenterMessage(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    graphic: (@Composable () -> Unit)? = null,
) {
    val c = Peakda.colors
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        graphic?.invoke()
        Text(title, style = PeakdaType.titleSmallBold, color = c.textMain, textAlign = TextAlign.Center)
        Text(body, style = PeakdaType.bodySmall, color = c.textMainSecondary, textAlign = TextAlign.Center)
    }
}

/** 160 원 아이콘 자리(18·19·20) */
@Composable
fun CircleGraphic(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(Peakda.colors.surfaceSoft),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
fun VSpace(h: Dp) = Spacer(Modifier.height(h))

@Composable
fun RowScope.Fill() = Spacer(Modifier.weight(1f))

@Composable
fun ColumnScope.Fill() = Spacer(Modifier.weight(1f))

val NoPadding = PaddingValues(0.dp)
