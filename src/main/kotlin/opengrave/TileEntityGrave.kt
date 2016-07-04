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
import net.minecraft.util.IChatComponent
import net.minecraft.util.IChatComponent.Serializer

class TileEntityGrave : TileEntity() {

    companion object {
        const val ID = "opengrave.tileentitygrave"
        const val INVENTORY_NBT_KEY = "inventory"
        const val DEATH_MESSAGE_NBT_KEY = "death_message"
    }

    var inventory: Array<ItemStack?> = emptyArray()
    var deathMessage: IChatComponent? = null

    private val inventoryWrapper: IInventory
        get() = InventoryBasic("throwaway", false, inventory.size).apply {
            inventory.forEachIndexed { i, stack -> setInventorySlotContents(i, stack) }
        }

    fun takeDrops(items: Array<ItemStack?>, deathMessage: IChatComponent?) {
        this.deathMessage = deathMessage
        inventory = items
    }

    fun dropItems() = InventoryHelper.dropInventoryItems(world, pos, inventoryWrapper)

    fun returnPlayerItems(player: EntityPlayer) {
        for ((index, itemStack) in inventory.withIndex()) {
            if (itemStack == null) continue
            val occupyingItem: ItemStack? = player.inventory.getStackInSlot(index)
            if (occupyingItem == null) {
                player.inventory.setInventorySlotContents(index, itemStack)
            } else {
                if (!player.inventory.addItemStackToInventory(itemStack)) {
                    itemStack.dropInWorld(world, pos)
                }
            }
        }
        inventory = emptyArray()
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        super.readFromNBT(compound)
        val rootTagCompound = compound?.getCompoundTag(ID)

        val json = rootTagCompound?.getString(DEATH_MESSAGE_NBT_KEY).orEmpty()
        deathMessage = Serializer.jsonToComponent(json)

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

        val json = Serializer.componentToJson(deathMessage)
        rootTagCompound.setString(DEATH_MESSAGE_NBT_KEY, json)

        val tagList = NBTTagList()
        inventory.map { it?.serializeNBT() }.filterNotNull().forEach { tagList.appendTag(it) }
        rootTagCompound.setTag(INVENTORY_NBT_KEY, tagList)
        compound?.setTag(ID, rootTagCompound) ?: debugLog.severe("$this unable to write to nbt!")
    }

    override fun toString() = "TileEntityGrave@$pos"
}

