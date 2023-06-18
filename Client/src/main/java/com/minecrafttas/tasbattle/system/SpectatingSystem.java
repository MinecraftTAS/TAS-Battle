package com.minecrafttas.tasbattle.system;

import java.util.Arrays;
import java.util.List;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Client-side spectating module
 * @author Scribble
 */
public class SpectatingSystem {
	
	public static enum SpectatorMode {
		FIXED, // Forces the player to always look at the spectatingEntity
		ORBIT, // Forces the position and angle to the spactatingEntity. By moving the mouse, the player can orbit around the spectatingEntity
		NONE; // If spectating should be disabled
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
	 * @param p Local player
	 * @param c Camera
	 * @param f Partial ticks
	 */
	public void onCamera(LocalPlayer p, Camera c, float f) {
		switch (this.mode) {
			case FIXED:
				var entityPos = this.spectatedEntity.getEyePosition();
				var playerX = Mth.lerp(f, p.xo, p.getX());
				var playerY = Mth.lerp(f, p.yo, p.getY()) + p.getEyeHeight();
				var playerZ = Mth.lerp(f, p.zo, p.getZ());

				var xOff = entityPos.x - playerX;
				var yOff = entityPos.y - playerY;
				var zOff = entityPos.z - playerZ;

				var y = Math.sqrt(xOff * xOff + zOff * zOff);
				var xRot = Mth.wrapDegrees((float) (-Mth.atan2(yOff, y) * 57.2957763671875));
				var yRot = Mth.wrapDegrees((float) (Mth.atan2(zOff, xOff) * 57.2957763671875) - 90.0f);

				p.setXRot(xRot);
				p.setYRot(yRot);
				c.setRotation(yRot, xRot);
				c.setPosition(playerX, playerY, playerZ);
				break;
			case ORBIT:
				var anglePitch = p.getYRot() / 65.0;
				var angleYaw = -p.getXRot() / 65.0;
				
				var posY = this.distance * Math.sin(angleYaw) + this.spectatedEntity.getY();
				var hyp = this.distance * Math.cos(angleYaw);
				var posX = hyp * Math.sin(anglePitch) + this.spectatedEntity.getX();
				var posZ = hyp * Math.cos(anglePitch) + this.spectatedEntity.getZ();
				
				p.setPos(posX, posY, posZ);
				
				var entityPos2 = this.spectatedEntity.getEyePosition();

				var xOff2 = entityPos2.x - posX;
				var yOff2 = entityPos2.y - posY;
				var zOff2 = entityPos2.z - posZ;

				var y2 = Math.sqrt(xOff2 * xOff2 + zOff2 * zOff2);
				var xRot2 = Mth.wrapDegrees((float) (-Mth.atan2(yOff2, y2) * 57.2957763671875));
				var yRot2 = Mth.wrapDegrees((float) (Mth.atan2(zOff2, xOff2) * 57.2957763671875) - 90.0f);

				c.setRotation(yRot2, xRot2);
				c.setPosition(posX, posY, posZ);
				
				break;
				
//			case ORBIT:
//				if(this.spectatedEntity != null) {

//					
//					double posY = this.distance * Math.sin(this.angleYaw) + this.spectatedEntity.getY();
//					double hyp = this.distance * Math.cos(this.angleYaw);
//					double posX = hyp*Math.sin(this.anglePitch) + this.spectatedEntity.getX();
//					double posZ = hyp*Math.cos(this.anglePitch) + this.spectatedEntity.getZ();
//					
//					player.lookAt(Anchor.EYES, this.spectatedEntity.getEyePosition());
//					player.setPos(posX, posY, posZ);
//					break;
//				}
			default:
				break;
		}
	}
	
	/**
	 * Update spectating mode
	 * @param mode New spectating mode
	 */
	public void setMode(SpectatorMode mode) {
		var mc = Minecraft.getInstance();
		
		if(mode == SpectatorMode.NONE) {
			this.spectatedEntity = null;
			this.mode = mode;
			return;
		}
		
		if (this.spectatedEntity == null)
			this.spectatedEntity = this.getNearestPlayer();
		
		if (this.spectatedEntity != null) {
			this.mode = mode;
			mc.player.connection.send(new ServerboundTeleportToEntityPacket(this.spectatedEntity.getUUID()));
		}

	}
	
	/**
	 * Cycle between spectating modes
	 */
	public void cycleSpectate() {
		this.setMode(this.nextObject(Arrays.asList(SpectatorMode.values()), this.mode));
		TASBattle.LOGGER.info("Cycling spectate to {}", this.mode);
	}
	
	/**
	 * Spectate next player
	 */
	public void spectateNextPlayer() {
		var mc = Minecraft.getInstance();
		
		// get spectatable players
		var playerList = mc.level.players();
		playerList.removeIf(player -> player.isSpectator()); 

		if (!playerList.isEmpty()) {
			this.spectatedEntity = this.nextObject(playerList, this.spectatedEntity);
			if (this.isSpectating())
				mc.player.connection.send(new ServerboundTeleportToEntityPacket(this.spectatedEntity.getUUID()));
		}
	}

	/**
	 * Spectate previous player
	 */
	public void spectatePreviousPlayer() {
		var mc = Minecraft.getInstance();
		
		// get spectatable players
		var playerList = mc.level.players();
		playerList.removeIf(player -> player.isSpectator()); 

		if (!playerList.isEmpty()) {
			this.spectatedEntity = this.previousObject(playerList, this.spectatedEntity);
			if (this.isSpectating())
				mc.player.connection.send(new ServerboundTeleportToEntityPacket(this.spectatedEntity.getUUID()));
		}
	}

	/**
	 * Get nearest player
	 * @return Nearest player or null
	 */
	private Entity getNearestPlayer() {
		Minecraft mc = Minecraft.getInstance();
		return mc.level.getNearestPlayer(mc.player, 128);
	}

	/**
	 * Update distance to spectating entity
	 * @param i Scroll amount
	 */
	public void onScroll(int i) {
		this.distance = Math.min(Math.max(this.distance - i, 1), 15);
	}
	
	public boolean isSpectating() {
		return this.mode != SpectatorMode.NONE;
	}
	
	/**
	 * Retrieves the next object out of a list. Wraps back to the start once the end of the list is reached
	 * @param list The list to cycle through
	 * @param currentObject The currently selected object
	 * @return The next object after the currentObject
	 */
	private <T> T nextObject(List<? extends T> list, T currentObject) {
		int index = list.indexOf(currentObject);
		if (list.size() > index + 1)
			return list.get(index + 1);
		else if (list.size() == index + 1)
			return list.get(0);

		return currentObject;
	}
	
	/**
	 * Retrieves the previous object out of a list. Wraps back to the end once the start of the list is reached
	 * @param list The list to cycle through
	 * @param currentObject The currently selected object
	 * @return The previous object before the currentObject
	 */
	private <T> T previousObject(List<? extends T> list, T currentObject) {
		int index = list.indexOf(currentObject);
		if (0 <= index - 1)
			return list.get(index - 1);
		else if (-1 == index - 1)
			return list.get(list.size() - 1);

		return currentObject;
	}
	
}
