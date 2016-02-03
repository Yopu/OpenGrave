package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World

object ItemGraveCompass : Item() {

    const val LAST_DEATH_KEY = "last_death_key"

    init {
        setRegistryName(MODID, "itemgravecompass")
    }

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, playerIn: EntityPlayer?): ItemStack? {
        if (worldIn?.isRemote ?: true) return itemStackIn
        val intArray = itemStackIn?.tagCompound?.getIntArray(LAST_DEATH_KEY) ?: return itemStackIn
        val pos = intArray.toBlockPos()
        playerIn?.addChatMessage(ChatComponentText("Last death occurred at $pos"))
        return itemStackIn
    }
}