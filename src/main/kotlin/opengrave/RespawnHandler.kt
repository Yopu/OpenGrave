package opengrave

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent
import opengrave.ItemGraveCompass.LAST_DEATH_KEY

object RespawnHandler {

    @SubscribeEvent
    fun handleRespwan(event: PlayerRespawnEvent?) {
        val player = event?.player ?: return
        val deathPositionCapability = player.getDeathCapability()
        val pos = deathPositionCapability?.pos ?: return

        val inventory = player.inventory?.mainInventory ?: return
        val i = player.inventory?.firstEmptyStack ?: 0

        val itemStack = ItemStack(ItemGraveCompass)
        itemStack.setTagInfo(LAST_DEATH_KEY, pos.toNBTTag())

        inventory[i] = itemStack
    }
}