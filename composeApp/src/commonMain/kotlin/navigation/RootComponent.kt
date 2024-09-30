package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import data.client.GameStatesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val gameState = GameStatesRepository(CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate))

    private val navigation = StackNavigation<Configuration>()
    val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack<_, Configuration, Child>(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.StartScreen,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(
        configuration: Configuration, context: ComponentContext
    ): Child {
        return when (configuration) {
            is Configuration.StartScreen -> Child.StartScreen(
                StartScreenComponent(
                    componentContext = context,
                    onNavigateToConnectScreen = {
                        navigation.pushNew(Configuration.ConnectScreen)
                    },
                    onNavigateToCreateScreen = {
                        navigation.pushNew(Configuration.CreateScreen)
                    }
                )
            )

            is Configuration.ConnectScreen -> Child.ConnectScreen(
                ConnectScreenComponent(
                    componentContext = context,
                    onNavigateToLobbyScreen = { lobbyId, name ->
                        gameState.connect(lobbyId, name)
                        navigation.pushNew(Configuration.LobbyScreen)
                    },
                    onBackPressed = {
                        navigation.pop()
                    }
                )
            )

            is Configuration.CreateScreen -> Child.CreateScreen(
                CreateScreenComponent(
                    componentContext = context,
                    onNavigateToLobbyScreen = {
                        gameState.connect(null, it)
                        navigation.pushNew(Configuration.LobbyScreen)
                    },
                    onBackPressed = { navigation.pop() }
                )
            )

            is Configuration.LobbyScreen -> Child.LobbyScreen(
                LobbyScreenComponent(
                    componentContext = context,
                    gameState = gameState,
                    onBackPressed = {
                        gameState.close()
                        navigation.pop()
                    },
                    onReadyPressed = {
                        gameState.ready()
                    },
                    onNotReadyPressed = {
                        gameState.notReady()
                    },
                    onStartPressed = {
                        gameState.start()
                    },
                    clearGameExceptionMessage = {
                        gameState.clearExceptionMessage()
                    },
                    pushToGameScreen = {
                        navigation.pushNew(Configuration.GameScreen)
                    }
                )
            )

            is Configuration.GameScreen -> Child.GameScreen(
                GameScreenComponent(
                    componentContext = context,
                    gameState = gameState,
                    refreshQuestion = {
                        gameState.refreshQuestion()
                    },
                    leaderDone = {
                        gameState.leaderDone()
                    },
                    sendAnswer = {  answer ->
                        gameState.answer(answer)
                    },
                    onBackPressed = {
                        gameState.close()
                        navigation.popTo(1)
                    },
                    pushToResultScreen = {
                        navigation.pushNew(Configuration.ResultScreen)
                    }
                )
            )

            is Configuration.ResultScreen -> Child.ResultScreen(
                ResultScreenComponent(
                    componentContext = context,
                    gameState = gameState,
                    onBackPressed = {
                        navigation.popTo(2)
                    }
                )
            )
        }
    }

    sealed class Child {
        data class StartScreen(val component: StartScreenComponent) : Child()
        data class ConnectScreen(val component: ConnectScreenComponent) : Child()
        data class CreateScreen(val component: CreateScreenComponent) : Child()
        data class LobbyScreen(val component: LobbyScreenComponent) : Child()
        data class GameScreen(val component: GameScreenComponent) : Child()
        data class ResultScreen(val component: ResultScreenComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object StartScreen : Configuration()

        @Serializable
        data object ConnectScreen : Configuration()

        @Serializable
        data object CreateScreen : Configuration()

        @Serializable
        data object LobbyScreen : Configuration()

        @Serializable
        data object GameScreen : Configuration()

        @Serializable
        data object ResultScreen: Configuration()
    }
}