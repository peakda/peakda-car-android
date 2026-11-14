package ai.pleos.playground.navidevapp.peakda.navi

import ai.pleos.playground.navi.constants.NaviErrorCode
import ai.pleos.playground.navi.constants.NaviMode
import ai.pleos.playground.navi.constants.NavigationStatus
import ai.pleos.playground.navi.constants.WaypointIndex
import ai.pleos.playground.navi.data.DestinationArrivedInfo
import ai.pleos.playground.navi.data.RequestWaypointInfo
import ai.pleos.playground.navi.data.RouteInfo
import ai.pleos.playground.navi.data.RouteStartInfo
import ai.pleos.playground.navi.data.RouteStateInfo
import ai.pleos.playground.navi.data.WaypointChangedInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

/** 내비에 보낼 장소. */
data class NaviPlace(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
)

/** 내비 요청 결과. */
sealed interface NaviResult {
    /** 지금 가는 길에 경유지로 들어갔다. */
    data object WaypointAdded : NaviResult
    /** 새 목적지로 길 안내를 시작했다. [fromWaypoint] 는 안내 중인 길이 없어 경유지 대신 목적지로 보낸 경우. */
    data class RouteStarted(val fromWaypoint: Boolean = false) : NaviResult
    /** 요청은 내비에 전달됐지만 아직 확정되지 않았다(내비 화면에서 경로 선택 중 등). */
    data object Sent : NaviResult
    /** 내비가 거절했거나 연결되지 않았다. */
    data class Failed(val reason: String) : NaviResult
}

/**
 * Pleos NaviHelper SDK 로 기본 내비 앱에 경유지 추가·목적지 설정을 요청한다.
 *
 * SDK 호출은 결과를 바로 돌려주지 않고 [NaviHelperEventListener] 로 알려 준다.
 * 그래서 요청마다 성공/실패 콜백을 기다린다. 내비는 경로 탐색·경로 선택에 1분 넘게 걸리기도 해서,
 * 일정 시간 안에 결과가 없으면 실패가 아니라 "전달됨"([NaviResult.Sent])으로 끝낸다.
 */
class PeakdaNavi(context: Context) : NaviHelperEventListener {

    private val helper = NaviHelper(context.applicationContext)
    private val main = Handler(Looper.getMainLooper())

    @Volatile private var connected = false
    /** 내비가 지금 길 안내 중인지. 연결 직후 [NaviHelper.getRouteStateInfo] 로 받고, 이후 이벤트로 갱신한다. */
    @Volatile private var routeActive = false

    private var pending: Pending? = null

    private class Pending(val kind: Kind, val onResult: (NaviResult) -> Unit, val timeout: Runnable)

    private enum class Kind { WAYPOINT, ROUTE, ROUTE_INSTEAD_OF_WAYPOINT }

    fun connect() {
        if (connected) return
        runCatching {
            helper.initialize(
                {
                    connected = true
                    helper.addListener(this)
                    Log.i(TAG, "connected")
                    runCatching { helper.getRouteStateInfo() }
                },
                { e -> Log.e(TAG, "initialize failed", e) },
            )
        }.onFailure { Log.e(TAG, "initialize threw", it) }
    }

    fun release() {
        runCatching {
            helper.removeListener(this)
            helper.release()
        }
        connected = false
        main.removeCallbacksAndMessages(null)
        pending = null
    }

    /** 지금 가는 길에 경유지로 추가한다. 안내 중인 길이 없으면 목적지로 안내를 시작한다. */
    fun addWaypoint(place: NaviPlace, onResult: (NaviResult) -> Unit) {
        Log.i(TAG, "addWaypoint ${place.name} routeActive=$routeActive")
        if (!routeActive) {
            request(Kind.ROUTE_INSTEAD_OF_WAYPOINT, onResult) { helper.requestRoute(place.toRouteInfo()) }
            return
        }
        request(Kind.WAYPOINT, onResult) {
            helper.addWaypoint(
                RequestWaypointInfo(
                    latitude = place.latitude,
                    longitude = place.longitude,
                    poiName = place.name,
                    waypointIndex = WaypointIndex.FIRST,
                    poiId = NO_POI_ID,
                    poiSubId = NO_POI_SUB_ID,
                    address = place.address,
                )
            )
        }
    }

    /** 새 목적지로 길 안내를 시작한다. */
    fun setDestination(place: NaviPlace, onResult: (NaviResult) -> Unit) {
        Log.i(TAG, "setDestination ${place.name}")
        request(Kind.ROUTE, onResult) { helper.requestRoute(place.toRouteInfo()) }
    }

    private fun request(kind: Kind, onResult: (NaviResult) -> Unit, call: () -> Unit) {
        main.post {
            // 앞선 요청이 남아 있으면 결과 없이 버린다(새 요청이 화면을 이어받는다).
            pending?.let { main.removeCallbacks(it.timeout) }
            pending = null
            if (!connected) {
                onResult(NaviResult.Failed("내비 서비스에 연결되지 않았어요"))
                connect()
                return@post
            }
            val timeout = Runnable { finish(NaviResult.Sent) }
            pending = Pending(kind, onResult, timeout)
            main.postDelayed(timeout, TIMEOUT_MS)
            runCatching(call).onFailure {
                Log.e(TAG, "request failed", it)
                finish(NaviResult.Failed(it.message ?: it::class.java.simpleName))
            }
        }
    }

    /** 콜백 스레드와 관계없이 메인 스레드에서 한 번만 결과를 넘긴다. */
    private fun finish(result: NaviResult) {
        val p = pending ?: return
        pending = null
        main.removeCallbacks(p.timeout)
        Log.i(TAG, "result ${p.kind} -> $result")
        p.onResult(result)
    }

    /** 경로가 바뀌었다는 콜백이 오면 기다리던 요청을 성공으로 끝낸다. */
    private fun succeed() = main.post {
        val p = pending ?: return@post
        finish(
            when (p.kind) {
                Kind.WAYPOINT -> NaviResult.WaypointAdded
                Kind.ROUTE_INSTEAD_OF_WAYPOINT -> NaviResult.RouteStarted(fromWaypoint = true)
                Kind.ROUTE -> NaviResult.RouteStarted()
            }
        )
    }

    // ── NaviHelperEventListener ─────────────────────────────────────────────

    override fun onRouteStateInfo(routeStateInfo: RouteStateInfo) {
        Log.i(TAG, "onRouteStateInfo naviMode=${routeStateInfo.naviMode}")
        routeActive = routeStateInfo.naviMode == NaviMode.ROUTE_GUIDING ||
            routeStateInfo.naviMode == NaviMode.SIMULATION_GUIDING
    }

    override fun onNavigationStatus(status: NavigationStatus) {
        Log.i(TAG, "onNavigationStatus $status")
        routeActive = status == NavigationStatus.ROUTE_DRIVE
    }

    override fun onWaypointChanged(waypointChangedInfo: WaypointChangedInfo) {
        Log.i(TAG, "onWaypointChanged $waypointChangedInfo")
        succeed()
    }

    override fun onRouteStarted(routeStartInfo: RouteStartInfo) {
        Log.i(TAG, "onRouteStarted $routeStartInfo")
        routeActive = true
        succeed()
    }

    override fun onRouteCancelled() {
        Log.i(TAG, "onRouteCancelled")
        routeActive = false
    }

    override fun onDestinationArrived(destinationArrivedInfo: DestinationArrivedInfo) {
        Log.i(TAG, "onDestinationArrived")
        routeActive = false
    }

    override fun onError(errorCode: NaviErrorCode) {
        Log.w(TAG, "onError $errorCode")
        main.post { finish(NaviResult.Failed(errorCode.name)) }
    }

    private fun NaviPlace.toRouteInfo() = RouteInfo(
        latitude = latitude,
        longitude = longitude,
        poiName = name,
        poiId = NO_POI_ID,
        poiSubId = NO_POI_SUB_ID,
        address = address,
    )

    companion object {
        private const val TAG = "PeakdaNavi"
        /**
         * 결과를 기다리는 시간. 에뮬레이터 내비는 경로 탐색에 20~30초, 경유지 추가 후 경로 선택까지
         * 1분 정도 걸린다. 그보다 길면 사용자를 붙잡지 않고 "전달됨"으로 끝낸다.
         */
        private const val TIMEOUT_MS = 30_000L

        // 지도 POI 가 없는 장소라 좌표로만 안내한다.
        private const val NO_POI_ID = ""
        private const val NO_POI_SUB_ID = "0"
    }
}
