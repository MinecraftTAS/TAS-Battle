package com.minecrafttas.tasbattle.tickratechanger;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractModule;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Tickrate Changer module
 * @author Pancake
 */
public class TickrateChanger extends AbstractModule implements PluginMessageListener, Listener {
	
	private static final String TRC_CHANNEL = "tickratechanger:data";

	private HashMap<UUID, BukkitTask> verificationTimers = new HashMap<>();
	private TASBattle plugin;
	private float tickrate;
	
	/**
	 * Initialize tickrate changer
	 */
	@Override
	public void onEnable(TASBattle plugin) {
		this.plugin = plugin;

		// register plugin channels
		var messenger = Bukkit.getMessenger();
		messenger.registerOutgoingPluginChannel(plugin, TRC_CHANNEL);
		messenger.registerIncomingPluginChannel(plugin, TRC_CHANNEL, this);

		// register events
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.setTickrate(20.0f);
	}

	/**
	 * Update tickrate
	 * @param tickrate New tickrate between 0.1 and 100
	 */
	public void setTickrate(float tickrate) {
		if (tickrate < 0.1f || tickrate > 100.0f)
			return;
		
			
		// update server tickrate
		this.tickrate = tickrate;
		try {
			var msPerTick = Class.forName("net.minecraft.server.MinecraftServer").getField("MS_PER_TICK"); // unavoidable obfuscation
			msPerTick.setAccessible(true);
			msPerTick.setInt(null, (int) (1000 / tickrate));
			
			var gamespeed = Class.forName("net.minecraft.server.MinecraftServer").getField("GAMESPEED");
			gamespeed.setAccessible(true);
			gamespeed.setFloat(null, tickrate / 20.0f);
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			System.err.println("Unable to update server tickrate");
			e.printStackTrace();
		}
		
		// update client tickrate
		Bukkit.getOnlinePlayers().forEach(this::updatePlayer);
	}
	
	/**
	 * Update tickrate for player
	 * @param p
	 */
	private void updatePlayer(Player p) {
		p.sendPluginMessage(plugin, TickrateChanger.TRC_CHANNEL, ByteBuffer.allocate(4).putFloat(this.tickrate).array());
	}
	
	/**
	 * Handle /tickrate command
	 */
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.isOp() || args.length != 1)
			return;

		try {
			// parse and update tickrate
			var tickrate = Float.parseFloat(args[0]);
			this.setTickrate(tickrate);
			sender.sendMessage(Component.text("Updated tickrate to " + tickrate + "."));
		} catch (NumberFormatException e) {
			sender.sendMessage(Component.text("Unable to parse tickrate: " + args[0] + ".", NamedTextColor.RED));
		}
	}

	/**
	 * Schedule verification timer on player join
	 * @param e Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// schedule new 8s kick timer for new player
		this.verificationTimers.put(e.getPlayer().getUniqueId(), new BukkitRunnable() {

			@Override
			public void run() {
				if (e.getPlayer().isOnline())
					e.getPlayer().kick(Component.text("Couldn't connect you to the game server. Please make sure you have installed the TAS Battle client properly.", NamedTextColor.RED));
			}

		}.runTaskLater(this.plugin, 8 * 20));
	}
	
	/**
	 * Cancel verification timer for player on plugin message
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (this.verificationTimers.containsKey(player.getUniqueId()))
			this.verificationTimers.remove(player.getUniqueId()).cancel();

		this.updatePlayer(player);
	}
	
	@Override public String getCommandName() { return "tickrate"; }
	
}
