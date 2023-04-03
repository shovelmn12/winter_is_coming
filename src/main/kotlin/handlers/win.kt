package handlers

import extensions.notify
import models.Game
import models.Player

fun createWinHandler(player: Player.Human): GameAction = {
    it?.run {
        winHandler(player, this)
    }
}


fun winHandler(
    player: Player.Human,
    game: Game,
): Pair<Game, GameNotifier> = Pair(game) {
    game.players.values.forEach {
        it.session.notify("${player.name} WON THE ROUND")
    }
}