package handlers

import extensions.notify
import io.ktor.websocket.*
import models.Game
import models.Player

fun createStartHandler(
    pid: Int,
    name: String,
    session: DefaultWebSocketSession,
): GameAction = {
    it?.run {
        Pair(this) {
            session.notify("GAME ALREADY STARTED, USE \"JOIN [username]\" TO JOIN")
        }
    } ?: startHandler(
        createGame(
            listOf(
                Player.Human(
                    id = pid,
                    name = name,
                    session = session,
                ),
            ),
        ),
    )
}

fun createGame(players: List<Player.Human>): Game = Game(
    players = players.associateBy { it.id },
)

fun startHandler(game: Game): Pair<Game, GameNotifier> = Pair(game) {
    game.players.values.forEach { player ->
        player.session.notify("GAME ${game.id} STARTED")
    }
}