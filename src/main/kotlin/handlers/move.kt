package handlers

import extensions.notify
import models.Game
import models.Player
import java.util.*
import kotlin.random.Random

private val random = Random(Date().time)

fun createMoveHandler(): (Game?) -> Pair<Game?, GameNotifier>? = {
    it?.run {
        moveHandler(
            this,
            generateNewZombiePosition(
                this.zombie,
                this.cols,
                this.rows,
            ),
        )
    }
}

fun generateNewZombiePosition(
    zombie: Player.NPC,
    maxX: Int,
    maxY: Int,
): Player.NPC = zombie.copy(
    x = (zombie.x + (if (random.nextBoolean()) 1 else -1)).coerceIn(0..maxX),
    y = (zombie.y + (if (random.nextBoolean()) 1 else -1)).coerceIn(0..maxY)
)

fun moveHandler(
    game: Game,
    zombie: Player.NPC,
): Pair<Game, GameNotifier> = if (game.cols == zombie.x) {
    Pair(
        game.copy(
            players = game.players.mapValues { (_, player) ->
                player.copy(
                    score = (player.score - 1).coerceIn(0..Int.MAX_VALUE),
                )
            },
            zombie = Player.NPC(
                name = "night-king",
            ),
        )
    ) {
        game.players.values.forEach {
            it.session.notify("GAME LOST, ZOMBIE REACHED THE WALL, GAME RESTARTED")
        }
    }
} else {
    Pair(
        game.copy(
            zombie = zombie,
        ),
    )
    {
        game.players.values.forEach {
            it.session.notify("WALK ${zombie.name} ${zombie.x} ${zombie.y}")
        }
    }
}
