package opengrave;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemUtil {

    public static boolean dropItemStack(ItemStack itemStack, World world, int x, int y, int z) {
        Random random = world.rand;

        float entityItemPosX = x + random.nextFloat() * 0.8F + 0.1F;
        float entityItemPosY = y + random.nextFloat() * 0.8F + 0.1F;
        float entityItemPosZ = z + random.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, entityItemPosX, entityItemPosY, entityItemPosZ, itemStack.copy());

        entityItem.motionX = random.nextGaussian() * 0.05F;
        entityItem.motionY = random.nextGaussian() * 0.05F + 0.2F;
        entityItem.motionZ = random.nextGaussian() * 0.05F;

        return world.spawnEntityInWorld(entityItem);
    }

    public static List<ItemStack> getItemStacks(List<EntityItem> items) {
        ArrayList<ItemStack> itemStacks = new ArrayList<ItemStack>();
        for (EntityItem entityItem : items) {
            itemStacks.add(entityItem.getEntityItem());
        }
        return itemStacks;
    }
}
