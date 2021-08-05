package de.pfannekuchen.tickratechanger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

public class Paperchanger extends JavaPlugin implements Listener, PluginMessageListener {

	public final File saved_tickrate = new File(getDataFolder(), "tickrate.dat");
	public float tickrate = 20;

	public static volatile List<UUID> queuedPlayers = new ArrayList<>();
	public static Paperchanger PLUGIN;
	
	@EventHandler
	public void onLateJoin(PlayerJoinEvent e) {
		queuedPlayers.add(e.getPlayer().getUniqueId());
		new Thread(() -> {
			try {
				Thread.sleep(200);
			} catch (Exception e2) {
				
			}
			if (queuedPlayers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().isOnline()) {
				queuedPlayers.remove(e.getPlayer().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().kick(Component.text("Login Failed."));
					}
				}.runTask(PLUGIN);
			}
		}).start();
	}
	
	@Override
	public void onEnable() {
		PLUGIN = this;
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "tickratechanger:data", this);
		Bukkit.getPluginManager().registerEvents(this, this);
		if (!saved_tickrate.exists()) {
			try {
				saved_tickrate.getParentFile().mkdirs();
				saved_tickrate.createNewFile();
				Files.write(saved_tickrate.toPath(), ByteBuffer.allocate(4).putFloat(20).array(), StandardOpenOption.WRITE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				updateTickrate(ByteBuffer.wrap(Files.readAllBytes(saved_tickrate.toPath())).getFloat());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onEnable();
	}
	
	private void updateTickrate(float float1) throws Exception {
		this.tickrate = float1;
		Field f = Class.forName("net.minecraft.server.MinecraftServer").getDeclaredField("tickrateServer");
		f.setAccessible(true);
		f.setFloat(null, float1);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendPluginMessage(this, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(float1).array());
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			try {
				updateTickrate(Float.parseFloat(args[0]));
				sender.sendMessage("Tickrate changed to " + args[0]);
				return true;
			} catch (Exception e) {
				sender.sendMessage("Invalid Tickrate");
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("default")) {
				try {
					Files.write(saved_tickrate.toPath(), ByteBuffer.allocate(4).putFloat(Float.parseFloat(args[1])).array(), StandardOpenOption.WRITE);
					updateTickrate(Float.parseFloat(args[1]));
					sender.sendMessage("Default Tickrate changed to " + args[1]);
					return true;
				} catch (Exception e) {
					sender.sendMessage("Invalid Tickrate");
				}
			}
		}
		return false;
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		if (channel.equalsIgnoreCase("tickratechanger:data")) {
			queuedPlayers.remove(player.getUniqueId());
			player.sendPluginMessage(PLUGIN, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
		}
	}
	
}
