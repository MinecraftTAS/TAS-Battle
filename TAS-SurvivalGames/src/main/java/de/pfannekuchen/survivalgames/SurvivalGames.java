package de.pfannekuchen.survivalgames;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import de.pfannekuchen.survivalgames.stats.PlayerStats;

/**
 * Main and basically everything of the Survival Games Plugin
 * @author Pancake
 */
public class SurvivalGames extends JavaPlugin implements PluginMessageListener {

	private static SurvivalGames instance;
	public static SurvivalGames instance() { return instance; }

	/**
	 * Creates the Data Folder and registers the events on initialization
	 */
	@Override
	public void onEnable() {
		try {
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data");
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data2");
			Bukkit.getMessenger().registerIncomingPluginChannel(this, "tickratechanger:data", this);
			Bukkit.getPluginManager().registerEvents(new Events(), this);
			instance = this;
			if (!getDataFolder().exists()) getDataFolder().mkdir();
			PlayerStats.load();
			new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(12000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendPluginMessage(SurvivalGames.instance, "tickratechanger:data2", ByteBuffer.allocate(20).putInt(1).putInt(Game.alivePlayers.size()).putInt(12).putLong(Instant.now().toEpochMilli()).array());	
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static volatile List<UUID> queuedPlayers = new ArrayList<>();
	public static float tickrate = 20;
	
	public static void updateTickrate(float float1) throws Exception {
		tickrate = float1;
		Field f = Class.forName("net.minecraft.server.MinecraftServer").getField("b"); // unavoidable obfuscation
		f.setAccessible(true);
		f.setInt(null, (int) (1000 / float1));
		Field f2 = Class.forName("net.minecraft.server.MinecraftServer").getField("GAMESPEED");
		f2.setAccessible(true);
		f2.setFloat(null, float1 / 20.0f);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendPluginMessage(SurvivalGames.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(float1).array());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("tickratechanger:data")) {
			queuedPlayers.remove(player.getUniqueId());
			player.sendPluginMessage(SurvivalGames.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
		}
	}

}
