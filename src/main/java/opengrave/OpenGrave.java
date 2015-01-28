package opengrave;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "opengrave")
public class OpenGrave {

    @Mod.Instance
    public static OpenGrave instance;

    public static Block blockGrave;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        blockGrave = new BlockGrave();
        GameRegistry.registerBlock(blockGrave, blockGrave.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityGrave.class, TileEntityGrave.class.getSimpleName());

        MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
    }
}
