import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

class WebSocketClient(private val client: HttpClient, private val localHostIP: String) {

    suspend fun helloFromWebSocket(): String {
        var text = ""
        client.webSocket(urlString = "$localHostIP/echo") {
            val frame = incoming.receive()
            text = (frame as Frame.Text).readText()
        }
        return text
    }

    fun close() {
        client.close()
    }

    
}