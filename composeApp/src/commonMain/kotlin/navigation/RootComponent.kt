package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()
    val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack<_,Configuration,Child>(
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
                        navigation.push(Configuration.ConnectScreen)
                    },
                    onNavigateToCreateScreen = {
                        TODO()
                    }
                )
            )

            is Configuration.ConnectScreen -> Child.ConnectScreen(
                ConnectScreenComponent(
                    componentContext = context,
                    onNavigateToLobbyScreen = { TODO() },
                    onBackPressed = {
                        navigation.pop()
                    }
                )
            )
        }
    }

    sealed class Child {
        data class StartScreen(val component: StartScreenComponent) : Child()
        data class ConnectScreen(val component: ConnectScreenComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object StartScreen : Configuration()

        @Serializable
        data object ConnectScreen : Configuration()
    }
}