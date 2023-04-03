package handlers

import extensions.notify
import io.ktor.websocket.*
import models.Game

fun createStopHandler(
    pid: Int,
    session: DefaultWebSocketSession,
): GameAction = {
    it?.run {
        stopHandler(pid, this)
    } ?: Pair(null) {
        session.notify("GAME NOT STARTED")
    }
}


fun stopHandler(
    pid: Int,
    game: Game,
): Pair<Game?, GameNotifier>? {
    val player = game.players[pid]

    return player?.run {
        Pair(null) {
            game.players.values.forEach {
                it.session.notify("${player.name} STOPPED THE GAME")
            }
        }
    }
}