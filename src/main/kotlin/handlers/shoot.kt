package handlers

import extensions.notify
import models.Event
import models.Game
import models.Player

fun createShootHandler(
    shot: Event.Shoot,
): GameAction = {
    it?.run {
        shootHandler(shot, this)
    }
}

fun shootHandler(
    shot: Event.Shoot,
    game: Game,
): Pair<Game, GameNotifier>? {
    val player = game.players[shot.pid]

    return if (player == null) {
        null
    } else if (game.zombie.x == shot.x && game.zombie.y == shot.y) {
        Pair(
            game.copy(
                players = game.players +
                        mapOf(
                            shot.pid to player.copy(
                                score = player.score + 1,
                            )
                        ),
                zombie = Player.NPC(
                    name = "night-king",
                ),
            )
        ) {
            game.players.values.forEach {
                it.session.notify("BOOM ${player.name} ${player.score} ${game.zombie.name}")
            }
            it(Event.Win(player))
        }
    } else {
        Pair(game) {
            game.players.values.forEach {
                it.session.notify("BOOM ${player.name} ${player.score}")
            }
        }
    }
}