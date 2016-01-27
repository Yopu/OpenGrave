package opengrave;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockGrave extends BlockContainer {

    public BlockGrave() {
        super(Material.rock);
        setHardness(1.5F);
        setResistance(6000000.0F);
        setBlockName(BlockGrave.class.getSimpleName());
        setBlockTextureName("opengrave:block.grave");
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1F, 1.0F);
    }

    public static boolean spawnGraveBlock(World world, int x, int y, int z, List<ItemStack> drops) {
        if (BlockUtil.safeSetBlock(world, x, y, z, OpenGrave.blockGrave)) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null && tileEntity instanceof TileEntityGrave) {
                ((TileEntityGrave) tileEntity).addAllItemStacks(drops);
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntityGrave tileEntityGrave = (TileEntityGrave) world.getTileEntity(x, y, z);
        if (tileEntityGrave != null && !tileEntityGrave.isBeingDestroyed())
            tileEntityGrave.dropAllItems();
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (y == 0) return;
        if (!world.isAirBlock(x, y - 1, z)) return;

        int airY = y - 2;
        while (world.isAirBlock(x, airY, z) && airY > 1)
            airY--;

        TileEntityGrave tileEntity = (TileEntityGrave) world.getTileEntity(x, y, z);
        List<ItemStack> itemStacks = tileEntity.getItemStacks();

        if (spawnGraveBlock(world, x, airY + 1, z, itemStacks)) {
            tileEntity.setToBeDestroyed();
            world.setBlockToAir(x, y, z);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == ForgeDirection.UP.ordinal())
            return super.getIcon(side, meta);
        return Blocks.stone.getIcon(side, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityGrave();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return null;
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 15;
    }
}
