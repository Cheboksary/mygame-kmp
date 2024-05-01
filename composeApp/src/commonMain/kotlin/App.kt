import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import navigation.RootComponent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.ConnectScreen
import ui.StartScreen
import ui.theme.AppTheme

@OptIn(ExperimentalResourceApi::class)
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
            modifier = Modifier.fillMaxSize()
        ) {
            Children(
                stack = childStack,
                animation = stackAnimation(slide()),
                modifier = if (getPlatform() == Platform.Wasm) Modifier
                    .fillMaxHeight()
                    .widthIn(max = getScreenHeight() * 2 / 3)
                    .background(Color.Cyan)
                else Modifier.fillMaxSize()
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.StartScreen -> StartScreen(instance.component)
                    is RootComponent.Child.ConnectScreen -> ConnectScreen(instance.component)
                }
            }
        }
    }
}