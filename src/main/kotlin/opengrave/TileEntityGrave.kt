package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity

class TileEntityGrave : TileEntity() {

    companion object {
        val ID = "opengrave.tileentitygrave"
        val INVENTORY_NBT_KEY = "inventory"
    }

    val inventory = arrayListOf<ItemStack?>()

    private val inventoryWrapper: IInventory
        get() = InventoryBasic("throwaway", false, inventory.size).apply {
            inventory.forEachIndexed { i, stack -> setInventorySlotContents(i, stack) }
        }

    fun takePlayerInventory(player: EntityPlayer) {
        inventory.addAll(player.inventory.mainInventory)
        inventory.addAll(player.inventory.armorInventory)
        player.inventory.clear()
    }

    fun dropItems() = InventoryHelper.dropInventoryItems(world, pos, inventoryWrapper)

    override fun readFromNBT(compound: NBTTagCompound?) {
        super.readFromNBT(compound)
        val rootTagCompound = compound?.getCompoundTag(ID)
        val tagList = rootTagCompound?.getTagList(INVENTORY_NBT_KEY, NBTBase.NBT_TYPES.indexOf("COMPOUND"))
        if (tagList != null) {
            for (i in 0..tagList.tagCount()) {
                val base = tagList.get(i) as? NBTTagCompound?
                if (base != null)
                    inventory += ItemStack.loadItemStackFromNBT(base)
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound?) {
        super.writeToNBT(compound)
        val rootTagCompound = NBTTagCompound()
        val tagList = NBTTagList()
        inventory.map { it?.serializeNBT() }.filterNotNull().forEach { tagList.appendTag(it) }
        rootTagCompound.setTag(INVENTORY_NBT_KEY, tagList)
        compound?.setTag(ID, rootTagCompound)
    }
}

