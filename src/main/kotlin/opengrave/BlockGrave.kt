package opengrave

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import java.util.*

object BlockGrave : BlockContainer(Material.rock) {

    init {
        setRegistryName("blockgrave")
        setHardness(5.0F)
        setResistance(6000000.0F)
        setBlockBounds(0.0625F, 0.0F, 0.375F, 0.9375F, 0.875F, 0.625F)
    }

    override fun breakBlock(worldIn: World?, pos: BlockPos?, state: IBlockState?) {
        val tileEntityGrave = worldIn?.getTileEntity(pos) as? TileEntityGrave?
        tileEntityGrave?.dropItems()
        super.breakBlock(worldIn, pos, state)
    }

    override fun getPickBlock(target: MovingObjectPosition?, world: World?, pos: BlockPos?, player: EntityPlayer?) = null
    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = null

    override fun getRenderType() = 3
    override fun isOpaqueCube() = false
    override fun isFullCube() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileEntityGrave()
}