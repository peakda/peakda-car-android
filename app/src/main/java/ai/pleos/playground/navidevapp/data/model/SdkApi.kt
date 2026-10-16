package ai.pleos.playground.navidevapp.data.model

data class SdkApi(
    val apiName: String
)

enum class InitStatus {
    Initialized, Released, Idle, Failed
}

val helperApiList = listOf(
    SdkApi("requestRoute"),
    SdkApi("requestRouteWithWaypoints"),
    SdkApi("changeRouteOption"),
    SdkApi("cancelRoute"),
    SdkApi("addWaypoint"),
    SdkApi("requestReRoute"),
    SdkApi("removeWaypoint"),
    SdkApi("getCurrentLocationInfo"),
    SdkApi("getRouteStateInfo"),
    SdkApi("getBookmarkInfo"),
    SdkApi("getRecentDestinationInfo"),
    SdkApi("getDestinationInfo"),
    SdkApi("getWaypointInfo"),
    SdkApi("getTBTInfo"),
    SdkApi("getChargerOperatorInfo"),
    SdkApi("requestNavigationScreen"),
    SdkApi("requestTrip"),
)
