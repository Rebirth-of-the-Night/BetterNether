package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockNetherMycelium;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureGrayMold implements IStructure {
	private static final int ITERATION_COUNT = 32;
	private static final int MAX_HEIGHT = 31;
	private static final int MAX_OFFSET = 2;
	private static final int MAX_HEIGHT_OFFSET = 6;

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (under instanceof BlockNetherMycelium) {
			IBlockState grayMoldState = BlocksRegister.BLOCK_GRAY_MOLD.getDefaultState();
			for (int i = 0; i < ITERATION_COUNT; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * MAX_OFFSET);
				int z = pos.getZ() + (int) (random.nextGaussian() * MAX_OFFSET);
				int y = pos.getY() + random.nextInt(MAX_HEIGHT_OFFSET);
				for (int j = 0; j < MAX_HEIGHT_OFFSET; j++) {
					BlockPos npos = new BlockPos(x, y - j, z);
					if (npos.getY() > MAX_HEIGHT) {
						Block underBlock = world.getBlockState(npos.down()).getBlock();
						if (underBlock == BlocksRegister.BLOCK_NETHER_MYCELIUM && world.isAirBlock(npos)) {
							world.setBlockState(npos, grayMoldState);
						}
					} else {
						break;
					}
				}
			}
		}
	}
}