package handlers

import extensions.notify
import models.Event

fun eventToAction(event: Event): GameAction = when (event) {
    is Event.Start -> createStartHandler(
        event.pid,
        event.name,
        event.session,
    )

    is Event.Join -> createJoinHandler(
        event.pid,
        event.name,
        event.session,
    )

    is Event.Stop -> createStopHandler(
        event.pid,
        event.session,
    )

    is Event.Exit -> createExitHandler(event.pid)

    is Event.Shoot -> createShootHandler(event)

    is Event.Move -> createMoveHandler()

    is Event.Win -> createWinHandler(event.player)

    is Event.Invalid -> { game ->
        Pair(game) {
            event.session.notify("INVALID COMMAND: ${event.command}")
        }
    }
}



