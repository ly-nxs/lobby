package lynx.lobby;

import net.minecraft.util.math.Vec3d;

class PlayerData {

	public Vec3d position;
	public float yaw;
	public float pitch;

	public PlayerData(Vec3d position, float yaw, float pitch) {

		this.position = position;
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
