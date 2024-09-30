package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import data.client.GameStatesRepository
import data.models.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LobbyScreenComponent(
    componentContext: ComponentContext,
    gameState: GameStatesRepository,
    private val onBackPressed: () -> Unit,
    private val onReadyPressed: suspend () -> Unit,
    private val onNotReadyPressed: suspend () -> Unit,
    private val onStartPressed: () -> Unit,
    private val clearGameExceptionMessage: () -> Unit,
    private val pushToGameScreen: () -> Unit
) : ComponentContext by componentContext {

    val serverClosedTheConnectionByReason = gameState.serverClosedTheConnectionByReason
    val exceptionMessage = gameState.gameExceptionMessage
    val myself: StateFlow<Player> = gameState.myself
    val players: StateFlow<List<Player>> = gameState.playersList
    val lobbyId: StateFlow<String> = gameState.lobbyId
    val gameIsStarted = gameState.gameIsStarted

    private val _onReadyClicked = MutableValue(false)
    val onReadyClicked: Value<Boolean> = _onReadyClicked

    fun onEvent(event: LobbyScreenEvent) {
        when (event) {
            is LobbyScreenEvent.ClickButtonBack -> {
                onBackPressed()
            }
            is LobbyScreenEvent.ClickButtonReady -> {
                _onReadyClicked.value = true
                CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
                    launch {
                        if (myself.value.isReady)
                            onNotReadyPressed()
                        else onReadyPressed()
                    }.join()
                    _onReadyClicked.value = false
                }
            }
            is LobbyScreenEvent.ClickButtonStart -> {
                onStartPressed()
            }
            is LobbyScreenEvent.ClearGameExceptionMessage -> {
                clearGameExceptionMessage()
            }
            is LobbyScreenEvent.PushToGameScreen -> {
                pushToGameScreen()
            }
        }
    }
}