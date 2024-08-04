package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockBrownLargeMushroom;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;

public class StructureMedBrownMushroom implements IStructure {
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
				IBlockState mushroomState = BlocksRegister.BLOCK_BROWN_LARGE_MUSHROOM.getDefaultState();
				IBlockState middleState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.MIDDLE);
				IBlockState topState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.TOP);
				IBlockState sideNState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_N);
				IBlockState sideSState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_S);
				IBlockState sideEState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_E);
				IBlockState sideWState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_W);
				IBlockState cornerNState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_N);
				IBlockState cornerWState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_W);
				IBlockState cornerEState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_E);
				IBlockState cornerSState = mushroomState.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_S);

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

					boolean hasAir = true;

					if (size > 2) {
						for (int x = -1; x < 2; x++) {
							for (int z = -1; z < 2; z++) {
								hasAir = hasAir && world.getBlockState(npos.up(size).add(x, 0, z)).getBlock() == Blocks.AIR;
							}
						}
					}

					if (hasAir && size > 2) {
						world.setBlockState(npos, mushroomState);
						for (int y = 1; y < size; y++) {
							world.setBlockState(npos.up(y), middleState);
						}
						BlockPos topPos = npos.up(size);
						world.setBlockState(topPos, topState);
						world.setBlockState(topPos.north(), sideNState);
						world.setBlockState(topPos.south(), sideSState);
						world.setBlockState(topPos.east(), sideEState);
						world.setBlockState(topPos.west(), sideWState);
						world.setBlockState(topPos.north().east(), cornerNState);
						world.setBlockState(topPos.north().west(), cornerWState);
						world.setBlockState(topPos.south().east(), cornerEState);
						world.setBlockState(topPos.south().west(), cornerSState);
					}
				}

				world.captureBlockSnapshots = false;
				world.capturedBlockSnapshots.clear();
			}
		}
	}
}