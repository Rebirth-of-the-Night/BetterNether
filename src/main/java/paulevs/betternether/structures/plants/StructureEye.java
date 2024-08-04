package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;

public class StructureEye implements IStructure {
	private static final int MIN_HEIGHT = 33;
	private static final int MIN_STALK_HEIGHT = 5;
	private static final int STALK_HEIGHT_VARIATION_DIVISOR = 3;

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		BlockPos upPos = pos.up();
		if(world.getBiome(pos) == Biomes.HELL && world.getBiome(pos.up()) == Biomes.HELL
				&& world.getBiome(pos.up(2)) == Biomes.HELL) {

			Block upBlock = world.getBlockState(pos.up()).getBlock();
			Block upUpBlock = world.getBlockState(pos.up(2)).getBlock();

			if(ConfigLoader.isTerrain(upBlock) && ConfigLoader.isTerrain(upUpBlock)) {
			int height = random.nextInt(19) + MIN_STALK_HEIGHT;
			int stalkHeight = pos.getY() - height;
			if (stalkHeight < MIN_HEIGHT) {
				return;
			}

			stalkHeight = height;
			boolean interrupted = false;
			for (int y = 1; y < height; y++) {
				BlockPos downPos = pos.down(y);
				if (world.getBlockState(downPos).getBlock() != Blocks.AIR) {
					stalkHeight = y;
					interrupted = true;
					break;
				}
			}

			if (interrupted) {
				if (stalkHeight < MIN_STALK_HEIGHT) {
					return;
				}
				height = MIN_STALK_HEIGHT + random.nextInt(stalkHeight / STALK_HEIGHT_VARIATION_DIVISOR);
			}

			IBlockState vineState = BlocksRegister.BLOCK_EYE_VINE.getDefaultState();
			IBlockState eyeState = random.nextInt(2) == 0 ?
					BlocksRegister.BLOCK_EYEBALL.getDefaultState() :
					BlocksRegister.BLOCK_EYEBALL_SMALL.getDefaultState();

			BlockPos.MutableBlockPos stalkPos = new BlockPos.MutableBlockPos(pos);
			for (int y = 0; y < height; y++) {
				stalkPos.setY(pos.getY() - y);
				world.setBlockState(stalkPos, vineState);
			}
			stalkPos.setY(pos.getY() - height);
			world.setBlockState(stalkPos, eyeState);
			}
		}
	}
}
