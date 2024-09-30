package navigation

sealed interface LobbyScreenEvent {
    data object ClickButtonBack : LobbyScreenEvent
    data object ClickButtonReady : LobbyScreenEvent
    data object ClickButtonStart : LobbyScreenEvent
    data object ClearGameExceptionMessage : LobbyScreenEvent
    data object PushToGameScreen : LobbyScreenEvent
}