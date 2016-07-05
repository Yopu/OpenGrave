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

fun BlockPos.neighbors(offset: Int): List<BlockPos> {
    val positions = mutableListOf<BlockPos>()
    for (newX in (x - offset)..(x + offset)) {
        for (newY in (y - offset)..(y + offset)) {
            for (newZ in (z - offset)..(z + offset)) {
                val newPos = BlockPos(newX, newY, newZ)
                if (newPos !in positions) {
                    positions += newPos
                }
            }
        }
    }
    return positions
}