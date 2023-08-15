package com.minecrafttas.tasbattle.system;

import com.minecrafttas.tasbattle.TASBattle;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Client-side spectating module
 * @author Scribble
 */
public class SpectatingSystem {
	
	public enum SpectatorMode {
		FIXED, // forces the player to always look at the spectatingEntity
		ORBIT // forces the position and angle to the spactatingEntity. By moving the mouse, the player can orbit around the spectatingEntity
	}
	
	private Entity spectatedEntity;
	private SpectatorMode mode;
	private int distance;
	
	/**
	 * Initialize spectator manager
	 */
	public SpectatingSystem() {
		this.mode = SpectatorMode.NONE;
		this.distance = 5;
	}
	
	/**
	 * Main update loop of the spectator manager
	 * @param cameraEntity Camera entity
	 * @param c Camera
	 * @param f Partial ticks
	 */
	public void onCamera(Entity cameraEntity, Camera c, float f) {
		var entityPos = this.spectatedEntity.getEyePosition();
		var playerX = Mth.lerp(f, cameraEntity.xo, cameraEntity.getX());
		var playerY = Mth.lerp(f, cameraEntity.yo, cameraEntity.getY()) + cameraEntity.getEyeHeight();
		var playerZ = Mth.lerp(f, cameraEntity.zo, cameraEntity.getZ());
        switch (this.mode) {
            case FIXED -> {
				// calculate rotation
                var xOff = entityPos.x - playerX;
                var yOff = entityPos.y - playerY;
                var zOff = entityPos.z - playerZ;
                var y = Math.sqrt(xOff * xOff + zOff * zOff);
                var xRot = Mth.wrapDegrees((float) (-Mth.atan2(yOff, y) * 57.2957763671875));
                var yRot = Mth.wrapDegrees((float) (Mth.atan2(zOff, xOff) * 57.2957763671875) - 90.0f);
				cameraEntity.setXRot(xRot);
				cameraEntity.setYRot(yRot);

				// update rotation
                c.setRotation(yRot, xRot);
                c.setPosition(playerX, playerY, playerZ);
            }
            case ORBIT -> {
				// calculate rotation
                var anglePitch = cameraEntity.getYRot() / 65.0;
                var angleYaw = -cameraEntity.getXRot() / 65.0;
                var posY = this.distance * Math.sin(angleYaw) + this.spectatedEntity.getY();
                var hyp = this.distance * Math.cos(angleYaw);
                var posX = hyp * Math.sin(anglePitch) + this.spectatedEntity.getX();
                var posZ = hyp * Math.cos(anglePitch) + this.spectatedEntity.getZ();
                var xOff2 = entityPos.x - posX;
                var yOff2 = entityPos.y - posY;
                var zOff2 = entityPos.z - posZ;
                var y2 = Math.sqrt(xOff2 * xOff2 + zOff2 * zOff2);
                var xRot2 = Mth.wrapDegrees((float) (-Mth.atan2(yOff2, y2) * 57.2957763671875));
                var yRot2 = Mth.wrapDegrees((float) (Mth.atan2(zOff2, xOff2) * 57.2957763671875) - 90.0f);

				// update rotation
                c.setRotation(yRot2, xRot2);
                c.setPosition(posX, posY, posZ);
            }
        }
	}
	
	/**
	 * Spectate an entity
	 * @param e Entity
	 */
	public void spectate(Entity e) {
		this.spectatedEntity = e;
		this.mode = e == null ? null : SpectatorMode.FIXED;
	}

	/**
	 * Cycle between spectating modes
	 */
	public void changeMode() {
		if (this.spectatedEntity == null)
			return;

		this.mode = this.mode == SpectatorMode.FIXED ? SpectatorMode.ORBIT : SpectatorMode.FIXED;
		TASBattle.LOGGER.info("Cycling spectate to {}", this.mode);
	}

	/**
	 * Update distance to spectating entity
	 * @param i Scroll amount
	 */
	public void onScroll(int i) {
		this.distance = Math.min(Math.max(this.distance - i, 2), 15);
	}
	
	public boolean isSpectating() {
		return this.mode != null;
	}
	
}
