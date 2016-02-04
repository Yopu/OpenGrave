package opengrave.handler

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.IChatComponent
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerDropsEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import opengrave.*

object DeathHandler {

    @SubscribeEvent
    fun handleDeath(event: PlayerDropsEvent?) {
        if (event == null) return
        val player = event.entity as? EntityPlayer? ?: return
        val world = player.entityWorld
        if (world == null || world.isRemote) return

        debugLog.finest("handling $event")
        val pos = player.findIdealGravePos()
        debugLog.finest("using $pos")
        val deathMessage = event.source?.getDeathMessage(player)
        val drops = event.drops.orEmpty().filterNotNull().map { it.entityItem }
        if (world.spawnGrave(pos, drops, deathMessage)) {
            player.getDeathCapability()?.pos = pos
            event.isCanceled = true
        }
    }

    fun World.spawnGrave(pos: BlockPos, drops: List<ItemStack>, deathMessage: IChatComponent?): Boolean {
        val blockHardness = getBlockState(pos).block.getBlockHardness(this, pos)
        if (blockHardness < 0)
            return false

        setBlockState(pos, BlockGrave.defaultState, 3)
        val tileEntity = getTileEntity(pos) as TileEntityGrave?
        return tileEntity?.takeDrops(drops, deathMessage) ?: false
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
        return time { worldObj.findNearestIdealGravePos(position) }
    }

    fun World.findNearestIdealGravePos(pos: BlockPos): BlockPos {
        debugLog.finest("finding nearest ideal grave pos $pos")
        val stack = arrayListOf<BlockPos>()
        stack += pos
        while (stack.isNotEmpty()) {
            val nextPos = stack.removeAt(0)
            if (isIdealGravePosition(nextPos))
                return nextPos
            if (pos.distanceSq(nextPos) >= 10)
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

    fun World.isIdealGravePosition(pos: BlockPos): Boolean {
        val isAir = isAirBlock(pos)
        val downBlockState = getBlockState(pos.down())
        val goodPlatform = downBlockState.block.isSideSolid(this, pos.down(), EnumFacing.UP)
        val b = isAir and goodPlatform
        debugLog.finest("$pos ${if (b) "is" else "is not"} an ideal grave position")
        return b
    }
}