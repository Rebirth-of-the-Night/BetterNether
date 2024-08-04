package paulevs.betternether.structures.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.structures.IStructure;

public class StructureWartCap implements IStructure {
	private static IBlockState inside = Blocks.RED_MUSHROOM_BLOCK.getDefaultState()
			.withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_INSIDE);
	private static IBlockState skin = Blocks.NETHER_WART_BLOCK.getDefaultState();

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		int radius = 3 + random.nextInt(3);
		int r2 = radius * radius;

		List<BlockPos> blockUpdates = new ArrayList<>(); // Store block updates

		for (int y = 0; y <= radius >> 1; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					int d = x * x + y * y * 6 + z * z;
					if (d <= r2) {
						BlockPos blockPos = pos.add(x, y, z);
						place(world, blockPos, blockUpdates);
					}
				}
			}
		}

		// Batch setBlockState calls
		for (BlockPos blockPos : blockUpdates) {
			world.setBlockState(blockPos, inside);
			if (world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR)
				world.setBlockState(blockPos.up(), skin);
			if (world.getBlockState(blockPos.north()).getBlock() == Blocks.AIR)
				world.setBlockState(blockPos.north(), skin);
			if (world.getBlockState(blockPos.south()).getBlock() == Blocks.AIR)
				world.setBlockState(blockPos.south(), skin);
			if (world.getBlockState(blockPos.east()).getBlock() == Blocks.AIR)
				world.setBlockState(blockPos.east(), skin);
			if (world.getBlockState(blockPos.west()).getBlock() == Blocks.AIR)
				world.setBlockState(blockPos.west(), skin);
		}
	}

	private void place(World world, BlockPos pos, List<BlockPos> blockUpdates) {
		IBlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.AIR || blockState == skin) {
			blockUpdates.add(pos); // Add block to blockUpdates list
		}
	}
}
