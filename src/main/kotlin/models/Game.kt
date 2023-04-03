package models

import java.util.concurrent.atomic.AtomicInteger

data class Game(
    val id: Int = lastID.getAndIncrement(),
    val cols: Int = 30,
    val rows: Int = 10,
    val players: Map<Int, Player.Human> = mapOf(),
    val zombie: Player.NPC = Player.NPC(
        name = "night-king",
    ),
) {
    companion object {
        private val lastID = AtomicInteger(1)
    }
}