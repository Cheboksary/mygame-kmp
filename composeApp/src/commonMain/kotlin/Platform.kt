import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import io.ktor.client.HttpClient

enum class Platform{
    Android,
    Wasm
}

expect fun getPlatform(): Platform

expect fun getClient(): HttpClient

expect fun getLocalHostIP(): String

@Composable
expect fun getScreenHeight(): Dp