	package de.pfannekuchen.ffa;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main and basically everything of the FFA Plugin
 * @author Pancake
 */
public class FFA extends JavaPlugin {

	/** The currently selected kit */
	public static byte[][] serializedSelectedKit;
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
		super.onEnable();
	}
	
	/**
	 * Returns a list of kits for every tab completion
	 */
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (!sender.isOp()) return null;
		return super.onTabComplete(sender, command, alias, args);
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
				sender.sendMessage("\u00A7b\u00bb \u00A77The kit \u00A7a\"" + kit.getName() + "\"\u00A77 was successfully marked as active.");
				selectedKitName = kit.getName();
				Events.instance().onKitSelectedEvent();
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
