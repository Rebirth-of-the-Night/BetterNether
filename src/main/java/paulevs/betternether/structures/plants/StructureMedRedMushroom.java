package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockRedLargeMushroom;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;

public class StructureMedRedMushroom implements IStructure {
	@Override
	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (ConfigLoader.isTerrain(under) || under == Blocks.SOUL_SAND) {
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
						if (under == BlocksRegister.BLOCK_NETHER_MYCELIUM) {
							positions[count++] = npos.down();
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
				IBlockState mushroomState = BlocksRegister.BLOCK_RED_LARGE_MUSHROOM.getDefaultState();
				IBlockState middleState = mushroomState.withProperty(BlockRedLargeMushroom.SHAPE, BlockRedLargeMushroom.EnumShape.MIDDLE);
				IBlockState topState = mushroomState.withProperty(BlockRedLargeMushroom.SHAPE, BlockRedLargeMushroom.EnumShape.TOP);

				world.captureBlockSnapshots = true;

				for (int i = 0; i < count; i++) {
					BlockPos npos = positions[i];
					int size = 2 + random.nextInt(3);

					for (int y = 1; y <= size; y++) {
						if (world.getBlockState(npos.up(y)).getBlock() != Blocks.AIR) {
							size = y - 1;
							break;
						}
					}

					if (size > 2) {
						for (int y = 2; y < size; y++) {
							world.setBlockState(npos.up(y), middleState);
						}
						world.setBlockState(npos.up(size), topState);
						world.setBlockState(npos.up(), mushroomState);
					}
				}

				world.captureBlockSnapshots = false;
				world.capturedBlockSnapshots.clear();
			}
		}
	}
}

