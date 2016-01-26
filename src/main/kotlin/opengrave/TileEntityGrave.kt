package opengrave

import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.tileentity.TileEntity

class TileEntityGrave : TileEntity() {

    companion object {
        const val ID = "opengrave.tileentitygrave"
        const val INVENTORY_NBT_KEY = "inventory"
        const val NAME_NBT_KEY = "player_name"
    }

    val inventory = arrayListOf<ItemStack>()
    var playerName: String = "Herobrine"

    private val inventoryWrapper: IInventory
        get() = InventoryBasic("throwaway", false, inventory.size).apply {
            inventory.forEachIndexed { i, stack -> setInventorySlotContents(i, stack) }
        }

    fun takeDrops(items: MutableList<EntityItem?>?, playerName: String?): Boolean {
        if (playerName != null)
            this.playerName = playerName
        val actualDrops = items.orEmpty().filterNotNull().map { it.entityItem }
        inventory.clear()
        return inventory.addAll(actualDrops)
    }

    fun dropItems() = InventoryHelper.dropInventoryItems(world, pos, inventoryWrapper)

    override fun readFromNBT(compound: NBTTagCompound?) {
        super.readFromNBT(compound)
        val rootTagCompound = compound?.getCompoundTag(ID)

        val nameTagString = rootTagCompound?.getTag(NAME_NBT_KEY) as? NBTTagString?
        nameTagString?.string?.let { playerName = it }

        val tagList = rootTagCompound?.getTagList(INVENTORY_NBT_KEY, NBTBase.NBT_TYPES.indexOf("COMPOUND")) ?: return
        for (i in 0..tagList.tagCount()) {
            val nbt = tagList.get(i) as? NBTTagCompound? ?: continue
            val stack = ItemStack.loadItemStackFromNBT(nbt) ?: continue
            inventory += stack
        }
    }

    override fun writeToNBT(compound: NBTTagCompound?) {
        super.writeToNBT(compound)
        val rootTagCompound = NBTTagCompound()

        rootTagCompound.setTag(NAME_NBT_KEY, NBTTagString(playerName))

        val tagList = NBTTagList()
        inventory.map { it.serializeNBT() }.filterNotNull().forEach { tagList.appendTag(it) }
        rootTagCompound.setTag(INVENTORY_NBT_KEY, tagList)
        compound?.setTag(ID, rootTagCompound)
    }

    override fun toString() = "TileEntityGrave@$pos"
}

