package opengrave

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

const val MODID = "opengrave"

@Mod(modid = MODID, modLanguage = "Kotlin", modLanguageAdapter = "io.drakon.forgelin.KotlinAdapter")
object OpenGrave {

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        debugLog.info("Opengrave preinit $event")
        GameRegistry.registerBlock(BlockGrave)
        GameRegistry.registerTileEntity(TileEntityGrave::class.java, TileEntityGrave.ID)
        MinecraftForge.EVENT_BUS.register(DeathHandler)
    }
}