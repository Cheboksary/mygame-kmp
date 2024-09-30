package data.models

import kotlinx.serialization.Serializable

enum class OutgoingMsgType {
    GAME_START,
    LEADER_DONE,
    REFRESH_QUESTION,
    READY,
    NOT_READY,
    ANSWER,
    VOTE_KICK
}

@Serializable
data class WSOutgoingMessage(
    val type: OutgoingMsgType,
    val msg: String = ""
)

enum class IncomingMsgType {
    LOBBY_STATE,
    EXCEPTION,
    GAME_START,
    YOUR_LIAR,
    QUESTION,
    LEADER_DONE,
    READY,
    NOT_READY,
    ANSWER_ACCEPTED,
    NEXT_ROUND,
    GAME_OVER,
    LIAR_WON,
    PLAYERS_WON,
    GAME_OVER_BY_VOTE
}

@Serializable
data class WSIncomingMessage(
    val type: IncomingMsgType,
    val lobbyId: String = "",
    val playersList: List<Player>? = null,
    val msg: String = "",
    val player: Player? = null,
    val resultTable: List<MutableSet<Player>>? = null
)