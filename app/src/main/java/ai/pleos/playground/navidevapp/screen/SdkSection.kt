package ai.pleos.playground.navidevapp.screen

import ai.pleos.playground.navidevapp.data.model.InitStatus
import ai.pleos.playground.navidevapp.data.model.SdkApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SdkSection(
    modifier: Modifier = Modifier,
    state: SdkSectionUiState,
    onApiChange: (SdkApi) -> Unit,
    onExecuteClick: () -> Unit,
    onClearLogClick: () -> Unit,
    onClearApiCall: () -> Unit,
    onClearListenerResponse: () -> Unit
) {
    Column(modifier = modifier) {
        SdkSectionTitle(
            sdkName = state.title,
            status = state.sdkStatus
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ApiSelector(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                apis = state.apis,
                selectedApi = state.selectedApi,
                onApiChange = onApiChange
            )

            OutlinedButton(
                onClick = onExecuteClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Execute", color = Color(0xFF563DF1))
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onClearLogClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Clear", color = Color(0xFF563DF1))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("API Call", style = MaterialTheme.typography.labelLarge)
        LogPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            text = state.apiCall,
            onClearClick = onClearApiCall,
            clearContentDescription = "Clear API Call Log"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Listener Response", style = MaterialTheme.typography.labelLarge)
        LogPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            text = state.listenerResponse,
            onClearClick = onClearListenerResponse,
            clearContentDescription = "Clear Listener Response Log"
        )
    }
}

@Composable
private fun ApiSelector(
    modifier: Modifier = Modifier,
    apis: List<SdkApi>,
    selectedApi: SdkApi,
    onApiChange: (SdkApi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .clickable { expanded = true }
                .border(1.dp, Color.Gray)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ApiName(api = selectedApi)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            apis.forEach { api ->
                DropdownMenuItem(
                    text = { ApiName(api = api) },
                    onClick = {
                        onApiChange(api)
                        expanded = false
                    },
                    modifier = if (api == selectedApi) {
                        Modifier.background(Color(0xFFE0E0E0))
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}

@Composable
private fun ApiName(api: SdkApi) {
    Text(
        text = api.apiName,
        color = Color.Black
    )
}

@Composable
private fun LogPanel(
    modifier: Modifier = Modifier,
    text: String,
    onClearClick: () -> Unit,
    clearContentDescription: String
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(text) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier.border(1.dp, Color.LightGray)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, end = 28.dp, bottom = 5.dp)
                .verticalScroll(scrollState)
        )

        IconButton(
            onClick = onClearClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = clearContentDescription,
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun SdkSectionTitle(
    sdkName: String,
    status: InitStatus
) {
    Text(
        text = buildAnnotatedString {
            append("$sdkName ")
            withStyle(style = SpanStyle(color = status.toColor())) {
                append("(${status.name})")
            }
        },
        style = MaterialTheme.typography.titleMedium
    )
}

private fun InitStatus.toColor(): Color =
    when (this) {
        InitStatus.Initialized -> Color(0xFF2E7D32)
        InitStatus.Failed -> Color(0xFFB3261E)
        InitStatus.Idle,
        InitStatus.Released -> Color.Gray
    }
