package paulevs.betternether.structures.city;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.noise.OpenSimplexNoise;
import paulevs.betternether.noise.PregennedOpenSimplexNoise;
import paulevs.betternether.structures.big.BigStructure;
import paulevs.betternether.structures.big.StructureManager;

public class CityStructureManager extends StructureManager
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();

	protected CityGenerator generator = new CityGenerator();
	private PregennedOpenSimplexNoise noise;

	public CityStructureManager(long seed)
	{
		super("city", 80, seed);
		random.setSeed(seed);
		//noise = new PregennedOpenSimplexNoise(xSize, ySize, noiseGen) OpenSimplexNoise(random.nextLong());
	}

	protected BigStructureCity makeStructure(int cx, int cz) {
		setSeed(cx, cz);
		BlockPos pos = new BlockPos(cx << 4, 40, cz << 4);
		return makeStructure(cx, cz, new BigStructureCity(pos, cx, cz, generator, random));
	}

	protected BigStructureCity makeStructure(int cx, int cz, BigStructureCity structure)
	{
		setSeed(cx, cz);
		BlockPos pos = new BlockPos(cx << 4, 40, cz << 4);
		BigStructureCity cave = new BigStructureCity(pos, cx, cz, generator, random); // rng consistency
		cave = structure;
		int caveSize = (int) (cave.getCitySide() * 0.6) + random.nextInt(50);
		makeCave(caveSize, 40, cave);
		List<BlockPos> positions = cave.getPosList(pos);
		List<Integer> radiuses = cave.getRadiuses();
		for (int i = 0; i < positions.size(); i++)
		{
			BlockPos cp = positions.get(i);
			int r = radiuses.get(i);
			//noise = new PregennedOpenSimplexNoise(r * 3, r * 3, new OpenSimplexNoise(random.nextLong()));
			makeCave(r, cp.getX(), cp.getY(), cp.getZ(), cave);
		}
		return cave;
	}

	protected void makeCave(int radius, int centerY, BigStructure structure)
	{
		int bounds = (int) (radius * 1.5);
		radius *= 0.8;
		int rr = radius * radius;
		int minY = 5 - centerY;
		int lavaH = 31 - centerY;
		int height = Math.abs(minY) + radius;
		int noiseBounds = Math.max(bounds * 2, height);
		noise = new PregennedOpenSimplexNoise(noiseBounds, noiseBounds, new OpenSimplexNoise(random.nextLong()));
		for (int x = -bounds; x < bounds; x++)
		{
			for (int y = minY; y < radius; y++)
			{
				int wy = y + centerY - 40;
				int y2 = y * 2;
				for (int z = -bounds; z < bounds; z++)
				{
					/*double dx = x + noiseX.GetValue(y2 * 0.02, z * 0.02) * 20;
					double dy = y2 + noiseX.GetValue(x * 0.02, z * 0.02) * 20;
					double dz = z + noiseX.GetValue(y * 0.02, x * 0.02) * 20;*/
					/*double dx = noise.eval(y2 * 0.02, z * 0.02) * radius;
					double dy = noise.eval(x * 0.02, z * 0.02) * radius;
					double dz = noise.eval(y * 0.02, x * 0.02) * radius;*/
					double nx = warp(x, y - minY, z + bounds);
					double ny = warp(y2, x + bounds, z + bounds);
					double nz = warp(z, x + bounds, y - minY);
					double xx = nx * nx;
					double yy = ny * ny;
					double zz = nz * nz;
					//double posRadius = radius - (Math.abs((noise.eval(x * 0.075, y * 0.075, z * 0.075) * 20)) + 10);
					if (xx + yy + zz < rr)
					{
						if (wy > lavaH)
							structure.setBlock(AIR, new BlockPos(x, wy, z));
						else
							structure.setBlock(LAVA, new BlockPos(x, wy, z));
					}
				}
			}
		}
	}

	protected void makeCave(int radius, int centerX, int centerY, int centerZ, BigStructure structure)
	{
		int bounds = (int) (radius * 1.5);
		radius *= 0.8;
		int rr = radius * radius;
		int minY = 5 - centerY;
		int lavaH = 31 - centerY;
		int height = Math.abs(minY) + bounds;
		int noiseBounds = Math.max(bounds * 2, height);
		noise = new PregennedOpenSimplexNoise(noiseBounds, noiseBounds, new OpenSimplexNoise(random.nextLong()));
		for (int x = -bounds; x < bounds; x++)
		{
			int wx = x + centerX;
			for (int y = minY; y < bounds; y++)
			{
				double y2 = y * 2;
				int wy = y + centerY - 40;
				for (int z = -bounds; z < bounds; z++)
				{
					int wz = z + centerZ;
					/*double dx = noise.eval(y * 0.02, z * 0.02) * radius - 10;
					double dy = noise.eval(x * 0.02, z * 0.02) * radius - 10;
					double dz = noise.eval(y * 0.02, x * 0.02) * radius - 10;*/
					double nx = warp(x, y - minY, z + bounds);
					double ny = warp(y2, x + bounds, z + bounds);
					double nz = warp(z, x + bounds, y - minY);
					double xx = nx * nx;
					double yy = ny * ny;
					double zz = nz * nz;
					if (xx + yy + zz < rr)
					{
						if (wy > lavaH)
							structure.setBlock(AIR, new BlockPos(wx, wy, wz));
						else
							structure.setBlock(LAVA, new BlockPos(wx, wy, wz));
					}
				}
			}
		}
	}

	private double warp(double val, int nx, int ny) {
		return val + (noise.eval(nx, ny) * 5);
	}

	@Override
	public void load(World world)
	{
		String path = world.getSaveHandler().getWorldDirectory().getAbsolutePath() + "/data/bn_" + name + ".nbt";
		File file = new File(path);
		if (file.exists())
		{
			try
			{
				FileInputStream fs = new FileInputStream(file);
				NBTTagCompound root = CompressedStreamTools.readCompressed(fs);
				fs.close();
				NBTTagList structureData = root.getTagList("structures", 10);
				for (int i = 0; i < structureData.tagCount(); i++)
				{
					BigStructureCity city = new BigStructureCity(structureData.getCompoundTagAt(i), generator);
					if(!city.generationComplete()) {
						makeStructure(city.getChunkX(), city.getChunkZ(), city);
					}
					structures.add(city);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
