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


public class StructureInkBush implements IStructure {
	private static final int OUTER_LOOP_ITERATIONS = 10;
	private static final int INNER_LOOP_ITERATIONS = 6;

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (ConfigLoader.isTerrain(under) || under == Blocks.SOUL_SAND) {
			IBlockState state = BlocksRegister.BLOCK_INK_BUSH.getDefaultState();
			BlockPos airPos = null;
			Material lavaMaterial = Material.LAVA;

			for (int i = 0; i < OUTER_LOOP_ITERATIONS; i++) {
				double gaussianX = random.nextGaussian() * 2;
				double gaussianZ = random.nextGaussian() * 2;
				int x = pos.getX() + (int) gaussianX;
				int z = pos.getZ() + (int) gaussianZ;
				int y = pos.getY() + random.nextInt(INNER_LOOP_ITERATIONS);

				for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
					BlockPos npos = new BlockPos(x, y - j, z);
					if (npos.getY() > 31) {
						if (airPos == null) {
							airPos = npos;
						}

						Block underBlock = world.getBlockState(npos.down()).getBlock();
						if ((ConfigLoader.isTerrain(underBlock) || underBlock == Blocks.SOUL_SAND) &&
								(world.getBlockState(airPos).getBlock() == Blocks.AIR ||
										world.getBlockState(airPos).getMaterial() != lavaMaterial)) {
							if (world.getBlockState(npos).getMaterial().isReplaceable()) {
								world.setBlockState(npos, state);
							}
							break;
						}
					} else {
						break;
					}
				}
			}
		}
	}
}