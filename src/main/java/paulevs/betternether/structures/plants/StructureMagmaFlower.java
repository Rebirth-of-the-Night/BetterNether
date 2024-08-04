package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.noise.WorleyNoise;
import paulevs.betternether.structures.IStructure;

public class StructureMagmaFlower implements IStructure {
	private static WorleyNoise offsetNoise = new WorleyNoise(0L); // Initialize offsetNoise once

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		if (ConfigLoader.isTerrain(world.getBlockState(pos).getBlock())) {
			IBlockState state = BlocksRegister.BLOCK_MAGMA_FLOWER.getDefaultState();

			for (int y = -3; y < 4; y++) {
				for (int x = -3; x < 4; x++) {
					int oz = x + (int) (offsetNoise.GetValue(x, y) * 3);
					for (int z = -3; z < 4; z++) {
						int ox = y + (int) (offsetNoise.GetValue(y, z) * 3);
						int oy = z + (int) (offsetNoise.GetValue(x, z) * 3);
						if (ox * ox + oy * oy + oz * oz <= 9) {
							BlockPos npos = pos.add(ox, oy, oz);
							IBlockState blockState = world.getBlockState(npos);
							Block blockBelow = world.getBlockState(npos.down()).getBlock();

							if (blockState.isFullBlock()) {
								world.setBlockState(npos, Blocks.MAGMA.getDefaultState());
							} else if (blockState.getBlock() == Blocks.AIR && blockBelow instanceof BlockMagma) {
								if (world.setBlockState(npos, state)) {
									world.notifyBlockUpdate(npos, Blocks.AIR.getDefaultState(), state, 3);
									world.notifyNeighborsOfStateChange(npos, state.getBlock(), false);
								}
							}
						}
					}
				}
			}
			// Optional: Update nearby blocks if necessary
			world.notifyBlockUpdate(pos, Blocks.AIR.getDefaultState(), state, 3);
			world.notifyNeighborsOfStateChange(pos, state.getBlock(), false);
		}
	}
}
