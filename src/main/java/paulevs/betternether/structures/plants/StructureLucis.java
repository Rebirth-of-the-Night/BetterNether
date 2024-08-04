package paulevs.betternether.structures.plants;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockLucisMushroom;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureLucis implements IStructure {
	@Override
	public void generate(World world, BlockPos pos, Random random) {
		IBlockState center = BlocksRegister.BLOCK_LUCIS_MUSHROOM.getDefaultState().withProperty(BlockLucisMushroom.SHAPE, BlockLucisMushroom.EnumShape.CENTER);
		IBlockState side = BlocksRegister.BLOCK_LUCIS_MUSHROOM.getDefaultState().withProperty(BlockLucisMushroom.SHAPE, BlockLucisMushroom.EnumShape.SIDE);
		IBlockState corner = BlocksRegister.BLOCK_LUCIS_MUSHROOM.getDefaultState().withProperty(BlockLucisMushroom.SHAPE, BlockLucisMushroom.EnumShape.CORNER);

		if (random.nextInt(3) == 0) {
			if (world.isAirBlock(pos))
				world.setBlockState(pos, center);

			Map<EnumFacing, BlockLucisMushroom.EnumDir> facingMapping = createFacingMapping();

			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(facing);
				if (world.isAirBlock(offsetPos))
					world.setBlockState(offsetPos, side.withProperty(BlockLucisMushroom.FACING, facingMapping.get(facing)));
			}

			for (EnumFacing cornerFacing : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(cornerFacing).offset(cornerFacing.rotateY());
				if (world.isAirBlock(offsetPos))
					world.setBlockState(offsetPos, corner.withProperty(BlockLucisMushroom.FACING, facingMapping.get(cornerFacing.getOpposite())));
			}

		} else {
			boolean offset = false;
			EnumFacing offsetFacing = null;

			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(facing);
				if (world.getBlockState(offsetPos).isFullBlock()) {
					pos = offsetPos;
					offset = true;
					offsetFacing = facing;
					break;
				}
			}

			if (!offset)
				world.setBlockState(pos, corner.withProperty(BlockLucisMushroom.FACING, BlockLucisMushroom.EnumDir.SOUTH));

			Map<EnumFacing, BlockLucisMushroom.EnumDir> cornerFacingMapping = createCornerFacingMapping();

			for (EnumFacing cornerFacing : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(cornerFacing.rotateYCCW());
				if (world.isAirBlock(offsetPos))
					world.setBlockState(offsetPos, corner.withProperty(BlockLucisMushroom.FACING, cornerFacingMapping.get(cornerFacing.getOpposite())));
			}
		}
	}

	private Map<EnumFacing, BlockLucisMushroom.EnumDir> createFacingMapping() {
		Map<EnumFacing, BlockLucisMushroom.EnumDir> mapping = new EnumMap<>(EnumFacing.class);
		mapping.put(EnumFacing.NORTH, BlockLucisMushroom.EnumDir.NORTH);
		mapping.put(EnumFacing.SOUTH, BlockLucisMushroom.EnumDir.SOUTH);
		mapping.put(EnumFacing.EAST, BlockLucisMushroom.EnumDir.EAST);
		mapping.put(EnumFacing.WEST, BlockLucisMushroom.EnumDir.WEST);
		return mapping;
	}

	private Map<EnumFacing, BlockLucisMushroom.EnumDir> createCornerFacingMapping() {
		Map<EnumFacing, BlockLucisMushroom.EnumDir> mapping = new EnumMap<>(EnumFacing.class);
		mapping.put(EnumFacing.NORTH, BlockLucisMushroom.EnumDir.SOUTH);
		mapping.put(EnumFacing.SOUTH, BlockLucisMushroom.EnumDir.NORTH);
		mapping.put(EnumFacing.EAST, BlockLucisMushroom.EnumDir.WEST);
		mapping.put(EnumFacing.WEST, BlockLucisMushroom.EnumDir.EAST);
		return mapping;
	}
}