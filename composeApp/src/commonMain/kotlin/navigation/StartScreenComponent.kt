package navigation

import com.arkivanov.decompose.ComponentContext

class StartScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToConnectScreen: () -> Unit,
    private val onNavigateToCreateScreen: () -> Unit
) : ComponentContext by componentContext {

    fun onEvent(event: StartScreenEvent) {
        when (event) {
            is StartScreenEvent.ClickButtonConnect -> onNavigateToConnectScreen()
            is StartScreenEvent.ClickButtonCreate -> onNavigateToCreateScreen()
        }
    }
}