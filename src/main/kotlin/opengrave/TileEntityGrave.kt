package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IChatComponent
import net.minecraft.util.IChatComponent.Serializer
import java.util.*

class TileEntityGrave : TileEntity() {

    companion object {
        const val ID = "opengrave.tileentitygrave"
        const val INVENTORY_NBT_KEY = "inventory"
        const val DEATH_MESSAGE_NBT_KEY = "death_message"
        const val ENTITY_PLAYER_ID_NBT_KEY = "entity_player_id"
        const val BAUBLES_NBT_KEY = "baubles"
    }

    var entityPlayerID: UUID? = null
    var inventory: Array<ItemStack?> = emptyArray()
    var baubles: Array<ItemStack?> = emptyArray()
    var deathMessage: IChatComponent? = null

    private val inventoryWrapper: IInventory
        get() = InventoryBasic("throwaway", false, inventory.size + baubles.size).apply {
            (inventory + baubles).forEachIndexed { i, stack -> setInventorySlotContents(i, stack) }
        }

    fun takeDrops(entityPlayerID: UUID, items: Array<ItemStack?>, baubles: Array<ItemStack?>, deathMessage: IChatComponent?) {
        this.entityPlayerID = entityPlayerID
        this.inventory = items
        this.baubles = baubles
        this.deathMessage = deathMessage
    }

    fun dropItems() = InventoryHelper.dropInventoryItems(world, pos, inventoryWrapper)

    fun returnPlayerItems(player: EntityPlayer) {
        if (player.persistentID != entityPlayerID)
            return

        player.inventory.dispenseItems(inventory)
        inventory = emptyArray()

        val baublesInventory = player.safeGetBaubles()
        if (baublesInventory != null) {
            baublesInventory.dispenseItems(baubles)
            baubles = emptyArray()
        }
    }

    private fun IInventory.dispenseItems(items: Array<ItemStack?>) {
        for ((index, itemStack) in items.withIndex()) {
            if (itemStack == null)
                continue

            var dispensed = false
            var possibleIndex = index
            while (possibleIndex < sizeInventory) {
                if (getStackInSlot(possibleIndex) == null && isItemValidForSlot(possibleIndex, itemStack)) {
                    setInventorySlotContents(index, itemStack)
                    dispensed = true
                    break
                } else {
                    possibleIndex++
                }
            }
            if (!dispensed)
                itemStack.dropInWorld(world, pos)
        }
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        super.readFromNBT(compound)
        val rootTagCompound = compound?.getCompoundTag(ID)

        val entityPlayerIDString = rootTagCompound?.getString(ENTITY_PLAYER_ID_NBT_KEY)
        if (!entityPlayerIDString.isNullOrBlank()) {
            try {
                entityPlayerID = UUID.fromString(entityPlayerIDString)
            } catch (e: IllegalArgumentException) {
                entityPlayerID = null
            }
        }

        val readItemStackArray = rootTagCompound?.getItemStackArray(INVENTORY_NBT_KEY)
        if (readItemStackArray != null) {
            inventory = readItemStackArray
        } else {
            OpenGrave.log.error("$this unable to read inventory from nbt!")
        }

        val readBaubles = rootTagCompound?.getItemStackArray(BAUBLES_NBT_KEY)
        if (readBaubles != null) {
            baubles = readBaubles
        } else {
            OpenGrave.log.error("$this unable to read baubles from nbt!")
        }

        val json = rootTagCompound?.getString(DEATH_MESSAGE_NBT_KEY).orEmpty()
        deathMessage = Serializer.jsonToComponent(json)
    }

    override fun writeToNBT(compound: NBTTagCompound?) {
        super.writeToNBT(compound)
        val rootTagCompound = NBTTagCompound()

        val entityPlayerIDString = entityPlayerID?.toString() ?: ""
        rootTagCompound.setString(ENTITY_PLAYER_ID_NBT_KEY, entityPlayerIDString)

        rootTagCompound.setTag(INVENTORY_NBT_KEY, inventory.toNBTTag())

        rootTagCompound.setTag(BAUBLES_NBT_KEY, baubles.toNBTTag())

        val deathMessageJson = Serializer.componentToJson(deathMessage)
        rootTagCompound.setString(DEATH_MESSAGE_NBT_KEY, deathMessageJson)

        compound?.setTag(ID, rootTagCompound) ?: OpenGrave.log.error("$this unable to write to nbt!")
    }

    override fun toString() = "TileEntityGrave@$pos"
}

