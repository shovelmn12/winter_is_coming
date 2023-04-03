package handlers

import models.Event
import models.Game
import java.util.Optional

typealias GameNotifier = ((Event) -> Unit) -> Unit
typealias GameAction = (Game?) -> Pair<Game?, GameNotifier>?

fun createActionHandler(
    lock: Any,
    getGame: () -> Game?,
    setGame: (Game?) -> Unit,
): (GameAction) -> Optional<GameNotifier> = {
    Optional.ofNullable(
        actionHandler(
            lock,
            getGame,
            setGame,
            it,
        ),
    )
}

fun actionHandler(
    lock: Any,
    getGame: () -> Game?,
    setGame: (Game?) -> Unit,
    action: GameAction,
): GameNotifier? = synchronized(lock) {
    val result = action(getGame())

    return result?.run {
        val (next, notifier) = this

        setGame(next)

        return notifier
    }
}