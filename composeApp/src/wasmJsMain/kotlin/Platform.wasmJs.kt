import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.serialization.json.Json

actual fun getPlatform(): Platform = Platform.Wasm

actual fun getClient() = HttpClient() {
    install(WebSockets) {
        pingInterval = 5_000
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}

actual fun getLocalHostIP() = "wss://ru-mygame-mygame-backend.onrender.com"//"ws://127.0.0.1:8080"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight() = LocalWindowInfo.current.containerSize.height.dp