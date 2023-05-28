package com.minecrafttas.tasbattle;

import java.util.List;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class SpectatorManager {

	@Getter
	private static SpectatorManager instance;

	private Entity spectatingPlayer;

	@Getter
	private boolean spectating = false;

	public SpectatorManager() {
		instance = this;
	}

	public void toggleSpectate() {
		if(!spectating) {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;

			Player nearestPlayer = mc.level.getNearestPlayer(player, 100);
			if (nearestPlayer != null) {
				spectatingPlayer = nearestPlayer;
				spectating = true;
			} else {
				if(TASBattle.isDevEnvironment()) { // Shit code, will clean up TODO
					Entity nearestEntity = mc.level.getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, player, player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(50));
					spectatingPlayer = nearestEntity;
					spectating = true;
				}
			}
		} else {
			spectating = false;
		}
	}

	public void spectateNextPlayer() {
		Minecraft mc = Minecraft.getInstance();
		List<AbstractClientPlayer> playerList = mc.level.players();

		if (playerList.size()!=1) {
			int index = playerList.indexOf(spectatingPlayer);
			if (playerList.size() > index + 1) {
				spectatingPlayer = playerList.get(index + 1);
			} else if (playerList.size() == index + 1) {
				spectatingPlayer = playerList.get(0);
			}
		} else {
			if(TASBattle.isDevEnvironment()) {	// Shit code, will clean up TODO
				List<Entity> entities = mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(100));
				if (!entities.isEmpty()) {
					int index = entities.indexOf(spectatingPlayer);
					if (entities.size() > index + 1) {
						spectatingPlayer = entities.get(index + 1);
					} else if (entities.size() == index + 1) {
						spectatingPlayer = entities.get(0);
					}
				} 
			}
		}
	}

	public void spectatePreviousPlayer() {
		Minecraft mc = Minecraft.getInstance();
		List<AbstractClientPlayer> playerList = mc.level.players();

		if (playerList.size()!=1) {
			int index = playerList.indexOf(spectatingPlayer);
			if (0 <= index - 1) {
				spectatingPlayer = playerList.get(index - 1);
			} else if (-1 == index - 1) {
				spectatingPlayer = playerList.get(playerList.size() - 1);
			}
		} else {
			if(TASBattle.isDevEnvironment()) {	// Shit code, will clean up TODO
				List<Entity> entities = mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(100));
				if (!entities.isEmpty()) {
					int index = entities.indexOf(spectatingPlayer);
					if (0 <= index - 1) {
						spectatingPlayer = entities.get(index - 1);
					} else if (-1 == index - 1) {
						spectatingPlayer = entities.get(entities.size() - 1);
					}
				} 
			}
		}
	}

	public void onMouse(LocalPlayer player, double pitchD, double yawD) {
		if (spectating) {
			player.lookAt(Anchor.EYES, spectatingPlayer.getEyePosition());
			// TODO proper orbiting
		} else {
			player.turn(pitchD, yawD);
		}
	}

//	private void transform(LocalPlayer player, double x, double y, double z, double pitch, double yaw) {
//		player.setPos(x, y, z);
//	}
}
