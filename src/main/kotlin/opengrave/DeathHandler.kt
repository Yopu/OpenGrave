package opengrave

import net.minecraft.block.BlockLiquid
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.IChatComponent
import net.minecraft.world.World
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerDropsEvent
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

object DeathHandler {

    var neighborSearchDepth: Int = 0
    private var lastDeath: Pair<UUID, Array<ItemStack?>>? = null

    @SubscribeEvent
    fun handlePreDeath(event: LivingDeathEvent) {
        val entity = event.entity as? EntityPlayer ?: return
        lastDeath = entity.persistentID to entity.fullInventory
    }

    @SubscribeEvent
    fun handleDeath(event: PlayerDropsEvent) {
        val entity = event.entity as? EntityPlayer? ?: return
        val world = entity.entityWorld
        if (world == null || world.isRemote) return

        if (lastDeath == null) {
            OpenGrave.log.error("GraveHandler's lastDeath uninitialized before handling a PlayerDropsEvent")
            return
        }
        val (lastDeathPlayerID, lastDeathInventory) = lastDeath!!
        if (entity.persistentID != lastDeathPlayerID) {
            OpenGrave.log.error("GraveHandler handled a PlayerDropsEvent from the incorrect player")
            return
        }

        Debug.log.finest("handling $event")
        val pos = entity.findIdealGravePos()
        Debug.log.finest("using $pos")
        val deathMessage = event.source?.getDeathMessage(entity)
        if (world.spawnGrave(pos, lastDeathPlayerID, lastDeathInventory, deathMessage)) {
            event.isCanceled = true
        }
    }

    fun World.spawnGrave(pos: BlockPos, entityPlayerID: UUID, drops: Array<ItemStack?>, deathMessage: IChatComponent?): Boolean {
        val blockHardness = getBlockState(pos).block.getBlockHardness(this, pos)
        if (blockHardness < 0)
            return false

        setBlockState(pos, BlockGrave.defaultState, 3)
        val tileEntity = getTileEntity(pos) as TileEntityGrave? ?: return false
        tileEntity.takeDrops(entityPlayerID, drops, deathMessage)
        return true
    }

    fun Entity.findIdealGravePos(): BlockPos {
        Debug.log.finest("finding ideal grave pos")
        if (worldObj.isLiquidBlock(position)) {
            val possibleFloatingPosition = worldObj.findFloatingPosition(position)
            if (possibleFloatingPosition != null) {
                Debug.log.finest("found floating pos $possibleFloatingPosition")
                return possibleFloatingPosition
            }
        }
        return Debug.time { worldObj.findNearestIdealGravePos(position) }
    }

    fun World.findNearestIdealGravePos(pos: BlockPos): BlockPos {
        Debug.log.finest("finding nearest ideal grave pos $pos")
        if (isIdealGravePosition(pos)) {
            Debug.log.finest("original position is already ideal")
            return pos
        }
        for (neighbor in pos.neighbors(neighborSearchDepth)) {
            if (isIdealGravePosition(neighbor))
                return neighbor
        }
        Debug.log.finest("couldn't find ideal, returning original")
        return pos
    }

    fun World.findFloatingPosition(start: BlockPos): BlockPos? {
        Debug.log.finest("finding floating position at $start")
        var next = start.up()
        Debug.log.finest("floating up $next")
        while (isLiquidBlock(next)) {
            if (next.y >= height)
                break
            next = next.up()
            Debug.log.finest("floating up $next")
        }
        if (isAirBlock(next)) {
            Debug.log.finest("found air $next, returning")
            return next
        }
        Debug.log.finest("couldn't find a floating position")
        return null
    }

    fun World.isLiquidBlock(pos: BlockPos): Boolean {
        val block = getBlockState(pos).block
        val b = block is BlockLiquid || block is IFluidBlock
        Debug.log.finest("$pos ${if (b) "is" else "is not"} a liquid")
        return b
    }

    fun World.isIdealGravePosition(pos: BlockPos): Boolean {
        val isAir = isAirBlock(pos)
        val downBlockState = getBlockState(pos.down())
        val goodPlatform = downBlockState.block.isSideSolid(this, pos.down(), EnumFacing.UP)
        val b = isAir and goodPlatform
        Debug.log.finest("$pos ${if (b) "is" else "is not"} an ideal grave position")
        return b
    }
}