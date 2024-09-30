package navigation

sealed interface ResultTableEvent {
    data object BackToLobby : ResultTableEvent
}