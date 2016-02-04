package opengrave

import net.minecraft.init.Items
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import opengrave.handler.DeathHandler.findIdealGravePos
import opengrave.handler.DeathHandler.spawnGrave
import java.time.Duration
import java.time.Instant
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

val DEBUG_MODE = System.getenv("opengrave.debug")?.toBoolean() ?: false

val debugLog = Logger.getAnonymousLogger().apply {
    if (DEBUG_MODE) {
        level = Level.ALL
        val consoleHandler = ConsoleHandler()
        consoleHandler.level = level
        addHandler(consoleHandler)
    } else {
        level = Level.OFF
    }
}

fun debugPreInit(event: FMLPreInitializationEvent?) {
    if (DEBUG_MODE) {
        MinecraftForge.EVENT_BUS.register(DebugClickHandler)
    }
}

object DebugClickHandler {

    @SubscribeEvent
    fun handleClick(event: PlayerInteractEvent?) {
        if (!DEBUG_MODE || event == null || event.world.isRemote) return
        val rightClickingBlock = event.action == RIGHT_CLICK_BLOCK
        val rightClickingAir = event.action == RIGHT_CLICK_AIR
        val crouching = event.entityPlayer.isSneaking
        val usingStick = event.entityPlayer?.heldItem?.item == Items.stick
        if ((rightClickingBlock and usingStick) xor (rightClickingAir and crouching and usingStick)) {
            val pos = event.entityPlayer?.findIdealGravePos() ?: event.pos
            val drops = event.entityPlayer?.inventory?.run { armorInventory + mainInventory }.orEmpty().toList()
            event.world?.spawnGrave(pos, drops, ChatComponentText("DEBUG"))
        }
    }
}

fun <T> time(logger: Logger = debugLog, func: () -> T): T {
    var result: T? = null
    val duration = duration { result = func() }
    logger.finest("Duration of ${duration.toMillis()}ms")
    return result!!
}

fun duration(func: () -> Unit): Duration {
    val start = Instant.now()
    func()
    val end = Instant.now()
    return Duration.between(start, end)
}