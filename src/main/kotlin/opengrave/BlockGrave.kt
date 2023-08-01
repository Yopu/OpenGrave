package opengrave

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

object BlockGrave : BlockContainer(Material.ROCK) {

    init {
        setRegistryName("blockgrave")
        setHardness(1.0F)
        setResistance(6000000.0F)
    }

    override fun removedByPlayer(state: IBlockState?, worldIn: World?, pos: BlockPos?, player: EntityPlayer?, willHarvest: Boolean): Boolean {
        if (worldIn == null || pos == null || player == null || worldIn.isRemote)
            return super.removedByPlayer(state,  worldIn, pos, player, willHarvest)

        val tileEntityGrave = worldIn.getTileEntity(pos) as? TileEntityGrave?
        tileEntityGrave?.returnPlayerItems(player)

        return super.removedByPlayer(state, worldIn, pos, player, willHarvest)
    }

    override fun breakBlock(worldIn: World?, pos: BlockPos?, state: IBlockState?) {
        val tileEntityGrave = worldIn?.getTileEntity(pos) as? TileEntityGrave?
        tileEntityGrave?.dropItems()
        super.breakBlock(worldIn, pos, state)
    }

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn == null || pos == null || hand == null || worldIn.isRemote || hand != EnumHand.MAIN_HAND) {
            return false
        }
        val tileEntityGrave = worldIn.getTileEntity(pos) as? TileEntityGrave?
        if (tileEntityGrave != null) {
            Debug.log.finest("${tileEntityGrave.entityPlayerID}\tInventory:\t${tileEntityGrave.inventory.joinToString()}")
            Debug.log.finest("${tileEntityGrave.entityPlayerID}\tBaubles:\t${tileEntityGrave.baubles.joinToString()}")

            tileEntityGrave.deathMessage?.let {
                playerIn?.sendStatusMessage(it, false)
            }
        }
        return false
    }

    override fun getPickBlock(p_getPickBlock_1_: IBlockState?, p_getPickBlock_2_: RayTraceResult?, p_getPickBlock_3_: World?, p_getPickBlock_4_: BlockPos?, p_getPickBlock_5_: EntityPlayer?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = null

    override fun canDropFromExplosion(explosionIn: Explosion?) = false
    override fun onBlockExploded(world: World?, pos: BlockPos?, explosion: Explosion?) {
    }

    override fun getRenderType(p_getRenderType_1_: IBlockState?): EnumBlockRenderType = EnumBlockRenderType.MODEL
    override fun isOpaqueCube(p_isOpaqueCube_1_: IBlockState?): Boolean = false
    override fun isFullCube(p_isFullCube_1_: IBlockState?): Boolean = false

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return AxisAlignedBB(0.0625, 0.0, 0.375, 0.9375, 0.875, 0.625)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileEntityGrave()
}