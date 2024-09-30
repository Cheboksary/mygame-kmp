package navigation

import com.arkivanov.decompose.ComponentContext
import data.client.GameStatesRepository

class ResultScreenComponent(
    componentContext: ComponentContext,
    gameState: GameStatesRepository,
    private val onBackPressed: () -> Unit
): ComponentContext by componentContext {
    val liarName = gameState.liarName
    val liarWon = gameState.liarWon
    val resultTable = gameState.resultTable

    fun onEvent(event: ResultTableEvent) {
        when (event) {
            is ResultTableEvent.BackToLobby -> {
                onBackPressed()
            }
        }
    }
}