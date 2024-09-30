package data.client

import data.models.WSIncomingMessage
import data.models.WSOutgoingMessage
import kotlinx.coroutines.flow.Flow

interface WebSocketClient {
    fun getIncomingMessages(lobbyId: String?, playerName: String): Flow<WSIncomingMessage>
    suspend fun sendMessage(message: WSOutgoingMessage)
    suspend fun close()
}