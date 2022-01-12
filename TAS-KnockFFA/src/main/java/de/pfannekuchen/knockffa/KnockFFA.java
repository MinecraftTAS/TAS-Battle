package de.pfannekuchen.knockffa;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import de.pfannekuchen.knockffa.stats.PlayerStats;

/**
 * Main and basically everything of the Knock FFA Plugin
 * @author Pancake
 */
public class KnockFFA extends JavaPlugin implements PluginMessageListener {

	private static KnockFFA instance;
	public static KnockFFA instance() { return instance; }

	/**
	 * Creates the Data Folder and registers the events on initialization
	 */
	@Override
	public void onEnable() {
		try {
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data");
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data2");
			Bukkit.getMessenger().registerIncomingPluginChannel(this, "tickratechanger:data", this);
			instance = this;
			if (!getDataFolder().exists()) getDataFolder().mkdir();
			Bukkit.getPluginManager().registerEvents(new Events(), this);
			PlayerStats.load();
			updateTickrate(4.0f);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for (Entry<Location, Integer> set : new HashMap<>(Game.blocks).entrySet()) {
						int i = set.getValue() - 1;
						if (i == 0) {
							set.getKey().getBlock().breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE), true);
							Game.blocks.remove(set.getKey());
						} else {
							Game.blocks.put(set.getKey(), i);
						}
					}
				}
			}.runTaskTimer(this, 1L, 1L);
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
			player.sendPluginMessage(KnockFFA.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(float1).array());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("tickratechanger:data")) {
			queuedPlayers.remove(player.getUniqueId());
			player.sendPluginMessage(KnockFFA.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
		}
	}

}
