package de.pancake.trc;

import java.nio.ByteBuffer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Tickrate Changer API
 * @author Pancake
 */
public abstract class TickrateChangerAPI {

	/**
	 * Updates the tickrate for all players
	 * @param tickrate New tickrate. Must be between 0.1 and 100
	 */
	public static void setTickrate(float tickrate) {
		TickrateChanger.TICKRATE = Math.max(Math.min(tickrate, 100.0f), 0.1f);
		updateServer();
		Bukkit.getOnlinePlayers().forEach(TickrateChangerAPI::updatePlayer);
	}

	/**
	 * Internally updates the tickrate for a player
	 * @param player Player
	 */
	static void updatePlayer(Player player) {
		player.sendPluginMessage(TickrateChanger.plugin, TickrateChanger.TRC_C, ByteBuffer.allocate(4).putFloat(TickrateChanger.TICKRATE).array());
	}

	/**
	 * Internally updates the tickrate for the server
	 */
	static void updateServer() {
		try {
			var msPerTick = Class.forName("net.minecraft.server.MinecraftServer").getField("b"); // unavoidable obfuscation
			msPerTick.setAccessible(true);
			msPerTick.setInt(null, (int) (1000 / TickrateChanger.TICKRATE));
			var gamespeed = Class.forName("net.minecraft.server.MinecraftServer").getField("GAMESPEED");
			gamespeed.setAccessible(true);
			gamespeed.setFloat(null, TickrateChanger.TICKRATE / 20.0f);
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			System.err.println("Unable to update the server tickrate");
			e.printStackTrace();
		}
	}

}
