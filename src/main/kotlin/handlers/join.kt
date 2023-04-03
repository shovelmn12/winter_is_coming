package handlers

import extensions.notify
import io.ktor.websocket.*
import models.Game
import models.Player

fun createJoinHandler(
    pid: Int,
    name: String,
    session: DefaultWebSocketSession,
): GameAction = {
    it?.run {
        joinHandler(
            this,
            Player.Human(
                id = pid,
                name = name,
                session = session,
            )
        )
    } ?: Pair(null) {
        session.notify("GAME NOT STARTED")
    }
}


fun joinHandler(
    game: Game,
    player: Player.Human,
): Pair<Game, GameNotifier> = Pair(
    game.copy(
        players = game.players + mapOf(
            player.id to player,
        ),
    )
) {
    game.players.values.forEach {
        it.session.notify("${player.name} JOINED THE GAME")
    }
}