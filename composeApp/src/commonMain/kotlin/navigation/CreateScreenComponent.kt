package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class CreateScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToLobbyScreen: (name: String) -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    private var _userName = MutableValue("")
    val userName: Value<String> = _userName

    fun onEvent(event: CreateScreenEvent) {
        when (event) {
            is CreateScreenEvent.ClickButtonCreate -> {
                onNavigateToLobbyScreen(_userName.value)
            }
            is CreateScreenEvent.ClickButtonBack -> {
                onBackPressed()
            }
            is CreateScreenEvent.UpdateUserNameText -> {
                _userName.value = event.name
            }
        }
    }
}