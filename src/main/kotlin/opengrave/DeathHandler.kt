package opengrave

import net.minecraft.block.BlockLiquid
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerDropsEvent
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object DeathHandler {

    @SubscribeEvent
    fun handleDeath(event: PlayerDropsEvent?) {
        if (event == null) return
        val entity = event.entity as? EntityPlayer? ?: return
        val world = entity.entityWorld
        if (world == null || world.isRemote) return

        debugLog.finest("handling $event")
        val pos = entity.findIdealGravePos()
        debugLog.finest("using $pos")
        world.setBlockState(pos, BlockGrave.defaultState, 3)
        val tileEntity = world.getTileEntity(pos) as TileEntityGrave?
        if (tileEntity?.takeDrops(event.drops) ?: false) {
            event.isCanceled = true
        }
    }

    fun Entity.findIdealGravePos(): BlockPos {
        debugLog.finest("finding ideal grave pos")
        if (worldObj.isLiquidBlock(position)) {
            val possibleFloatingPosition = worldObj.findFloatingPosition(position)
            if (possibleFloatingPosition != null) {
                debugLog.finest("found floating pos $possibleFloatingPosition")
                return possibleFloatingPosition
            }
        }
        return worldObj.findNearestIdealGravePos(position)
    }

    fun IBlockAccess.findNearestIdealGravePos(pos: BlockPos): BlockPos {
        debugLog.finest("finding nearest ideal grave pos $pos")
        val stack = arrayListOf<BlockPos>()
        stack += pos
        while (stack.isNotEmpty()) {
            val nextPos = stack.removeAt(0)
            if (isIdealGravePosition(nextPos))
                return nextPos
            if (pos.distanceSq(nextPos) >= 256)
                break
            for (side in EnumFacing.VALUES)
                stack += nextPos.offset(side)
        }
        debugLog.finest("couldn't find ideal, returning original $pos")
        return pos
    }

    fun World.findFloatingPosition(start: BlockPos): BlockPos? {
        debugLog.finest("finding floating position at $start")
        var next = start.up()
        debugLog.finest("floating up $next")
        while (isLiquidBlock(next)) {
            if (next.y >= height)
                break
            next = next.up()
            debugLog.finest("floating up $next")
        }
        if (isAirBlock(next)) {
            debugLog.finest("found air $next, returning")
            return next
        }
        debugLog.finest("couldn't find a floating position")
        return null
    }

    fun IBlockAccess.isLiquidBlock(pos: BlockPos): Boolean {
        val block = getBlockState(pos).block
        val b = block is BlockLiquid || block is IFluidBlock
        debugLog.finest("$pos ${if (b) "is" else "is not"} a liquid")
        return b
    }

    fun IBlockAccess.isIdealGravePosition(pos: BlockPos): Boolean {
        val isAir = isAirBlock(pos)
        val downBlockState = getBlockState(pos.down())
        val goodPlatform = downBlockState.block.isSideSolid(this, pos.down(), EnumFacing.UP)
        val b = isAir and goodPlatform
        debugLog.finest("$pos ${if (b) "is" else "is not"} an ideal grave position")
        return b
    }
}