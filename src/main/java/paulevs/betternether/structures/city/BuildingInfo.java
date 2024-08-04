package paulevs.betternether.structures.city;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BuildingInfo {
	public StructureCityBuilding building;
	public BlockPos pos;

	public BuildingInfo(StructureCityBuilding building, BlockPos pos) {
		this.building = building;
		this.pos = pos;
	}

	public BuildingInfo(NBTTagCompound root, List<StructureCityBuilding> buildings) {
		int[] prePos = root.getIntArray("pos");
		int preRot = root.getInteger("rotation");
		String name = root.getString("name");

		pos = new BlockPos(prePos[0], prePos[1], prePos[2]);
		Rotation rot = Rotation.values()[preRot];
		for (StructureCityBuilding b : buildings) {
			if (b.getName().equals(name) && b.getRotation() == rot) {
				building = b;
				return;
			}
		}
		if (building == null) {
			building = new StructureCityBuilding(name).getRotated(rot);
		}
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound root = new NBTTagCompound();
		root.setIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
		root.setString("name", building.getName());
		root.setInteger("rotation", building.getRotation().ordinal());
		return root;
	}

	// Optimized NBT serialization/deserialization
	public byte[] serializeToBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		dos.writeInt(pos.getX());
		dos.writeInt(pos.getY());
		dos.writeInt(pos.getZ());
		dos.writeUTF(building.getName());
		dos.writeInt(building.getRotation().ordinal());

		dos.close();
		return baos.toByteArray();
	}

	public static BuildingInfo deserializeFromBytes(byte[] bytes, List<StructureCityBuilding> buildings) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		int x = dis.readInt();
		int y = dis.readInt();
		int z = dis.readInt();
		BlockPos pos = new BlockPos(x, y, z);

		String name = dis.readUTF();
		int rotationOrdinal = dis.readInt();
		Rotation rotation = Rotation.values()[rotationOrdinal];

		BuildingInfo buildingInfo = new BuildingInfo(null, pos);
		for (StructureCityBuilding building : buildings) {
			if (building.getName().equals(name) && building.getRotation() == rotation) {
				buildingInfo.building = building;
				break;
			}
		}
		if (buildingInfo.building == null) {
			buildingInfo.building = new StructureCityBuilding(name).getRotated(rotation);
		}

		dis.close();
		return buildingInfo;
	}
}
