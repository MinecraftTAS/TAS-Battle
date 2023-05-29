package com.minecrafttas.tasbattle;

import java.util.Arrays;
import java.util.List;

import com.minecrafttas.tasbattle.mixin.spectator.MixinMouseHandler;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

/**
 * Adds custom spectating modes. This is clientside only
 * @author Scribble
 *
 */
public class SpectatorManager {

	@Getter
	private static SpectatorManager instance;

	/**
	 * The entity that 
	 */
	private Entity spectatedEntity;

	/**
	 * The current specatting mode of the manager
	 * 
	 * @see SpectatorMode
	 */
	private SpectatorMode mode = SpectatorMode.None;
	
	public SpectatorManager() {
		instance = this;
	}

	/**
	 * Toggles between the {@link SpectatorMode#FixedAngle} mode and {@link SpectatorMode#None}
	 */
	public void cycleSpectate() {
		List<SpectatorMode> modes = SpectatorMode.valueList();
		setMode(nextObject(modes, mode));
		TASBattle.LOGGER.info("Cycling spectate to {}", mode);
	}
	
	/**
	 * Sets the {@link #mode} and runs initializing code for that mode.
	 * @param mode The {@link SpectatorMode} that should be set to {@link #mode}
	 */
	public void setMode(SpectatorMode mode) {
		if(mode == SpectatorMode.None) {
			spectatedEntity = null;
			this.mode = mode;
			return;
		}
		
		if (spectatedEntity == null) {
			spectatedEntity = getNearestSpectatedEntity();
		}
		if (spectatedEntity != null) {
			this.mode = mode;
		}
	}

	private Entity getNearestSpectatedEntity() {
		Entity nearestEntity = getNearestPlayer(100); // Get the nearest player
		
		if(nearestEntity == null && TASBattle.isDevEnvironment()) {	// If indev, fall back to the nearest entity if no player was found
			nearestEntity = getNearestEntity(100);
		}
		
		if(nearestEntity != null) {
			return nearestEntity;
		}
		return null;
	}
	
	/**
	 * Selects the next player/entity in range as the {@link #spectatedEntity}
	 */
	public void spectateNextPlayer() {
		Minecraft mc = Minecraft.getInstance();
		List<AbstractClientPlayer> playerList = mc.level.players();
		playerList.removeIf(player -> player.isSpectator()); // Remove spectators from the list

		if (!playerList.isEmpty()) {
			spectatedEntity = nextObject(playerList, spectatedEntity);
		} else if (TASBattle.isDevEnvironment()) {
			List<Entity> entitiesList = mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(100));
			spectatedEntity = nextObject(entitiesList, spectatedEntity);
		}
	}

	/**
	 * Selects the previous player/entity in range as the {@link #spectatedEntity}
	 */
	public void spectatePreviousPlayer() {
		Minecraft mc = Minecraft.getInstance();
		List<AbstractClientPlayer> playerList = mc.level.players();
		playerList.removeIf(player -> player.isSpectator()); // Remove spectators from the list

		if (!playerList.isEmpty()) {
			spectatedEntity = previousObject(playerList, spectatedEntity);
		} else if (TASBattle.isDevEnvironment()) {
			List<Entity> entities = mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(100));
			spectatedEntity = previousObject(entities, spectatedEntity);
		}
	}

	private Double anglePitch;
	private Double angleYaw;
	
	private int distance = 5;
	
	/**
	 * Main update loop of the spectator manager
	 * @param player The player that is spectating
	 * @param pitchD The pitch delta of the mouse, used when turning the player
	 * @param yawD The yaw delta of the mouse, used when turning the player
	 * @see MixinMouseHandler#redirect_turnPlayer(LocalPlayer, double, double)
	 */
	public void onMouse(LocalPlayer player, double pitchD, double yawD) {
		switch (this.mode) {
		case FixedAngle:
			if(this.spectatedEntity!=null) {
				player.lookAt(Anchor.EYES, spectatedEntity.getEyePosition());
				break;
			}
		case Orbit:
			if(this.spectatedEntity!=null) {
				if(this.anglePitch == null) {
					anglePitch = (double) player.getXRot();
				}
				if(angleYaw == null) {
					angleYaw = (double) player.getYRot();
				}
				anglePitch -= pitchD/200f;
				angleYaw -= yawD/200f;
				
				double posY = distance*Math.sin(angleYaw) + spectatedEntity.getY();
				double hyp = distance* Math.cos(angleYaw);
				double posX = hyp*Math.sin(anglePitch) + spectatedEntity.getX();
				double posZ = hyp*Math.cos(anglePitch) + spectatedEntity.getZ();
				
				player.lookAt(Anchor.EYES, spectatedEntity.getEyePosition());
				player.setPos(posX, posY, posZ);
				break;
			}
		default:
			player.turn(pitchD, yawD);
			break;
		}
	}

	/**
	 * Retrieves the next object out of a list. Wraps back to the start once the end of the list is reached
	 * @param list The list to cycle through
	 * @param currentObject The currently selected object
	 * @return The next object after the currentObject
	 */
	private <T> T nextObject(List<? extends T> list, T currentObject) {
		int index = list.indexOf(currentObject);
		if (list.size() > index + 1) {
			return list.get(index + 1);
		} else if (list.size() == index + 1) {
			return list.get(0);
		}
		return currentObject;
	}
	
	/**
	 * Retrieves the previous object out of a list. Wraps back to the end once the start of the list is reached
	 * @param list The list to cycle through
	 * @param currentObject The currently selected object
	 * @return The previous object before the currentObject
	 */
	private <T> T previousObject(List<? extends T> list, T currentObject) {
		int index = list.indexOf(spectatedEntity);
		if (0 <= index - 1) {
			return list.get(index - 1);
		} else if (-1 == index - 1) {
			return list.get(list.size() - 1);
		}
		return currentObject;
	}
	
	public boolean isSpectating() {
		return mode != SpectatorMode.None;
	}
	
	public static enum SpectatorMode {
		/**
		 * Forces the player to always look at the spectatingEntity
		 */
		FixedAngle,
		/**
		 * Forces the position and angle to the spactatingEntity. By moving the mouse, the player can orbit around the spectatingEntity
		 */
		Orbit,
		/**
		 * If spectating should be disabled
		 */
		None;
		
		public static List<SpectatorMode> valueList(){
			return Arrays.asList(values());
		}
	}
	
	private Entity getNearestPlayer(int radius) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		return mc.level.getNearestPlayer(player, radius);
	}
	
	private Entity getNearestEntity(int radius) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		return mc.level.getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(radius));
	}

	public void onScroll(int i) {
		distance += (i*-1);
	}
	
}
