package de.pancake.trc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Tickrate Changer plugin
 * @author Pancake
 */
public class TickrateChanger extends JavaPlugin {

	/**
	 * Default tickrate for new clients
	 * @see TickrateChangerAPI#setTickrate(float)
	 */
	static float TICKRATE = 20.0f;

	/**
	 * Tickrate changer plugin instance
	 */
	static TickrateChanger plugin;

	/**
	 * Tickrate changer plugin channel identifier
	 */
	static final String TRC_C = "tickratechanger:data";

	/**
	 * Player mod detector instance
	 */
	private TickrateChangerDetector detector;

	/**
	 * Initializes the tickrate changer
	 */
	@Override
	public void onEnable() {
		// update plugin singleton
		plugin = this;

		// initialize tickrate changer detector
		this.detector = new TickrateChangerDetector();

		// register plugin channels
		var messenger = Bukkit.getMessenger();
		messenger.registerOutgoingPluginChannel(this, TRC_C);
		messenger.registerIncomingPluginChannel(this, TRC_C, this.detector);

		// register events
		Bukkit.getPluginManager().registerEvents(this.detector, this);
	}

	/**
	 * Implements the /tickrate command
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp() || args.length != 1)
			return true;

		try {
			var tickrate = Float.parseFloat(args[0]);
			TickrateChangerAPI.setTickrate(tickrate);
			sender.sendMessage(Component.text("Updated tickrate to " + tickrate + "."));
		} catch (NumberFormatException e) {
			sender.sendMessage(Component.text("Unable to parse tickrate: " + args[0] + ".", NamedTextColor.RED));
		}

		return true;
	}

}
