package data.client

import io.ktor.websocket.CloseReason

object ClientExceptions {
    class SessionClosedException(val reason: CloseReason?) : Exception()
}