package com.minecrafttas.tasbattle;

import java.util.List;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SpectatorManager {
	
	@Getter
	private static SpectatorManager instance;
	
	/**
	 * The point where the camera is orbiting around
	 */
	private Vec3 orbitPoint;
	
	@Getter
	private boolean spectating=false;
	
	public SpectatorManager() {
		instance = this;
	}
	
	public void startSpectating() {
		spectating = true;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		
		List<Entity> entities = mc.level.getEntities(player, player.getBoundingBox().inflate(10));
		if(entities.size()>0) {
			orbitPoint = entities.get(0).getEyePosition();
		}
	}
	
	public void stopSpectating() {
		spectating = false;
	}
	
	public void spectateNextPlayer() {
		// Press E to cycle to the next player
	}
	
	public void spectatePreviousPlayer() {
		// Press Q to cycle to the next player
	}
	
	public void onMouse(LocalPlayer player, double pitchD, double yawD) {
		if(spectating) {
			player.setPos(orbitPoint);
		}else {
			player.turn(pitchD, yawD);
		}
	}
	
}
