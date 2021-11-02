package de.pfannekuchen.ffa;

import java.io.File;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main and basically everything of the FFA Plugin
 * @author Pancake
 */
public class FFA extends JavaPlugin implements PluginMessageListener {

	private static FFA instance;
	public static FFA instance() { return instance; }

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
			Game.onStartup();
			new Thread(() -> {
				
				player.sendPluginMessage(FFA.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a list of kits for every tab completion
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (!sender.isOp()) return null;
		return new ArrayList<>(Game.availableKits.keySet());
	}

	/**
	 * Runs the command for operators
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		try {
			if (!sender.isOp() || (args.length != 1)) return false;
			File kit = new File(getDataFolder(), args[0]);
			if (command.getName().equalsIgnoreCase("savekit")) {
				/* Save the players inventory as a kit */
				byte[][] serializedInventory = Serialization.serializeInventory(((Player) sender).getInventory());
				kit.mkdir();
				Files.write(new File(kit, "inv.dat").toPath(), serializedInventory[0], StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				Files.write(new File(kit, "extra.dat").toPath(), serializedInventory[1], StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				Files.write(new File(kit, "armor.dat").toPath(), serializedInventory[2], StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				sender.sendMessage("\u00A7b\u00bb \u00A77Your kit was successfully saved to \u00A7a\"" + kit.getName() + "\"\u00A77.");
				return true;
			}
			if (!kit.exists()) return false; // Only check for the load commands if the kit was found.
			if (command.getName().equalsIgnoreCase("loadkit")) {
				/* Load a kit into the players inventory */
				byte[][] items = new byte[4][];
				items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
				items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
				items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
				items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
				Serialization.deserializeInventory((Player) sender, items);
				sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully loaded into your Inventory.");
				return true;
			} else if (command.getName().equalsIgnoreCase("kit")) {
				/* Select a kit as active */
				byte[][] items = new byte[4][];
				items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
				items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
				items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
				items[3] = Files.readAllBytes(new File(kit, "icon.dat").toPath());
				Game.serializedSelectedKit = items;
				Game.selectedKitName = kit.getName();
				if (Game.onKitSelectedEvent()) {
					Game.shouldAllowVoting = false;
					sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully selected for the this game");
				} else {
					sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 could not be selected, because there need to be at least 2 people online.");
				}
				return true;
			} else if (command.getName().equalsIgnoreCase("delkit")) {
				for (File f : kit.listFiles()) f.delete();
				if (!kit.delete()) throw new Exception("Could not delete file: " + kit.getName());
				sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully deleted.");
				/* Delete a kit */
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage("\u00A7b\u00bb \u00A7cYour command failed, the stacktrace will be shown in the console.");
			e.printStackTrace();
			return true;
		}
		return false;
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
			player.sendPluginMessage(FFA.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(float1).array());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("tickratechanger:data")) {
			queuedPlayers.remove(player.getUniqueId());
			player.sendPluginMessage(FFA.instance, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
		}
	}

}
