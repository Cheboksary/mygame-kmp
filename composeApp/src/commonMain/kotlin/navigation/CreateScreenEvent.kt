package navigation

sealed interface CreateScreenEvent {
    data object ClickButtonCreate : CreateScreenEvent
    data object ClickButtonBack : CreateScreenEvent
    data class UpdateUserNameText(val name: String) : CreateScreenEvent
}