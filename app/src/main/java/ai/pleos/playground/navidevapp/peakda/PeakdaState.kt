package ai.pleos.playground.navidevapp.peakda

import ai.pleos.playground.navidevapp.peakda.navi.PeakdaNavi
import ai.pleos.playground.navidevapp.peakda.screens.Dummy
import ai.pleos.playground.navidevapp.peakda.screens.Place
import ai.pleos.playground.navidevapp.peakda.theme.ThemeMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** D2 화면 인벤토리(21개). id 는 ⑬ 핸드오프 번호와 같다. */
enum class Route(val id: String, val label: String) {
    Home("01", "홈 · 지금 가장 예쁜 곳"),
    Compare("02", "후보 비교 (3곳씩)"),
    Detail("03", "명소 상세"),
    Confirm("04", "경유지 · 목적지 확인"),
    Record("05", "절정도 기록"),
    Calendar("06", "예측 캘린더"),
    StopLock("07", "일시 정차 잠금"),
    Resume("08", "주차 복귀 · 이어서 보기"),
    Driving("09", "주행 중 안내"),
    Listening("10", "음성 · 듣는 중"),
    Thinking("11", "음성 · 생각 중"),
    VoiceResults("12", "음성 · 후보 결과"),
    VoiceDetail("13", "음성 · 장소 확정 → 상세"),
    VoiceAsk("14", "음성 · 추가 질문"),
    Login("15", "로그인 (선택)"),
    Settings("16", "설정"),
    Region("17", "위치 권한 · 지역 선택"),
    Empty("18", "오류 · 정보 없음"),
    NetworkError("19", "오류 · 통신 오류"),
    NaviFail("20", "오류 · 내비 연결 실패"),
    AutoRecord("21", "출발 시 자동 기록"),
    DevList("dev", "개발용 화면 목록");

    companion object {
        fun fromId(id: String?): Route? = entries.firstOrNull { it.id == id }
    }
}

/**
 * 화면 이동과 화면 상태를 가진다.
 * 명소 정보는 샘플 데이터이고, 경유지 추가·목적지 설정은 [navi] 로 실제 내비에 요청한다.
 */
class PeakdaState(start: Route = Route.Home) {
    val stack = mutableStateListOf(start)
    val current: Route get() = stack.last()

    /** 내비 연동(Pleos NaviHelper SDK). 미리보기·테스트에서는 null. */
    var navi: PeakdaNavi? = null
    /** 내비 요청을 보내고 응답을 기다리는 중 */
    var naviBusy by mutableStateOf(false)

    /** 상세·경유지 확인 화면에 보여 줄 명소 */
    var selected by mutableStateOf(Dummy.gongseri)
        private set

    fun select(place: Place) {
        selected = place
    }

    var themeMode by mutableStateOf(ThemeMode.System)
    /** 검증용 딥링크(--es theme)로 강제한 모드. 설정 값(themeMode)과 따로 둔다. */
    var debugTheme by mutableStateOf<ThemeMode?>(null)
    var loggedIn by mutableStateOf(false)
    var recommendCardOn by mutableStateOf(true)

    /** 03 하트 저장 여부(로그인 사용자만) */
    var saved by mutableStateOf(false)

    /** 05 선택 단계(0 이르다 · 1 시작 · 2 절정 · 3 늦었다) */
    var recordChoice by mutableStateOf<Int?>(null)
    var recordDone by mutableStateOf(false)

    var toast by mutableStateOf<String?>(null)
    /** 검증용 딥링크에서는 토스트를 자동으로 닫지 않는다 */
    var keepToast = false

    fun go(route: Route) {
        toast = null
        stack.add(route)
    }

    fun replace(route: Route) {
        toast = null
        stack[stack.lastIndex] = route
    }

    fun back() {
        toast = null
        if (stack.size > 1) stack.removeAt(stack.lastIndex)
    }

    fun home() {
        toast = null
        stack.clear()
        stack.add(Route.Home)
    }

    fun showToast(text: String) {
        toast = text
    }

    fun cycleTheme() {
        debugTheme = null
        themeMode = ThemeMode.entries[(themeMode.ordinal + 1) % ThemeMode.entries.size]
    }
}
