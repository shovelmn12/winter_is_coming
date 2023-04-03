package handlers

import extensions.notify
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

        if (next.players.isEmpty()) {
            Pair(null) {}
        } else {
            Pair(next) {
                game.players.values.forEach {
                    it.session.notify("${player.name} EXIT THE GAME")
                }
            }
        }
    }