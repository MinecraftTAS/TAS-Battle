package com.minecrafttas.tasbattle;

import java.util.Arrays;
import java.util.List;

import com.minecrafttas.tasbattle.mixin.spectator.MixinMouseHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;

/**
 * Client-side spectating module
 * @author Scribble
 */
public class SpectatorManager {
	
	public static enum SpectatorMode {
		FIXED, // Forces the player to always look at the spectatingEntity
		ORBIT, // Forces the position and angle to the spactatingEntity. By moving the mouse, the player can orbit around the spectatingEntity
		NONE; // If spectating should be disabled
	}
	
	private Entity spectatedEntity;
	private SpectatorMode mode;
	private Double anglePitch;
	private Double angleYaw;
	private int distance;
	
	/**
	 * Initialize spectator manager
	 */
	public SpectatorManager() {
		this.mode = SpectatorMode.NONE;
		this.distance = 5;
	}
	
	/**
	 * Main update loop of the spectator manager
	 * @param player The player that is spectating
	 * @param pitchD The pitch delta of the mouse, used when turning the player
	 * @param yawD The yaw delta of the mouse, used when turning the player
	 * @see MixinMouseHandler#redirect_turnPlayer(LocalPlayer, double, double)
	 */
	public void onMouse(LocalPlayer player, double pitchD, double yawD) {
		switch (this.mode) {
			case FIXED:
				if(this.spectatedEntity != null) {
					player.lookAt(Anchor.EYES, this.spectatedEntity.getEyePosition());
					break;
				}
			case ORBIT:
				if(this.spectatedEntity != null) {
					if (this.anglePitch == null)
						this.anglePitch = (double) player.getXRot();
					
					if(this.angleYaw == null)
						this.angleYaw = (double) player.getYRot();
					
					this.anglePitch -= pitchD/200f;
					this.angleYaw -= yawD/200f;
					
					double posY = this.distance * Math.sin(this.angleYaw) + this.spectatedEntity.getY();
					double hyp = this.distance * Math.cos(this.angleYaw);
					double posX = hyp*Math.sin(this.anglePitch) + this.spectatedEntity.getX();
					double posZ = hyp*Math.cos(this.anglePitch) + this.spectatedEntity.getZ();
					
					player.lookAt(Anchor.EYES, this.spectatedEntity.getEyePosition());
					player.setPos(posX, posY, posZ);
					break;
				}
			default:
				player.turn(pitchD, yawD);
				break;
		}
	}
	
	/**
	 * Update spectating mode
	 * @param mode New spectating mode
	 */
	public void setMode(SpectatorMode mode) {
		if(mode == SpectatorMode.NONE) {
			this.spectatedEntity = null;
			this.mode = mode;
			return;
		}
		
		if (this.spectatedEntity == null)
			this.spectatedEntity = this.getNearestPlayer();
		
		if (this.spectatedEntity != null)
			this.mode = mode;
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

		if (!playerList.isEmpty())
			this.spectatedEntity = this.nextObject(playerList, this.spectatedEntity);
	}

	/**
	 * Spectate previous player
	 */
	public void spectatePreviousPlayer() {
		var mc = Minecraft.getInstance();
		
		// get spectatable players
		var playerList = mc.level.players();
		playerList.removeIf(player -> player.isSpectator()); 

		if (!playerList.isEmpty())
			this.spectatedEntity = this.previousObject(playerList, this.spectatedEntity);
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
		this.distance -= i;
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
