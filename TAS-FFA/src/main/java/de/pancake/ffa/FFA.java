package de.pancake.ffa;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.pancake.common.CommonTASBattle;
import de.pancake.ffa.lobby.Lobby;
import net.kyori.adventure.text.Component;

/**
 * FFA plugin
 * @author Pancake
 */
public class FFA extends JavaPlugin {

	/**
	 * FFA plugin instance
	 */
	public static FFA plugin;

	/**
	 * Initializes the ffa plugin into lobby phase
	 */
	@Override
	public void onEnable() {
		// update plugin singleton
		plugin = this;
		CommonTASBattle.registerEvents("LOBBY", new Lobby());
		CommonTASBattle.updatePhase("LOBBY");
	}

	/**
	 * Launches the game and transitions from lobby phase into game phase
	 */
	public static void startGame(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
	}

}
