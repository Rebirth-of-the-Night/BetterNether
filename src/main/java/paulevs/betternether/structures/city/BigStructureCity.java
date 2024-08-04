package paulevs.betternether.structures.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import paulevs.betternether.structures.big.BigStructure;

public class BigStructureCity extends BigStructure
{
	private static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
	protected CityGenerator generator;
	protected ArrayList<BuildingInfo> buildings;
	
	public BigStructureCity(BlockPos pos, int cx, int cz, CityGenerator generator, Random random)
	{
		super(pos, cx, cz);
		this.generator = generator;
		buildings = generator.generate(pos, random);
	}
	
	public int getChunkX() {
		return cx;
	}
	
	public int getChunkZ() {
		return cz;
	}
	
	public BigStructureCity(NBTTagCompound structure, CityGenerator generator)
	{
		super(structure);
		NBTTagList builds = structure.getTagList("buildings", 10);
		buildings = new ArrayList<BuildingInfo>();
		for (int i = 0; i < builds.tagCount(); i++)
		{
			buildings.add(new BuildingInfo(builds.getCompoundTagAt(i), generator.getBuildings()));
		}
	}
	
	@Override
	public NBTTagCompound toNBT()
	{
		NBTTagCompound root = super.toNBT();
		NBTTagList builds = new NBTTagList();
		root.setTag("buildings", builds);
		for (BuildingInfo b: buildings)
			builds.appendTag(b.toNBT());
		return root;
	}
	
	@Override
	public boolean setChunk(World world, int cx, int cz)
	{
		if (super.setChunk(world, cx, cz))
		{
			int sx = (cx << 4) | 8;
			int sz = (cz << 4) | 8;
			StructureBoundingBox boundingBox = new StructureBoundingBox(sx, 0, sz, sx + 15, 256, sz + 15);
			for (BuildingInfo b: buildings)
				b.building.placeInChunk(world, b.pos, boundingBox);
			/*for (int y = 60; y < 120; y++)
			{
				for (int x = 0; x < 16; x++)
				{
					int wx = x + sx;
					for (int z = 0; z < 16; z++)
					{
						int wz = z + sz;
						BlockPos pos = new BlockPos(wx, y, wz);
						Block b = world.getBlockState(pos).getBlock();
						if (b == Blocks.FLOWING_LAVA)
							world.setBlockState(pos, AIR);
						else if (b == Blocks.LAVA)
							world.setBlockState(pos, NETHERRACK);
					}
				}
			}*/
// Create a list to hold the threads
			List<Thread> threads = new ArrayList<>();

			for (int y = 5; y < 32; y++) {
				for (int x = 0; x < 16; x++) {
					int wx = x + sx;
					for (int z = 0; z < 16; z++) {
						int wz = z + sz;
						BlockPos pos = new BlockPos(wx, y, wz);

						// Create a new thread for each block check
						Thread thread = new Thread(() -> {
							IBlockState blockState = world.getBlockState(pos);
							Block block = blockState.getBlock();
							if (block == Blocks.FLOWING_LAVA) {
								world.setBlockState(pos, LAVA, 2 | 16);
							}
						});

						// Start the thread and add it to the list
						thread.start();
						threads.add(thread);
					}
				}
			}

			// Wait for all threads to finish
			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	
	public List<BlockPos> getPosList(BlockPos pos)
	{
		ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
		for (BuildingInfo bi: buildings)
		{
			BlockPos center = bi.building.getBoundingBox().getCenter();
			center = center.add(bi.pos).subtract(pos);
			center = center.add(0, 40 - center.getY(), 0);
			positions.add(center);
		}
		return positions;
	}
	
	public List<Integer> getRadiuses()
	{
		ArrayList<Integer> rad = new ArrayList<Integer>();
		for (BuildingInfo bi: buildings)
		{
			int sideX = bi.building.getBoundingBox().getSideX();
			int sideZ = bi.building.getBoundingBox().getSideZ();
			rad.add(Math.max(sideX, sideZ));
		}
		return rad;
	}
	
	public int getCitySide()
	{
		BuildingInfo info = buildings.get(0);
		int minX = info.pos.getX();
		int maxX = info.building.getBoundingBox().getMaxX() + info.pos.getX();
		int minZ = info.pos.getZ();
		int maxZ = info.building.getBoundingBox().getMaxZ() + info.pos.getZ();
		for (BuildingInfo bi: buildings)
		{
			minX = Math.min(minX, bi.pos.getX());
			maxX = Math.max(maxX, bi.building.getBoundingBox().getMaxX() + bi.pos.getX());
			minZ = Math.min(minZ, bi.pos.getZ());
			maxZ = Math.max(maxZ, bi.building.getBoundingBox().getMinZ() + bi.pos.getZ());
		}
		return Math.max(maxX - minX, maxZ - minZ);
	}
}
