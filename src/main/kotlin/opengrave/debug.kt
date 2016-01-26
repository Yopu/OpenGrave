package opengrave

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

val debugLog = Logger.getAnonymousLogger().apply {
    if (System.getenv("opengrave.debug")?.toBoolean() ?: false) {
        level = Level.ALL
        val consoleHandler = ConsoleHandler()
        consoleHandler.level = level
        addHandler(consoleHandler)
    } else {
        level = Level.OFF
    }
}



