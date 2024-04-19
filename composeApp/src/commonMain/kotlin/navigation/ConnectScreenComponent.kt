package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class ConnectScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToLobbyScreen: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    private var _userName = MutableValue("")
    val userName: Value<String> = _userName

    private var _lobbyId = MutableValue("")
    val lobbyId: Value<String> = _lobbyId

    fun onEvent(event: ConnectScreenEvent) {
        when (event) {
            is ConnectScreenEvent.ClickButtonConnect -> onNavigateToLobbyScreen()
            is ConnectScreenEvent.ClickButtonBack -> onBackPressed()
            is ConnectScreenEvent.UpdateLobbyIdText -> {
                _lobbyId.value = event.lobbyId
            }
            is ConnectScreenEvent.UpdateUserNameText -> {
                _userName.value = event.userName
            }
        }
    }
}