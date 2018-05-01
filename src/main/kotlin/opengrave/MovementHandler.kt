package opengrave

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import opengrave.DeathHandler.isIdealGravePosition
import java.util.*

object MovementHandler {

    private val positionMap: MutableMap<UUID, BlockPos> = mutableMapOf()

    @SubscribeEvent
    fun handleLivingUpdate(event: LivingUpdateEvent) {
        val entityPlayer = event.entity as? EntityPlayer? ?: return
        if (!entityPlayer.isServerWorld)
            return

        if (entityPlayer.onGround) {
            val uuid = entityPlayer.persistentID
            val lastPos = positionMap[uuid]
            val pos = entityPlayer.position
            if (pos != lastPos) {
                positionMap[uuid] = pos
            }
        }
    }

    fun closestGroundPosition(player: Entity): Pair<BlockPos, Double>? {
        val world = player.world
        val potentialGroundPos = MovementHandler.positionMap[player.persistentID] ?: return null
        if (world.isIdealGravePosition(potentialGroundPos))
            return potentialGroundPos to potentialGroundPos.distanceSq(player.position)
        return null
    }
}