package ai.pleos.playground.navidevapp.screen.vm

import ai.pleos.playground.navi.constants.NaviErrorCode
import ai.pleos.playground.navi.constants.NavigationStatus
import ai.pleos.playground.navi.constants.RouteOption
import ai.pleos.playground.navi.data.BookmarkInfo
import ai.pleos.playground.navi.data.CameraAlert
import ai.pleos.playground.navi.data.CautionPoint
import ai.pleos.playground.navi.data.ChargerOperator
import ai.pleos.playground.navi.data.CurrentLocationInfo
import ai.pleos.playground.navi.data.DestinationArrivedInfo
import ai.pleos.playground.navi.data.DestinationInfo
import ai.pleos.playground.navi.data.DrivingInfo
import ai.pleos.playground.navi.data.RecentDestinationInfo
import ai.pleos.playground.navi.data.RouteStartInfo
import ai.pleos.playground.navi.data.RouteStateInfo
import ai.pleos.playground.navi.data.TripInfo
import ai.pleos.playground.navi.data.TBTInfo
import ai.pleos.playground.navi.data.WaypointArrivedInfo
import ai.pleos.playground.navi.data.WaypointChangedInfo
import ai.pleos.playground.navi.data.WaypointInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import ai.pleos.playground.navidevapp.data.model.InitStatus
import ai.pleos.playground.navidevapp.data.model.ResultNaviApi
import ai.pleos.playground.navidevapp.data.model.ResultType
import ai.pleos.playground.navidevapp.data.model.SdkApi
import ai.pleos.playground.navidevapp.data.sample.NaviTestData
import ai.pleos.playground.navidevapp.utils.StringUtils
import android.content.Context
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NaviHelperViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : NaviSdkBaseViewModel(), NaviHelperEventListener {

    private val logTag = StringUtils.TAG(this::class.java.simpleName)

    private var naviHelper: NaviHelper? = null
    private var lastSpeedLimit: Int? = null

    private val _initStatus = MutableStateFlow(InitStatus.Idle)
    val initStatus: StateFlow<InitStatus> = _initStatus.asStateFlow()

    fun initialize() {
        if (_initStatus.value == InitStatus.Initialized) {
            Log.v(logTag, "initialize skipped: already initialized")
            return
        }

        Log.v(logTag, "initialize")
        var helper: NaviHelper? = null
        try {
            helper = NaviHelper(context)
            helper.initialize()
            helper.addListener(this)

            naviHelper = helper
            lastSpeedLimit = null
            _initStatus.value = InitStatus.Initialized
        } catch (e: Exception) {
            Log.e(logTag, "Failed to initialize NaviHelper", e)
            runCatching { helper?.release() }
            naviHelper = null
            _initStatus.value = InitStatus.Failed
            updateCallback(ResultNaviApi("# initialize failed: ${e.toApiFailureMessage()}"))
        }
    }

    fun release() {
        val helper = naviHelper ?: run {
            Log.v(logTag, "release skipped: not initialized")
            return
        }

        Log.v(logTag, "release")
        try {
            helper.removeListener(this)
            helper.release()
        } catch (e: Exception) {
            Log.e(logTag, "Failed to release NaviHelper", e)
            updateCallback(ResultNaviApi("# release failed: ${e.toApiFailureMessage()}"))
        } finally {
            naviHelper = null
            lastSpeedLimit = null
        }
        _initStatus.value = InitStatus.Released
    }

    fun executeApi(api: SdkApi) {
        Log.v(logTag, "executeApi ${api.apiName}")
        val isKnownApi = try {
            executeKnownApi(api.apiName)
        } catch (e: Exception) {
            Log.e(logTag, "Failed to execute ${api.apiName}", e)
            updateCallback(ResultNaviApi("# ${api.apiName} failed: ${e.toApiFailureMessage()}"))
            false
        }

        if (isKnownApi) {
            Log.d(logTag, "# ${api.apiName}")
            updateCallback(ResultNaviApi("# ${api.apiName}"))
        }
    }

    private fun executeKnownApi(apiName: String): Boolean {
        when (apiName) {
            "initialize" -> {
                initialize()
                return true
            }
            "release" -> {
                release()
                return true
            }
        }

        val helper = requireNaviHelper()
        return when (apiName) {
            "requestRoute" -> {
                helper.requestRoute(NaviTestData.routeInfo)
                true
            }
            "requestRouteWithWaypoints" -> {
                helper.requestRouteWithWaypoints(NaviTestData.routeWithWaypointsRequest)
                true
            }
            "changeRouteOption" -> {
                helper.changeRouteOption(NaviTestData.routeOption)
                true
            }
            "cancelRoute" -> {
                helper.cancelRoute()
                true
            }
            "addWaypoint" -> {
                helper.addWaypoint(NaviTestData.waypointInfo)
                true
            }
            "requestReRoute" -> {
                helper.requestReRoute()
                true
            }
            "removeWaypoint" -> {
                helper.removeWaypoint(NaviTestData.waypointIndex)
                true
            }
            "getCurrentLocationInfo" -> {
                helper.getCurrentLocationInfo()
                true
            }
            "getRouteStateInfo" -> {
                helper.getRouteStateInfo()
                true
            }
            "getBookmarkInfo" -> {
                helper.getBookmarkInfo()
                true
            }
            "getRecentDestinationInfo" -> {
                helper.getRecentDestinationInfo()
                true
            }
            "getDestinationInfo" -> {
                helper.getDestinationInfo()
                true
            }
            "getWaypointInfo" -> {
                helper.getWaypointInfo()
                true
            }
            "getTBTInfo" -> {
                helper.getTBTInfo()
                true
            }
            "getChargerOperatorInfo" -> {
                helper.getChargerOperatorInfo()
                true
            }
            "requestNavigationScreen" -> {
                helper.requestNavigationScreen(NaviTestData.requestScreen)
                true
            }
            "requestTrip" -> {
                helper.requestTrip(NaviTestData.tripRequest)
                true
            }
            else -> {
                Log.w(logTag, "Unknown API: $apiName")
                updateCallback(ResultNaviApi("Unknown API: $apiName"))
                false
            }
        }
    }

    private fun requireNaviHelper(): NaviHelper =
        naviHelper ?: throw IllegalStateException("NaviHelper is not initialized")

    private fun Throwable.toApiFailureMessage(): String =
        when (this) {
            is SecurityException -> "SecurityException: ${message.orEmpty()}"
            else -> "${this::class.java.simpleName}${message?.let { ": $it" }.orEmpty()}"
        }

    override fun onCleared() {
        release()
        super.onCleared()
    }

    override fun onRouteStarted(routeStartInfo: RouteStartInfo) {
        Log.i(logTag, "[navHelper] onRouteStarted !!! $routeStartInfo")
        updateCallback(ResultNaviApi("# onRouteStarted $routeStartInfo", ResultType.LISTENER))
    }

    @Deprecated("Deprecated by NaviHelperEventListener")
    override fun onRouteEnded() {
        Log.i(logTag, "[navHelper] onRouteEnded(deprecated) !!! ")
        updateCallback(ResultNaviApi("# onRouteEnded", ResultType.LISTENER))
    }

    override fun onRoutingCompleted() {
        Log.i(logTag, "[navHelper] onRoutingCompleted !!! ")
        updateCallback(ResultNaviApi("# onRoutingCompleted", ResultType.LISTENER))
    }

    override fun onReRouteEnded() {
        Log.i(logTag, "[navHelper] onReRouteEnded !!! ")
        updateCallback(ResultNaviApi("# onReRouteEnded", ResultType.LISTENER))
    }

    override fun onRouteOptionChanged(routeOption: RouteOption) {
        Log.i(logTag, "[navHelper] onRouteOptionChanged !!! $routeOption")
        updateCallback(ResultNaviApi("# onRouteOptionChanged $routeOption", ResultType.LISTENER))
    }

    override fun onRouteCancelled() {
        Log.i(logTag, "[navHelper] onRouteCancelled !!! ")
        updateCallback(ResultNaviApi("# onRouteCancelled", ResultType.LISTENER))
    }

    override fun onWaypointChanged(waypointChangedInfo: WaypointChangedInfo) {
        Log.i(logTag, "[navHelper]  onWaypointChanged !!! $waypointChangedInfo")
        updateCallback(ResultNaviApi("# onWaypointChanged $waypointChangedInfo", ResultType.LISTENER))
    }

    override fun onDestinationArrived(destinationArrivedInfo: DestinationArrivedInfo) {
        Log.i(logTag, "[navHelper] onDestinationArrived !!! $destinationArrivedInfo")
        updateCallback(ResultNaviApi("# onDestinationArrived $destinationArrivedInfo", ResultType.LISTENER))
    }

    override fun onWayPointArrived(waypointArrivedInfo: WaypointArrivedInfo) {
        Log.i(logTag, "[navHelper]  onWayPointArrived !!! $waypointArrivedInfo")
        updateCallback(ResultNaviApi("# onWayPointArrived $waypointArrivedInfo", ResultType.LISTENER))
    }

    override fun onError(errorCode: NaviErrorCode) {
        Log.i(logTag, "[navHelper] onError !!! $errorCode")
        updateCallback(ResultNaviApi("# onError $errorCode", ResultType.LISTENER))
    }

    override fun onCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo) {
        Log.i(logTag, "[navHelper] onCurrentLocationInfo !!! $currentLocationInfo")
        updateCallback(ResultNaviApi("# onCurrentLocationInfo $currentLocationInfo", ResultType.LISTENER))
    }

    override fun onRouteStateInfo(routeStateInfo: RouteStateInfo) {
        Log.i(logTag, "[navHelper]  onRouteStateInfo !!! $routeStateInfo")
        updateCallback(ResultNaviApi("# onRouteStateInfo $routeStateInfo", ResultType.LISTENER))
    }

    override fun onDestinationInfo(destinationInfo: DestinationInfo) {
        Log.i(logTag, "[navHelper] onDestinationInfo !!! $destinationInfo")
        updateCallback(ResultNaviApi("# onDestinationInfo $destinationInfo", ResultType.LISTENER))
    }

    override fun onWayPointInfo(waypointInfoList: List<WaypointInfo>) {
        Log.i(logTag, "[navHelper]  onWayPointInfo !!! $waypointInfoList")
        updateCallback(ResultNaviApi("# onWayPointInfo $waypointInfoList", ResultType.LISTENER))
    }

    override fun onRecentDestinationInfo(recentDestinationInfoList: List<RecentDestinationInfo>) {
        Log.i(logTag, "[navHelper]  onRecentDestinationInfo !!! $recentDestinationInfoList")
        updateCallback(ResultNaviApi("# onRecentDestinationInfo $recentDestinationInfoList", ResultType.LISTENER))
    }

    override fun onBookmarkInfo(bookmarkInfoList: List<BookmarkInfo>) {
        Log.i(logTag, "[navHelper] onBookmarkInfo  !!! $bookmarkInfoList")
        updateCallback(ResultNaviApi("# onBookmarkInfo $bookmarkInfoList", ResultType.LISTENER))
    }

    override fun onCurrentRoadSpeedLimit(speedLimit: Int) {
        if (lastSpeedLimit != speedLimit) {
            lastSpeedLimit = speedLimit

            Log.i(logTag, "[navHelper] onCurrentRoadSpeedLimit changed !!! $speedLimit")
            updateCallback(ResultNaviApi("# onCurrentRoadSpeedLimit $speedLimit", ResultType.LISTENER))
        }
    }

    override fun onCameraAlert(cameraAlertList: List<CameraAlert>) {
        Log.i(logTag, "[navHelper] onCameraAlert  !!! $cameraAlertList")
        updateCallback(ResultNaviApi("# onCameraAlert $cameraAlertList", ResultType.LISTENER))
    }

    override fun onDrivingInfo(drivingInfo: DrivingInfo) {
        Log.i(logTag, "[navHelper] onDrivingInfo !!! $drivingInfo")
        updateCallback(ResultNaviApi("# onDrivingInfo $drivingInfo", ResultType.LISTENER))
    }

    override fun onBookMarkChanged() {
        Log.w(logTag, "[navHelper] onBookMarkChanged !!!")
        updateCallback(ResultNaviApi("# onBookMarkChanged", ResultType.LISTENER))
    }

    override fun onDestinationChanged() {
        Log.w(logTag, "[navHelper] onDestinationChanged !!!")
        updateCallback(ResultNaviApi("# onDestinationChanged", ResultType.LISTENER))
    }

    override fun onNavigationStatus(status: NavigationStatus) {
        Log.i(logTag, "[navHelper] onNavigationStatus !!! $status")
        updateCallback(ResultNaviApi("# onNavigationStatus $status", ResultType.LISTENER))
    }

    override fun onTBTInfo(tbtInfoList: List<TBTInfo>) {
        Log.i(logTag, "[navHelper] onTBTInfo !!! $tbtInfoList")
        updateCallback(ResultNaviApi("# onTBTInfo $tbtInfoList", ResultType.LISTENER))
    }

    override fun onChargerOperatorInfo(chargerOperatorList: List<ChargerOperator>) {
        Log.i(logTag, "[navHelper]  onChargerOperatorInfo !!! $chargerOperatorList")
        updateCallback(ResultNaviApi("# onChargerOperatorInfo $chargerOperatorList", ResultType.LISTENER))
    }

    override fun onCautionPointInfo(cautionPointList: List<CautionPoint>) {
        Log.i(logTag, "[navHelper]  onCautionPointInfo !!! $cautionPointList")
        updateCallback(ResultNaviApi("# onCautionPointInfo $cautionPointList", ResultType.LISTENER))
    }

    override fun onTripInfo(tripInfo: TripInfo) {
        Log.i(logTag, "[navHelper] onTripInfo !!! $tripInfo")
        updateCallback(ResultNaviApi("# onTripInfo $tripInfo", ResultType.LISTENER))
    }
}
