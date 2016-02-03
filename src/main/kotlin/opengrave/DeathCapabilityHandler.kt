package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object DeathCapabilityHandler {

    @SubscribeEvent
    fun attachToPlayer(event: AttachCapabilitiesEvent.Entity?) {
        val player = event?.entity
        if (player is EntityPlayer) {
            event?.addCapability(ResourceLocation("$MODID:DeathPositionCapability"), DeathPositionCapability())
        }
    }

    @SubscribeEvent
    fun reattachToPlayer(event: PlayerEvent.Clone?) {
        event ?: return
        if (event.wasDeath) {
            val originalDeathCap = event.original?.getCapability(OpenGrave.DEATH_CAPABILITY, EnumFacing.DOWN)
            val newDeathCap = event.entityPlayer?.getCapability(OpenGrave.DEATH_CAPABILITY, EnumFacing.DOWN)
            newDeathCap?.pos = originalDeathCap?.pos
        }
    }

    fun registerCapability() {
        CapabilityManager.INSTANCE.register(
                IDeathPositionProvider::class.java,
                DeathPositionStorage,
                DeathPositionCapability::class.java
        )
    }
}