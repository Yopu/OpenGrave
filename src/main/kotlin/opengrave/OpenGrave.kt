package opengrave

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.InstanceFactory
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Logger

const val MODID = "opengrave"

@Mod(modid = MODID, modLanguage = "Kotlin")
object OpenGrave {

    lateinit var log: Logger

    @JvmStatic
    @InstanceFactory
    fun instanceFactory() = this

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Debug.log.info("Opengrave preinit $event")
        this.log = event.modLog

        val config = Configuration(event.suggestedConfigurationFile)
        DeathHandler.neighborSearchDepth = config.getInt("search_distance", "path_finding", 2, 0, 10,
                "The search radius for a gravestone measured from the point of death.\n" +
                        "Nota bene: search time increases polynomially with respect to the radius. O(n^3)")
        config.save()

        GameRegistry.registerBlock(BlockGrave)
        GameRegistry.registerTileEntity(TileEntityGrave::class.java, TileEntityGrave.ID)
        MinecraftForge.EVENT_BUS.register(DeathHandler)
        Debug.preInit()
    }
}