package ai.pleos.playground.navidevapp.data.sample

import ai.pleos.playground.navi.constants.RouteOption
import ai.pleos.playground.navi.constants.WaypointIndex
import ai.pleos.playground.navi.data.NavigationScreenRequest
import ai.pleos.playground.navi.data.RequestWaypointInfo
import ai.pleos.playground.navi.data.RouteInfo
import ai.pleos.playground.navi.data.RouteWithWaypointsRequest
import ai.pleos.playground.navi.data.TripRequest

internal object NaviTestData {
    val routeInfo = RouteInfo(
        latitude = 37.48544722,
        longitude = 127.03636666,
        poiId = "36khp37hh8gqcm5nqhe5",
        poiName = "42dot",
        poiSubId = "0",
        address = "서울시 강남구 남부순환로 2621"
    )

    val waypointInfo = RequestWaypointInfo(
        latitude = 37.41275833,
        longitude = 127.09481944,
        waypointIndex = WaypointIndex.FIRST,
        poiId = "36khp2hdj2cfcm5nqhe3",
        poiName = "소프트웨어드림센터",
        poiSubId = "0",
        address = "경기도 성남시 수정구 창업로40번길 6"
    )

    val waypointIndex = WaypointIndex.FIRST

    val routeWithWaypointsRequest = RouteWithWaypointsRequest(
        destination = routeInfo,
        waypoints = listOf(waypointInfo)
    )

    val routeOption = RouteOption.FREE

    val requestScreen = NavigationScreenRequest.RouteOptionList

    val tripRequest = TripRequest(
        latitude = 37.41275833,
        longitude = 127.09481944,
        poiId = "36khp2hdj2cfcm5nqhe3",
        poiName = "소프트웨어드림센터",
        poiSubId = "0"
    )
}
