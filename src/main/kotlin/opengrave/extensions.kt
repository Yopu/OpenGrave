package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.world.World

val EntityPlayer.fullInventory: Array<ItemStack?>
    get() = arrayOf(*inventory.mainInventory, *inventory.armorInventory)

fun ItemStack.dropInWorld(world: World, pos: BlockPos) {
    val throwawayInventory = InventoryBasic("throwaway", false, 1)
    throwawayInventory.setInventorySlotContents(0, this)
    InventoryHelper.dropInventoryItems(world, pos, throwawayInventory)
}