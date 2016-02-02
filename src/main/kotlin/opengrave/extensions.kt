package opengrave

import net.minecraft.block.BlockLiquid
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidBlock

fun BlockPos.toIntArray(): IntArray = intArrayOf(x, y, z)
fun IntArray.toNBTTag() = NBTTagIntArray(this)
fun IntArray.toBlockPos(): BlockPos? {
    if (size == 3)
        return BlockPos(this[0], this[1], this[2])
    else
        return null
}

fun World.isLiquidBlock(pos: BlockPos): Boolean {
    val block = getBlockState(pos).block
    return block is BlockLiquid || block is IFluidBlock
}