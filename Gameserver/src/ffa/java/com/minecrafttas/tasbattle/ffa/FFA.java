package com.minecrafttas.tasbattle.ffa;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractModule;
import com.minecrafttas.tasbattle.ffa.lobby.Lobby;

import net.kyori.adventure.text.Component;

/**
 * FFA plugin
 * @author Pancake
 */
public class FFA extends AbstractModule {

	/**
	 * Launches the game and transitions from lobby phase into game phase
	 */
	public static void startGame(List<Player> players) {
		Bukkit.broadcast(Component.text("we can have fun now... woo"));
	}

	/**
	 * Enable FFA game mode
	 */
	@Override
	public void onEnable(TASBattle plugin) {
		plugin.getModeManagement().registerGameMode("FFA_LOBBY", new Lobby(plugin));
		plugin.getModeManagement().setGameMode("FFA_LOBBY");
	}

	@Override public void onCommand(CommandSender sender, String[] args) { }
	@Override public String getCommandName() { return null; }

}
