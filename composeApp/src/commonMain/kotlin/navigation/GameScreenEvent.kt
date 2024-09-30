package navigation

sealed interface GameScreenEvent {
    data object OnRefreshClicked : GameScreenEvent
    data object OnLeaderDoneClicked : GameScreenEvent
    data class UpdateAnswerField(val answer: String) : GameScreenEvent
    data object OnPlayerDoneClicked : GameScreenEvent
    data object OnBackPressed : GameScreenEvent
    data object PushToResultScreen : GameScreenEvent
}