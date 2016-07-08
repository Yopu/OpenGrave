package opengrave

import baubles.api.BaublesApi
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
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
                if (newPos == this)
                    continue
                positions += newPos
            }
        }
    }
    return positions
}

fun safeGetBaubles(entityPlayer: EntityPlayer): IInventory? {
    try {
        return BaublesApi.getBaubles(entityPlayer)
    } catch (e: NoClassDefFoundError) {
        return null
    }
}

fun getBaublesArray(entityPlayer: EntityPlayer): Array<ItemStack?> {
    val inventory = safeGetBaubles(entityPlayer) ?: return emptyArray()
    val itemStackList = mutableListOf<ItemStack?>()
    for (i in 0..inventory.sizeInventory) {
        itemStackList += inventory.getStackInSlot(i)
    }
    return itemStackList.toTypedArray()
}