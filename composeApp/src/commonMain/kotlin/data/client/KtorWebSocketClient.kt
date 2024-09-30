package data.client

import data.models.WSIncomingMessage
import data.models.WSOutgoingMessage
import getClient
import getLocalHostIP
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object KtorWebSocketClient : WebSocketClient {

    private val client: HttpClient = getClient()
    private val serverUrl: String = getLocalHostIP()
    private var session: WebSocketSession? = null

    override fun getIncomingMessages(
        lobbyId: String?,
        playerName: String
    ): Flow<WSIncomingMessage> {
        return flow {
            if (session == null)
                withTimeout(25000) {
                    session = client.webSocketSession("$serverUrl/") {
                        /*timeout {
                        requestTimeoutMillis = 30_000
                        connectTimeoutMillis = 30_000
                        socketTimeoutMillis = 10_000
                        }*/
                        url {
                            if (!lobbyId.isNullOrBlank())
                                parameters.append(name = "lobby_ID", value = lobbyId)
                            parameters.append(name = "name", value = playerName)
                        }
                    }
                }
            try {
                /*while (true) {
                    println("while loop working")
                    val frame = session!!.incoming.receiveCatching().getOrThrow()
                    if (frame is Frame.Text) {
                        emit(Json.decodeFromString<WSIncomingMessage>(frame.readText()))
                        println("text from Frame.Text emitted")
                    }
                }*/
                for (frame in session!!.incoming) {
                    if (frame is Frame.Close) {
                        println("Frame.Close received")
                        session = null
                        throw ClientExceptions.SessionClosedException(frame.readReason())
                    }
                    frame as? Frame.Text ?: continue
                    emit(Json.decodeFromString<WSIncomingMessage>(frame.readText()))
                }
            } /*catch (e: CancellationException) {
                println("CancellationException: ${e.stackTraceToString()}")
            }*/ catch (e: ClientExceptions.SessionClosedException) {
                println("session closed exception caught")
                throw e
            } catch (e: ClosedReceiveChannelException) {
                println("closedReceiveChannel : ${e.message}")
            } finally {
                println("session is over in finally")
            }
            /*val frames = session!!.incoming.receiveAsFlow().filterIsInstance<Frame.Text>()
                .mapNotNull { Json.decodeFromString<WSIncomingMessage>(it.readText()) }
            emitAll(frames)*/
        }
    }

    override suspend fun sendMessage(message: WSOutgoingMessage) {
        session!!.send(Frame.Text(Json.encodeToString(message)))
    }

    override suspend fun close() {
        session?.close()
        session = null
    }
}