package paulevs.betternether.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import paulevs.betternether.BetterNether;

public class BlockMagmaFlower extends Block implements IGrowable
{
	public static final PropertyInteger SIZE = PropertyInteger.create("size", 0, 3);
	private static final AxisAlignedBB COLLIDE_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.75, 0.9375);
	
	public BlockMagmaFlower()
	{
		super(Material.PLANTS, MapColor.ORANGE_STAINED_HARDENED_CLAY);
		this.setRegistryName("magma_flower");
		this.setUnlocalizedName("magma_flower");
		this.setHardness(0.5F);
		this.setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SIZE, 0));
		this.setCreativeTab(BetterNether.BN_TAB);
		this.setTickRandomly(true);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return COLLIDE_AABB;
    }
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getBlock() instanceof BlockMagma;
    }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		if (!canPlaceBlockAt(worldIn, pos))
			worldIn.destroyBlock(pos, true);
    }

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {

		if (worldIn.isRemote) return;

		super.updateTick(worldIn, pos, state, rand);
		if (canGrow(worldIn, pos, state, false) && (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt(16) == 0)))
			{
				grow(worldIn, rand, pos, state);
				ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
		}
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(SIZE, meta);
	}
	
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(SIZE);
	}
	
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {SIZE});
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public Block.EnumOffsetType getOffsetType()
    {
		return Block.EnumOffsetType.XYZ;
    }
	
	private void spawnSeeds(World world, BlockPos pos, Random random)
	{
		if (!world.isRemote)
		{
			ItemStack drop = new ItemStack(Items.MAGMA_CREAM, 1 + random.nextInt(3));
			EntityItem itemEntity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
			world.spawnEntity(itemEntity);
			drop = new ItemStack(this, 1 + random.nextInt(3));
			itemEntity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
			world.spawnEntity(itemEntity);
		}
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{
		if (state.getValue(SIZE) == 3)
			spawnSeeds(worldIn, pos, worldIn.rand);
		worldIn.destroyBlock(pos, true);
	}
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		if (!canPlaceBlockAt(worldIn, pos))
		{
			return false;
		}
		return state.getValue(SIZE) < 3;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if (worldIn.isRemote) return;

		int size = state.getValue(SIZE);
		if (!canPlaceBlockAt(worldIn, pos))
			worldIn.destroyBlock(pos, true);
		else if (canGrow(worldIn, pos, state, false))
		{
			worldIn.setBlockState(pos, state.withProperty(SIZE, size + 1));
		}
	}
}
