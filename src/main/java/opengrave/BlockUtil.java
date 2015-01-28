package opengrave;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockUtil {

    public static boolean safeSetBlock(World world, int x, int y, int z, Block block) {
        Block blockToReplace = world.getBlock(x, y, z);
        if (blockToReplace.getBlockHardness(world, x, y, z) >= 0.0F)
            return world.setBlock(x, y, z, block);
        return false;
    }
}
