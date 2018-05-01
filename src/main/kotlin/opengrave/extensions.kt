package opengrave

import baubles.api.cap.BaublesCapabilities
import baubles.api.inv.BaublesInventoryWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

val EntityPlayer.fullInventory: Array<ItemStack?>
    get() {
        val inv: ArrayList<ItemStack?> = arrayListOf()
        inv.addAll(inventory.mainInventory)
        inv.addAll(inventory.armorInventory)
        return inv.toTypedArray()
    }

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

class CapabilityError : Exception()

fun EntityPlayer.safeGetBaubles(): IInventory? {
    try {
        val handler = getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null) ?: throw CapabilityError()
        handler.setPlayer(this)
        Debug.log.finest("Received baubles $inventory for $this.")
        return BaublesInventoryWrapper(handler, this)
    } catch (e: NoClassDefFoundError) {
        Debug.log.finest("Baubles API not present when retrieving $this's baubles!")
    } catch (e: CapabilityError) {
        Debug.log.finest("Capability returned null when retrieving $this's baubles!")
    }
    return null
}

fun EntityPlayer.getBaublesArray(): Array<ItemStack?> {
    val inventory = safeGetBaubles() ?: return emptyArray()
    val itemStackList = mutableListOf<ItemStack?>()
    for (i in 0 until inventory.sizeInventory - 1) {
        itemStackList += inventory.getStackInSlot(i)
    }
    return itemStackList.toTypedArray()
}