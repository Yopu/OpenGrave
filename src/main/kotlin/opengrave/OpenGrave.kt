package opengrave

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Logger

const val MODID = "opengrave"

@Mod(modid = MODID, modLanguage = "Kotlin")
object OpenGrave {

    lateinit var log: Logger

    @JvmStatic
    @Mod.InstanceFactory
    fun instanceFactory() = this

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Debug.log.info("Opengrave preinit $event")
        log = event.modLog

        val config = Configuration(event.suggestedConfigurationFile)
        DeathHandler.neighborSearchDepth = config.getInt("search_distance", "path_finding", 2, 0, 10,
                "The search radius for a gravestone measured from the point of death.\n" +
                        "Nota bene: search time increases polynomially with respect to the radius. O(n^3)")
        DeathHandler.groundSearchDistance = config.getFloat("ground_distance", "path_finding", 10f, 0f, 20f,
                "The distance which a gravestone will spawn from the last cached player ground position.").toDouble()
        config.save()

        ForgeRegistries.BLOCKS.register(BlockGrave)
        GameRegistry.registerTileEntity(TileEntityGrave::class.java, TileEntityGrave.ID)
        MinecraftForge.EVENT_BUS.register(DeathHandler)
        MinecraftForge.EVENT_BUS.register(MovementHandler)
        Debug.preInit()
    }
}