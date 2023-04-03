package models

import io.ktor.websocket.*

sealed interface Event {
    companion object {
        fun commandToEvent(data: Triple<Int, DefaultWebSocketSession, Command>): Event {
            val (pid, session, command) = data

            println("commandToEvent $command")

            return when (command) {
                is Command.Exit -> Exit(
                    pid,
                )

                is Command.Invalid -> Invalid(
                    session,
                    command.command,
                )

                is Command.Start -> Start(
                    pid,
                    command.name,
                    session,
                )

                is Command.Join -> Join(
                    pid,
                    command.name,
                    session,
                )

                is Command.Shoot -> Shoot(
                    pid,
                    command.x,
                    command.y,
                )

                is Command.Stop -> Stop(
                    pid,
                    session,
                )
            }
        }
    }

    data class Start(
        val pid: Int,
        val name: String,
        val session: DefaultWebSocketSession,
    ) : Event

    data class Join(
        val pid: Int,
        val name: String,
        val session: DefaultWebSocketSession,
    ) : Event

    data class Stop(
        val pid: Int,
        val session: DefaultWebSocketSession,
    ) : Event

    data class Exit(
        val pid: Int,
    ) : Event

    data class Shoot(
        val pid: Int,
        val x: Int,
        val y: Int,
    ) : Event

    object Move : Event

    data class Win(
        val player: Player.Human,
    ) : Event

    data class Invalid(
        val session: DefaultWebSocketSession,
        val command: String,
    ) : Event
}