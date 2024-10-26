import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.serialization.json.Json

actual fun getPlatform(): Platform = Platform.Android

actual fun getClient() = HttpClient(CIO) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    engine {
        endpoint {
            connectTimeout = 20000
            socketTimeout = 25000
        }
    }
    /*engine {
        config {
            sslSocketFactory(SslSettings.getSslContext()!!.socketFactory, SslSettings.getTrustManager())
        }
    }*/
}

actual fun getLocalHostIP() = "wss://ru-mygame-mygame-backend.onrender.com" //"ws://10.0.2.2:8080"

@Composable
actual fun getScreenHeight() = LocalConfiguration.current.screenHeightDp.dp
