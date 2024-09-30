package data.models

import kotlinx.serialization.Serializable

enum class PlayerState {
    LEADER,
    LIAR,
    PLAYER
}

@Serializable
data class Player(
    val name: String,
    val id: Int,
    var state: PlayerState,
    var points: Int = 0,
    val answers: MutableList<String> = mutableListOf(),
    var isReady: Boolean = false
)