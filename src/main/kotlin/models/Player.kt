package models

import io.ktor.websocket.*

sealed interface Player {
    val name: String

    data class Human(
        val id: Int,
        override val name: String,
        val score: Int = 0,
        val session: DefaultWebSocketSession,
    ) : Player

    data class NPC(
        override val name: String,
        val x: Int = 0,
        val y: Int = 0,
    ) : Player
}

