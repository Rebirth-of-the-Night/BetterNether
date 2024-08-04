package paulevs.betternether.structures;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockCincinnasiteFireBowl;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;

public class StructureAltar implements IStructureWorld {
	@Override
	public void generate(World world, BlockPos pos, Rotation rotation) {
		if (shouldGenerateAltar(world, pos)) {
			IBlockState pillar = BlocksRegister.BLOCK_CINCINNASITE_WALL.getDefaultState();
			IBlockState cincinnasitePillar = BlocksRegister.BLOCK_CINCINNASITE_PILLAR.getDefaultState();
			IBlockState cincinnasiteFireBowl = Block.isEqualTo(BlocksRegister.BLOCK_CINCINNASITE_FIRE_BOWL, Blocks.AIR) ? Blocks.AIR.getDefaultState() : BlocksRegister.BLOCK_CINCINNASITE_FIRE_BOWL.getDefaultState().withProperty(BlockCincinnasiteFireBowl.STATE, true);
			BlockPos[] positions = {
					pos,
					pos.up(),
					pos.north().east(),
					pos.north().west(),
					pos.south().east(),
					pos.south().west()
			};
			IBlockState[] states = {
					cincinnasitePillar,
					cincinnasiteFireBowl,
					pillar,
					pillar,
					pillar,
					pillar
			};
			setBlocksAndNotifyAdequately(world, positions, states);
			pos = pos.down();
			BlockPos[] netherrackPositions = {
					pos.north().east(),
					pos.north().west(),
					pos.south().east(),
					pos.south().west()
			};
			IBlockState netherrackState = Blocks.NETHERRACK.getDefaultState();
			setBlocksIfNotTopSolid(world, netherrackPositions, netherrackState);
		}
	}

	private boolean shouldGenerateAltar(World world, BlockPos pos) {
		IBlockState downState = world.getBlockState(pos.down());
		IBlockState state = world.getBlockState(pos);
		IBlockState upState = world.getBlockState(pos.up());
		return ConfigLoader.isGenTerrain(downState.getBlock()) &&
				state.getBlock() == Blocks.AIR &&
				upState.getBlock() == Blocks.AIR;
	}

	private void setBlocksAndNotifyAdequately(World world, BlockPos[] positions, IBlockState[] states) {
		for (int i = 0; i < positions.length; i++) {
			BlockPos pos = positions[i];
			IBlockState state = states[i];
			setBlockAndNotifyAdequately(world, pos, state);
		}
	}

	private void setBlocksIfNotTopSolid(World world, BlockPos[] positions, IBlockState state) {
		for (BlockPos pos : positions) {
			if (!isTopSolid(world, pos)) {
				setBlockAndNotifyAdequately(world, pos, state);
			}
		}
	}
}