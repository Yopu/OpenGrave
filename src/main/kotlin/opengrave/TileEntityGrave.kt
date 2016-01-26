package opengrave

import net.minecraft.entity.item.EntityItem
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

    val inventory = arrayListOf<ItemStack>()

    private val inventoryWrapper: IInventory
        get() = InventoryBasic("throwaway", false, inventory.size).apply {
            inventory.forEachIndexed { i, stack -> setInventorySlotContents(i, stack) }
        }

    fun takeDrops(items: MutableList<EntityItem?>?): Boolean {
        val actualDrops = items.orEmpty().filterNotNull().map { it.entityItem }
        inventory.clear()
        return inventory.addAll(actualDrops)
    }

    fun dropItems() = InventoryHelper.dropInventoryItems(world, pos, inventoryWrapper)

    override fun readFromNBT(compound: NBTTagCompound?) {
        super.readFromNBT(compound)
        val rootTagCompound = compound?.getCompoundTag(ID)
        val tagList = rootTagCompound?.getTagList(INVENTORY_NBT_KEY, NBTBase.NBT_TYPES.indexOf("COMPOUND"))
        if (tagList == null) {
            debugLog.severe("$this unable to read from nbt!")
            return
        }
        for (i in 0..tagList.tagCount()) {
            val nbt = tagList.get(i) as? NBTTagCompound? ?: continue
            val stack = ItemStack.loadItemStackFromNBT(nbt) ?: continue
            inventory += stack
        }
    }

    override fun writeToNBT(compound: NBTTagCompound?) {
        super.writeToNBT(compound)
        val rootTagCompound = NBTTagCompound()
        val tagList = NBTTagList()
        inventory.map { it.serializeNBT() }.filterNotNull().forEach { tagList.appendTag(it) }
        rootTagCompound.setTag(INVENTORY_NBT_KEY, tagList)
        compound?.setTag(ID, rootTagCompound) ?: debugLog.severe("$this unable to write to nbt!")
    }

    override fun toString() = "TileEntityGrave@$pos"
}

