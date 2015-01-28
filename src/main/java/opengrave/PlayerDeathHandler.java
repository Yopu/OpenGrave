package opengrave;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import static net.minecraft.util.MathHelper.floor_double;

public class PlayerDeathHandler {

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        Entity entity = event.entity;
        if (entity instanceof EntityPlayer) {
            World world = entity.worldObj;
            int x = floor_double(entity.posX);
            int y = floor_double(entity.posY);
            y = y < 0 ? 1 : y;
            int z = floor_double(entity.posZ);

            while (y < 256 && !world.isAirBlock(x, y, z))
                y++;

            while (y > 1 && world.isAirBlock(x, y - 1, z))
                y--;

            if (world.setBlock(x, y, z, OpenGrave.blockGrave)) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityGrave) {
                    ((TileEntityGrave) tileEntity).addAllItems(event.drops);
                    for (EntityItem entityItem : event.drops)
                        entityItem.setDead();
                }
            }
        }
    }
}
