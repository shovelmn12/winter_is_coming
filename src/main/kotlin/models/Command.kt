package models

sealed interface Command {

    companion object {
        private val START_REGEX = Regex("^START (\\S+)$")
        private val STOP_REGEX = Regex("^STOP$")
        private val EXIT_REGEX = Regex("^EXIT$")
        private val JOIN_REGEX = Regex("^JOIN (\\S+)$")
        private val SHOOT_REGEX = Regex("^SHOOT (\\d+) (\\d+)\$")

        fun parse(command: String): Command =
            parseStart(command) ?: parseJoin(command) ?: parseShoot(command) ?: parseStop(command) ?: parseExit(command)
            ?: Invalid(command)

        private fun parseStart(command: String): Start? {
            val name = START_REGEX.matchEntire(command)?.groups?.get(1)?.value ?: return null

            return Start(
                name = name,
            )
        }

        private fun parseJoin(command: String): Join? {
            val groups = JOIN_REGEX.matchEntire(command)?.groups ?: return null
//            val idStr = groups[1]?.value ?: return null
            val name = groups[1]?.value ?: return null
//            val id = idStr.toIntOrNull() ?: return null

            return Join(
                name = name,
            )
        }

        private fun parseStop(command: String): Stop? = if (STOP_REGEX.matches(command)) {
            Stop
        } else {
            null
        }

        private fun parseExit(command: String): Exit? = if (EXIT_REGEX.matches(command)) {
            Exit
        } else {
            null
        }

        private fun parseShoot(command: String): Shoot? {
            val groups = SHOOT_REGEX.matchEntire(command)?.groups ?: return null
            val xStr = groups[1]?.value ?: return null
            val yStr = groups[2]?.value ?: return null
            val x = xStr.toIntOrNull() ?: return null
            val y = yStr.toIntOrNull() ?: return null

            return Shoot(
                x = x,
                y = y,
            )
        }
    }

    data class Start(
        val name: String,
    ) : Command

    data class Join(
        val name: String,
    ) : Command

    object Stop : Command

    object Exit : Command

    data class Shoot(
        val x: Int,
        val y: Int,
    ) : Command

    data class Invalid(
        val command: String,
    ) : Command
}