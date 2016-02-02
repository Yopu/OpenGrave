package opengrave

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent
import opengrave.DeathHandler.LAST_DEATH_KEY

object RespawnHandler {

    @SubscribeEvent
    fun handleRespwan(event: PlayerRespawnEvent?) {
        val player = event?.player ?: return
        val posIntArray = player.entityData?.getIntArray(LAST_DEATH_KEY) ?: return

        val inventory = player.inventory?.mainInventory ?: return
        val i = player.inventory?.firstEmptyStack ?: 0

        val itemStack = ItemStack(ItemGraveCompass)
        itemStack.setTagInfo(LAST_DEATH_KEY, posIntArray.toNBTTag())

        inventory[i] = itemStack
    }
}