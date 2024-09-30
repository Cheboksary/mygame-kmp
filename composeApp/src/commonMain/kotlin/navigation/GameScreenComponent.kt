package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import data.client.GameStatesRepository
import data.models.Player
import kotlinx.coroutines.flow.StateFlow

class GameScreenComponent(
    componentContext: ComponentContext,
    gameState: GameStatesRepository,
    private val refreshQuestion: () -> Unit,
    private val leaderDone: () -> Unit,
    private val sendAnswer: (String) -> Unit,
    private val onBackPressed: () -> Unit,
    private val pushToResultScreen: () -> Unit
) : ComponentContext by componentContext {

    val serverClosedTheConnectionByReason = gameState.serverClosedTheConnectionByReason
    val myself: StateFlow<Player> = gameState.myself
    val leaderIsDone: StateFlow<Boolean> = gameState.leaderIsDone
    val question = gameState.question
    val refreshing: StateFlow<Boolean> = gameState.refreshing
    val answered: StateFlow<Boolean> = gameState.answered
    val liarName: StateFlow<String> = gameState.liarName

    private val _answer = MutableValue("")
    var answer: Value<String> = _answer

    fun onEvent(event: GameScreenEvent) {
        when (event) {
            is GameScreenEvent.OnRefreshClicked -> {
                refreshQuestion()
            }
            is GameScreenEvent.OnLeaderDoneClicked -> {
                leaderDone()
            }
            is GameScreenEvent.OnPlayerDoneClicked -> {
                sendAnswer(_answer.value)
                _answer.value = ""
            }
            is GameScreenEvent.UpdateAnswerField -> {
                _answer.value = event.answer
            }
            is GameScreenEvent.OnBackPressed -> {
                onBackPressed()
            }
            is GameScreenEvent.PushToResultScreen -> {
                pushToResultScreen()
            }
        }
    }
}