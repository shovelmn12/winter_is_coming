package handlers

import extensions.notify
import models.Event
import models.Game
import models.Player

fun createExitHandler(pid: Int): GameAction = {
    it?.run {
        exitHandler(
            this,
            getPlayer(pid, this),
        )
    }
}

fun getPlayer(pid: Int, game: Game): Player.Human? = game.players[pid]

fun exitHandler(game: Game, player: Player.Human?): Pair<Game?, GameNotifier>? =
    player?.run {
        val next = game.copy(
            players = game.players.filter { it.key != player.id },
        )

        Pair(next) {
            // if no players left stop the game
            if (next.players.isEmpty()) {
                it(
                    Event.Stop(
                        player.id,
                        player.session,
                    ),
                )
            } else {
                game.players.values.forEach {
                    it.session.notify("${player.name} EXIT THE GAME")
                }
            }
        }
    }