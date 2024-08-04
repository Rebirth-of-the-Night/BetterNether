package paulevs.betternether.structures;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import paulevs.betternether.config.ConfigLoader;

public class StructureNBT
{
	protected ResourceLocation location;
	protected Template template;
	protected static final PlacementSettings DEFAULT_SETTINGS = new PlacementSettings()
			.setMirror(Mirror.NONE)
			.setRotation(Rotation.NONE)
			.setIgnoreEntities(false)
			.setChunk((ChunkPos) null)
			.setReplacedBlock((Block) null)
			.setIgnoreStructureBlock(false);

	public StructureNBT(String structure) {
		location = new ResourceLocation("betternether", structure);
		template = readTemplate(new ResourceLocation("betternether", structure));
	}

	protected StructureNBT(ResourceLocation location, Template template) {
		this.location = location;
		this.template = template;
	}

	public boolean generateCentered(World world, BlockPos pos, Random random) {
		return generateCentered(world, pos, Rotation.values()[random.nextInt(Rotation.values().length)]);
	}

	public boolean generateCentered(World world, BlockPos pos) {
		if (template == null) {
			System.out.println("No structure: " + location.toString());
			return false;
		}

		BlockPos blockpos2 = template.getSize();
		PlacementSettings placementsettings = DEFAULT_SETTINGS;
		template.addBlocksToWorldChunk(world, pos.add(-blockpos2.getX() >> 1, 0, -blockpos2.getZ() >> 1), placementsettings);
		return true;
	}

	public boolean generateCentered(World world, BlockPos pos, Rotation rotation) {
		if (template == null) {
			System.out.println("No structure: " + location.toString());
			return false;
		}

		BlockPos blockpos2 = getSize(rotation);
		PlacementSettings placementsettings = DEFAULT_SETTINGS.setRotation(rotation);
		pos = pos.add(-blockpos2.getX() >> 1, 0, -blockpos2.getZ() >> 1);
		template.addBlocksToWorldChunk(world, pos, placementsettings);
		return true;
	}
	
	private Template readTemplate(ResourceLocation resource)
    {
        String s = resource.getResourceDomain();
        String s1 = resource.getResourcePath();
        Path p = ConfigLoader.getStructureLoadPath().resolve(s1 + ".nbt");
        if(Files.exists(p)) {
        	try(InputStream inStream = new BufferedInputStream(Files.newInputStream(p))) {
        		return readTemplateFromStream(inStream);
        	} catch (IOException e) {
				System.err.println("Error loading Better Nether structure " + s1 + " from config dir:");
				e.printStackTrace();
				return null;
			}
        } else {
        	try(InputStream inStream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/structures/" + s1 + ".nbt")) {
        		if(inStream != null) {
        			return readTemplateFromStream(inStream);
        		} else {
        			System.err.println("Better Nether structure " + s + ":" + s1 + " not found in filesystem or classpath - double check your file paths.");
        			return null;
        		}
        	} catch (IOException e) {
				System.err.println("Error loading Better Nether structure " + ("/assets/" + s + "/structures/" + s1 + ".nbt") + " from classpath");
				e.printStackTrace();
				return null;
			}
        }
    }
	
	private Template readTemplateFromStream(InputStream stream) throws IOException
    {
        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);

        if (!nbttagcompound.hasKey("DataVersion", 99))
        {
            nbttagcompound.setInteger("DataVersion", 500);
        }

        Template template = new Template();
        template.read(nbttagcompound);
        
        return template;
    }


	public BlockPos getSize(Rotation rotation) {
		if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
			return template.getSize();
		} else {
			return template.getSize().rotate(rotation);
		}
	}
	
	public String getName()
	{
		return location.getResourcePath();
	}
}
