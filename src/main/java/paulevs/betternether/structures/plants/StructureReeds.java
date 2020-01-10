package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockNetherReed;
import paulevs.betternether.registers.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureReeds implements IStructure
{
	public void generate(IWorld world, BlockPos pos, Random random)
	{
		if (BlocksRegister.BLOCK_NETHER_REED.canPlaceAt(world.getBlockState(pos), world, pos))
		{
			BlockState med = BlocksRegister.BLOCK_NETHER_REED.getDefaultState().with(BlockNetherReed.TOP, false);
			int h = random.nextInt(2);
			for (int i = 0; i < h; i++)
				BlocksHelper.setWithoutUpdate(world, pos.up(i), med);
			BlocksHelper.setWithoutUpdate(world, pos.up(h), BlocksRegister.BLOCK_NETHER_REED.getDefaultState());
		}
	}
}