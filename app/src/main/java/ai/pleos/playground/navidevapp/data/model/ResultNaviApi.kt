package ai.pleos.playground.navidevapp.data.model

data class ResultNaviApi(
    val result: String,
    val type: ResultType = ResultType.API_CALL
)

enum class ResultType {
    API_CALL,
    LISTENER
}
