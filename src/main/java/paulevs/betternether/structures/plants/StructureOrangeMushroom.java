package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockNetherMycelium;
import paulevs.betternether.blocks.BlockOrangeMushroom;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureOrangeMushroom implements IStructure {
	@Override
	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (under instanceof BlockNetherMycelium) {
			BlockPos[] positions = new BlockPos[10];
			int count = 0;

			for (int i = 0; i < 10; i++) {
				int x = pos.getX() + (int) (random.nextGaussian() * 2);
				int z = pos.getZ() + (int) (random.nextGaussian() * 2);
				int y = pos.getY() + random.nextInt(6);

				boolean foundValidPosition = false;

				for (int j = 0; j < 6; j++) {
					BlockPos npos = new BlockPos(x, y - j, z);
					if (npos.getY() > 31) {
						under = world.getBlockState(npos.down()).getBlock();
						if (under == BlocksRegister.BLOCK_NETHER_MYCELIUM && world.getBlockState(npos).getBlock() == Blocks.AIR) {
							positions[count++] = npos;
							foundValidPosition = true;
						}
					} else {
						break;
					}
				}

				if (foundValidPosition) {
					break;
				}
			}

			if (count > 0) {
				IBlockState mushroomState = BlocksRegister.BLOCK_ORANGE_MUSHROOM.getDefaultState();

				world.captureBlockSnapshots = true;

				for (int i = 0; i < count; i++) {
					BlockPos npos = positions[i];
					int size = random.nextInt(4);

					world.setBlockState(npos, mushroomState.withProperty(BlockOrangeMushroom.SIZE, size));
				}

				world.captureBlockSnapshots = false;
				world.capturedBlockSnapshots.clear();
			}
		}
	}
}
