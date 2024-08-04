package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;


public class StructureBlackApple implements IStructure {
	private static final int OUTER_LOOP_ITERATIONS = 8;
	private static final int INNER_LOOP_ITERATIONS = 6;

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (ConfigLoader.isTerrain(under) || under == Blocks.SOUL_SAND) {
			IBlockState state = BlocksRegister.BLOCK_BLACK_APPLE.getDefaultState();
			BlockPos airPos = null;
			Material lavaMaterial = Material.LAVA;

			for (int i = 0; i < OUTER_LOOP_ITERATIONS; i++) {
				double gaussianX = random.nextGaussian() * 2;
				double gaussianZ = random.nextGaussian() * 2;
				int x = pos.getX() + (int) gaussianX;
				int z = pos.getZ() + (int) gaussianZ;
				int y = pos.getY() + random.nextInt(INNER_LOOP_ITERATIONS);
				BlockPos npos = null;

				for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
					npos = new BlockPos(x, y - j, z);
					under = world.getBlockState(npos.down()).getBlock();

					if ((ConfigLoader.isTerrain(under) || under == Blocks.SOUL_SAND) &&
							(world.getBlockState(pos).getBlock() == Blocks.AIR ||
									world.getBlockState(pos).getMaterial() != lavaMaterial)) {
						airPos = npos;
						break;
					}
				}

				if (airPos != null) {
					// Batch block state modifications
					IBlockState[] states = new IBlockState[INNER_LOOP_ITERATIONS];
					for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
						states[j] = world.getBlockState(airPos.add(0, -j, 0));
					}
					for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
						if (states[j].getMaterial().isReplaceable()) {
							world.setBlockState(airPos.add(0, -j, 0), state);
						}
					}
					break;
				}
			}
		}
	}
}

