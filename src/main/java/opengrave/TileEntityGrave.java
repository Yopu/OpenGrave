package opengrave;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class TileEntityGrave extends TileEntity {

    private ArrayList<ItemStack> itemStacks;

    public TileEntityGrave() {
        this.itemStacks = new ArrayList<ItemStack>();
    }

    public void addAllItems(ArrayList<EntityItem> drops) {
        for (EntityItem entityItem : drops)
            itemStacks.add(entityItem.getEntityItem());
    }

    public void dropAllItems() {
        for (ItemStack itemStack : itemStacks)
            ItemUtil.dropItemStack(itemStack, worldObj, xCoord, yCoord, zCoord);
        itemStacks.clear();
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        itemStacks.clear();
        NBTTagList tagList = data.getTagList("grave_items", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(tagCompound);
            itemStacks.add(itemStack);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        NBTTagList nbtTagList = new NBTTagList();
        for (ItemStack itemStack : itemStacks) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            itemStack.writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
        }
        data.setTag("grave_items", nbtTagList);
    }
}
