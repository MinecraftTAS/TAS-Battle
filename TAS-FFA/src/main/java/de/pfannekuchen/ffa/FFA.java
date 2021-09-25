	package de.pfannekuchen.ffa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;

/**
 * Main and basically everything of the FFA Plugin
 * @author Pancake
 */
public class FFA extends JavaPlugin {

	/** The currently selected kit */
	public static byte[][] serializedSelectedKit;
	/** All available kits */
	public static HashMap<String, byte[][]> availableKits = new HashMap<>(); 
	/** The currently selected kit name */
	public static String selectedKitName;
	
	private static FFA instance;
	public static FFA instance() { return instance; }
	
	/**
	 * Creates the Data Folder and registers the events on initialization
	 */
	@Override
	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		/* Read available Kits */
		for (File folder : getDataFolder().listFiles()) {
			try {
				if (folder.isDirectory()) {
					File kit = new File(getDataFolder(), folder.getName());
					byte[][] items = new byte[3][];
					items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
					items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
					items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
					availableKits.put(folder.getName(), items);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/* Start a Thread */
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) if (!Events.isRunning) p.sendActionBar(Component.text("\u00A7cLC to view a kit. RC to vote a kit."));	
			}
		}.runTaskTimer(this, 0L, 20L);
		super.onEnable();
	}
	
	/**
	 * Returns a list of kits for every tab completion
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (!sender.isOp()) return null;
		return new ArrayList<String>(availableKits.keySet());
	}

	/**
	 * Runs the command for operators
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		try {
			if (!sender.isOp()) return false;
			if (args.length != 1) return false;
			File kit = new File(getDataFolder(), args[0]);
			if (command.getName().equalsIgnoreCase("savekit")) {
				/* Save the players inventory as a kit */
				byte[][] serializedInventory = Serialization.playerInventoryToBase64(((Player) sender).getInventory());
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
				byte[][] items = new byte[3][];
				items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
				items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
				items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
				Serialization.base64ToPlayerInventory((Player) sender, items);
				sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully loaded into your Inventory.");
				return true;
			} else if (command.getName().equalsIgnoreCase("kit")) {
				/* Select a kit as active */
				byte[][] items = new byte[3][];
				items[0] = Files.readAllBytes(new File(kit, "inv.dat").toPath());
				items[1] = Files.readAllBytes(new File(kit, "extra.dat").toPath());
				items[2] = Files.readAllBytes(new File(kit, "armor.dat").toPath());
				serializedSelectedKit = items;
				selectedKitName = kit.getName();
				if (Events.instance().onKitSelectedEvent()) {
					Events.shouldAllowVoting = false;
					sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully selected for the this game");
				} else {
					sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 could not be selected, because there need to be at least 2 people online.");
				}
				return true;
			} else if (command.getName().equalsIgnoreCase("delkit")) {
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
	
}
