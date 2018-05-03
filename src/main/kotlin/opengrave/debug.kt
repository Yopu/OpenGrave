package opengrave

import net.minecraft.init.Items
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import opengrave.DeathHandler.findIdealGravePos
import opengrave.DeathHandler.spawnGrave
import java.time.Duration
import java.time.Instant
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

object Debug {

    private val enabled: Boolean = System.getProperty("opengrave.debug")?.toBoolean() ?: false

    val log: Logger = Logger.getAnonymousLogger().apply {
        if (enabled) {
            level = Level.ALL
            val consoleHandler = ConsoleHandler()
            consoleHandler.level = level
            addHandler(consoleHandler)
        } else {
            level = Level.OFF
        }
    }

    fun preInit() {
        if (enabled) {
            MinecraftForge.EVENT_BUS.register(DebugClickHandler)
        }
    }

    object DebugClickHandler {

        @SubscribeEvent
        fun handleClick(event: PlayerInteractEvent?) {
            if (!enabled || event == null || event.world.isRemote) return
            val entityPlayer = event.entityPlayer ?: return


            val rightClickingBlock = event is PlayerInteractEvent.RightClickBlock
            val rightClickingAir = event is PlayerInteractEvent.RightClickEmpty

            val crouching = entityPlayer.isSneaking
            val usingStick = entityPlayer.heldItemMainhand.item == Items.STICK

            if ((rightClickingBlock and usingStick) xor (rightClickingAir and crouching and usingStick)) {
                val pos = entityPlayer.findIdealGravePos()
                val drops = entityPlayer.fullInventory
                event.world?.spawnGrave(pos, entityPlayer.persistentID, drops, entityPlayer.getBaublesArray(), TextComponentString("DEBUG"))
            }
        }
    }

    fun <T> time(notation: String? = null, logger: Logger = log, func: () -> T): T {
        val (result, duration) = duration { func() }
        val prefix = if (notation != null) "$notation has a duration" else "Duration"
        logger.finest("$prefix of ${duration.toMillis()}ms")
        return result
    }

    fun <T> duration(func: () -> T): Pair<T, Duration> {
        val start = Instant.now()
        val result = func()
        val end = Instant.now()
        return result to Duration.between(start, end)
    }
}