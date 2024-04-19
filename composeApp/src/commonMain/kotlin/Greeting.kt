import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class Greeting {
    private val platform = getPlatform()
    private val wsClient = getClient()
    private val localHostIP = getLocalHostIP()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    private val client = HttpClient()

    suspend fun response(): String {
        val response = client.get("https://api.spacexdata.com/v5/launches/latest")
        return response.bodyAsText()
    }

    suspend fun webSocketRequest(): String {
        val client = WebSocketClient(wsClient, localHostIP)
        val response = client.helloFromWebSocket()
        client.close()
        return response
    }
}