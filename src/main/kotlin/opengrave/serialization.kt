package opengrave

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

fun NBTTagCompound.getItemStackArray(key: String): Array<ItemStack?> {
    val tagList = getTagList(key, NBTBase.NBT_TYPES.indexOf("COMPOUND"))
    val size = tagList.tagCount()
    if (size == 0)
        return emptyArray()
    val itemStackList = Array<ItemStack?>(size) { null }
    for (i in 0..size - 1) {
        val nbtTagCompound = tagList.getCompoundTagAt(i)
        val slot = nbtTagCompound.getInteger("slot")
        val itemStack = ItemStack(nbtTagCompound)
        itemStackList[slot] = itemStack
    }
    return itemStackList
}

fun Array<ItemStack?>.toNBTTag(): NBTBase {
    val nbtTagList = NBTTagList()
    for ((i, stack) in this.withIndex()) {
        val nbtTagCompound = NBTTagCompound()
        nbtTagCompound.setInteger("slot", i)
        stack?.writeToNBT(nbtTagCompound)
        nbtTagList.appendTag(nbtTagCompound)
    }
    return nbtTagList
}