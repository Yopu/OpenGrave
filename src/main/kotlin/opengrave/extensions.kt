package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

val EntityPlayer.fullInventory: Array<ItemStack?>
    get() = arrayOf(*inventory.mainInventory, *inventory.armorInventory)
