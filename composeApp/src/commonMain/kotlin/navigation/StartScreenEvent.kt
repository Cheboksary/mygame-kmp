package navigation

sealed interface StartScreenEvent {
    data object ClickButtonCreate : StartScreenEvent
    data object ClickButtonConnect : StartScreenEvent
}