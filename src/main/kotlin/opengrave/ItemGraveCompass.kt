package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World
import opengrave.DeathHandler.LAST_DEATH_KEY

object ItemGraveCompass : Item() {

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, playerIn: EntityPlayer?): ItemStack? {
        val intArray = itemStackIn?.tagCompound?.getIntArray(LAST_DEATH_KEY) ?: return itemStackIn
        val pos = intArray.toBlockPos()
        playerIn?.addChatMessage(ChatComponentText("Last death occurred at $pos"))
        return itemStackIn
    }
}