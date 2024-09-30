import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import navigation.RootComponent
import ui.screens.ConnectScreen
import ui.screens.CreateScreen
import ui.screens.GameScreen
import ui.screens.LobbyScreen
import ui.screens.ResultScreen
import ui.screens.StartScreen
import ui.theme.AppTheme

@Composable
fun App(
    darkTheme: Boolean,
    root: RootComponent
) {
    AppTheme(darkTheme = darkTheme) {
        val childStack by root.childStack.subscribeAsState()
        // container Box() may be omitted depending on the value of expect/actual fun getPlatform() is equals Platform.Android
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Children(
                stack = childStack,
                animation = stackAnimation(slide()),
                modifier = if (getPlatform() == Platform.Wasm) Modifier
                    .fillMaxHeight()
                    .widthIn(max = getScreenHeight() * 2 / 3)
                else Modifier.fillMaxSize()
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.StartScreen -> StartScreen(instance.component)
                    is RootComponent.Child.ConnectScreen -> ConnectScreen(instance.component)
                    is RootComponent.Child.CreateScreen -> CreateScreen(instance.component)
                    is RootComponent.Child.LobbyScreen -> LobbyScreen(instance.component)
                    is RootComponent.Child.GameScreen -> GameScreen(instance.component)
                    is RootComponent.Child.ResultScreen -> ResultScreen(instance.component)
                }
            }
        }
    }
}