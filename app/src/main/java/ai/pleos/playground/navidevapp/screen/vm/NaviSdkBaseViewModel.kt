package ai.pleos.playground.navidevapp.screen.vm

import ai.pleos.playground.navidevapp.data.model.ResultNaviApi
import ai.pleos.playground.navidevapp.data.model.ResultType
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class NaviSdkBaseViewModel : ViewModel() {

    private val _resultList = MutableStateFlow<List<ResultNaviApi>>(emptyList())
    val resultList: StateFlow<List<ResultNaviApi>> = _resultList.asStateFlow()

    protected fun updateCallback(result: ResultNaviApi) {
        _resultList.update { it + result }
    }

    fun clearLog(type: ResultType? = null) {
        _resultList.update { list ->
            type?.let { list.filterNot { it.type == type } } ?: emptyList()
        }
    }
}
