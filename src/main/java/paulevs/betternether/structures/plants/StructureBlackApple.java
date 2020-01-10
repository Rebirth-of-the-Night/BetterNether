package paulevs.betternether.structures.plants;

import paulevs.betternether.blocks.BlockCommonPlant;
import paulevs.betternether.registers.BlocksRegister;

public class StructureBlackApple extends StructureScatter
{
	public StructureBlackApple()
	{
		super(BlocksRegister.BLOCK_BLACK_APPLE, BlockCommonPlant.AGE, 4);
	}
}