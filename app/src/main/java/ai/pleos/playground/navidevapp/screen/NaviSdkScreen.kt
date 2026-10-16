package ai.pleos.playground.navidevapp.screen

import ai.pleos.playground.navidevapp.data.model.InitStatus
import ai.pleos.playground.navidevapp.data.model.ResultNaviApi
import ai.pleos.playground.navidevapp.data.model.ResultType
import ai.pleos.playground.navidevapp.data.model.SdkApi
import ai.pleos.playground.navidevapp.data.model.helperApiList
import ai.pleos.playground.navidevapp.screen.vm.NaviHelperViewModel
import ai.pleos.playground.navidevapp.ui.theme.NaviDevAppTheme
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val logTag = "[NaviDevApp]NaviSdkScreen"

internal data class SdkSectionUiState(
    val title: String,
    val sdkStatus: InitStatus,
    val apis: List<SdkApi>,
    val selectedApi: SdkApi,
    val apiCall: String,
    val listenerResponse: String
)

@Composable
fun NaviSdkScreen(
    modifier: Modifier = Modifier,
    naviHelperViewModel: NaviHelperViewModel = hiltViewModel()
) {
    var selectedHelperApi by remember { mutableStateOf(helperApiList.first()) }

    val helperResultList by naviHelperViewModel.resultList.collectAsStateWithLifecycle()
    val helperStatus by naviHelperViewModel.initStatus.collectAsStateWithLifecycle()

    DisposableEffect(naviHelperViewModel) {
        Log.v(logTag, "initialize NaviHelper")
        naviHelperViewModel.initialize()

        onDispose {
            Log.v(logTag, "release NaviHelper")
            naviHelperViewModel.release()
        }
    }

    val helperState = remember(helperResultList, helperStatus, selectedHelperApi) {
        SdkSectionUiState(
            title = "NaviHelper SDK",
            sdkStatus = helperStatus,
            apis = helperApiList,
            selectedApi = selectedHelperApi,
            apiCall = helperResultList.formatResults(ResultType.API_CALL),
            listenerResponse = helperResultList.formatResults(ResultType.LISTENER)
        )
    }

    NaviSdkContent(
        modifier = modifier.fillMaxSize(),
        helperState = helperState,
        onHelperApiChange = { selectedHelperApi = it },
        onHelperExecuteClick = { naviHelperViewModel.executeApi(selectedHelperApi) },
        onClearHelperLogClick = { naviHelperViewModel.clearLog() },
        onClearHelperApiCall = { naviHelperViewModel.clearLog(ResultType.API_CALL) },
        onClearHelperListenerResponse = { naviHelperViewModel.clearLog(ResultType.LISTENER) },
    )
}

@Composable
internal fun NaviSdkContent(
    modifier: Modifier = Modifier,
    helperState: SdkSectionUiState,
    onHelperApiChange: (SdkApi) -> Unit,
    onHelperExecuteClick: () -> Unit,
    onClearHelperLogClick: () -> Unit,
    onClearHelperApiCall: () -> Unit,
    onClearHelperListenerResponse: () -> Unit,
) {
    SdkSection(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        state = helperState,
        onApiChange = onHelperApiChange,
        onExecuteClick = onHelperExecuteClick,
        onClearLogClick = onClearHelperLogClick,
        onClearApiCall = onClearHelperApiCall,
        onClearListenerResponse = onClearHelperListenerResponse
    )
}

private fun List<ResultNaviApi>.formatResults(type: ResultType): String =
    filter { it.type == type }
        .joinToString("\n") { it.result }

@Preview(showBackground = true, widthDp = 1700, heightDp = 1184)
@Composable
fun NaviSdkScreenPreview() {
    var selectedHelperApi by remember { mutableStateOf(helperApiList.first()) }

    NaviDevAppTheme {
        NaviSdkContent(
            helperState = SdkSectionUiState(
                title = "NaviHelper SDK",
                sdkStatus = InitStatus.Initialized,
                apis = helperApiList,
                selectedApi = selectedHelperApi,
                apiCall = "",
                listenerResponse = ""
            ),
            onHelperApiChange = { selectedHelperApi = it },
            onHelperExecuteClick = {},
            onClearHelperLogClick = {},
            onClearHelperApiCall = {},
            onClearHelperListenerResponse = {},
        )
    }
}
