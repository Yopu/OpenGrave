package opengrave

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.Explosion
import net.minecraft.world.World
import java.util.*

object BlockGrave : BlockContainer(Material.rock) {

    init {
        registryName = "blockgrave"
        setHardness(5.0F)
        setResistance(6000000.0F)
        setBlockBounds(0.0625F, 0.0F, 0.375F, 0.9375F, 0.875F, 0.625F)
    }

    override fun removedByPlayer(worldIn: World?, pos: BlockPos?, player: EntityPlayer?, willHarvest: Boolean): Boolean {
        if (worldIn?.isRemote ?: true)
            return super.removedByPlayer(worldIn, pos, player, willHarvest)
        val tileEntityGrave = worldIn?.getTileEntity(pos) as? TileEntityGrave?
        if (player != null)
            tileEntityGrave?.returnPlayerItems(player)
        return super.removedByPlayer(worldIn, pos, player, willHarvest)
    }

    override fun breakBlock(worldIn: World?, pos: BlockPos?, state: IBlockState?) {
        val tileEntityGrave = worldIn?.getTileEntity(pos) as? TileEntityGrave?
        tileEntityGrave?.dropItems()
        super.breakBlock(worldIn, pos, state)
    }

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tileEntityGrave = worldIn?.getTileEntity(pos) as? TileEntityGrave?
        tileEntityGrave?.deathMessage?.let { playerIn?.addChatMessage(it) }
        return false
    }

    override fun getPickBlock(target: MovingObjectPosition?, world: World?, pos: BlockPos?, player: EntityPlayer?) = null
    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = null

    override fun canDropFromExplosion(explosionIn: Explosion?) = false
    override fun onBlockExploded(world: World?, pos: BlockPos?, explosion: Explosion?) {
    }

    override fun getRenderType() = 3
    override fun isOpaqueCube() = false
    override fun isFullCube() = false

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileEntityGrave()
}