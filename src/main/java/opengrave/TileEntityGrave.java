package opengrave;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityGrave extends TileEntity {

    private ArrayList<ItemStack> itemStacks;

    private boolean beingDestroyed = false;

    public TileEntityGrave() {
        this.itemStacks = new ArrayList<ItemStack>();
    }

    public void addAllItemStacks(List<ItemStack> drops) {
        itemStacks.addAll(drops);
    }

    public void dropAllItems() {
        for (ItemStack itemStack : itemStacks)
            ItemUtil.dropItemStack(itemStack, worldObj, xCoord, yCoord, zCoord);
        itemStacks.clear();
    }

    public List<ItemStack> getItemStacks() {
        return Collections.unmodifiableList(itemStacks);
    }

    public void setToBeDestroyed() {
        beingDestroyed = true;
    }

    public boolean isBeingDestroyed() {
        return beingDestroyed;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        beingDestroyed = data.getBoolean("grave_beingDestroyed");

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

        data.setBoolean("grave_beingDestroyed", beingDestroyed);

        NBTTagList nbtTagList = new NBTTagList();
        for (ItemStack itemStack : itemStacks) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            itemStack.writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
        }
        data.setTag("grave_items", nbtTagList);
    }
}
