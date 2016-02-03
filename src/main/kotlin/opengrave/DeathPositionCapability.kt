package opengrave

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.DOWN
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.ICapabilityProvider
import opengrave.OpenGrave.DEATH_CAPABILITY

fun EntityPlayer.getDeathCapability(): IDeathPositionProvider? = getCapability(DEATH_CAPABILITY, DOWN)

interface IDeathPositionProvider {
    var pos: BlockPos?
}

class DeathPositionCapability : IDeathPositionProvider, ICapabilityProvider {

    override var pos: BlockPos? = null

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        return DEATH_CAPABILITY != null && capability == DEATH_CAPABILITY
    }

    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T {
        return (if (hasCapability(capability, facing)) this else null) as T
    }
}

object DeathPositionStorage : IStorage<IDeathPositionProvider> {
    override fun writeNBT(capability: Capability<IDeathPositionProvider>?, instance: IDeathPositionProvider?, side: EnumFacing?): NBTBase? {
        return instance?.pos?.toNBTTag()
    }

    override fun readNBT(capability: Capability<IDeathPositionProvider>?, instance: IDeathPositionProvider?, side: EnumFacing?, nbt: NBTBase?) {
        val nbtTagIntArray = nbt as? NBTTagIntArray?
        instance?.pos = nbtTagIntArray?.toBlockPos()
    }
}