package navigation

sealed interface ConnectScreenEvent {
    data object ClickButtonConnect : ConnectScreenEvent
    data object ClickButtonBack : ConnectScreenEvent
    data class UpdateUserNameText(val userName: String) : ConnectScreenEvent
    data class UpdateLobbyIdText(val lobbyId: String) : ConnectScreenEvent
}