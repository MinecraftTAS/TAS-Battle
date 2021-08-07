package de.pfannekuchen.tasbattle;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

public class Game {

	public static boolean isGameRunning;
	
	private static ArrayList<Player> playersInTheGame = new ArrayList<>();
	
	/**
	 * Starts the Game
	 */
	public static void start() {
		if (isGameRunning) return;
	}

	/**
	 * Ends the Game
	 */
	public static void stop() {
		if (!isGameRunning) return;
		playersInTheGame.clear();
	}
	
	/**
	 * Adds a player to the list of players
	 * @param p Player to play
	 */
	public static void join(Player p) {
		if (isGameRunning) return;
		playersInTheGame.add(p);
	}
	
	/**
	 * Lets a player spectate a game that is running
	 * @param p Player that will spectate
	 */
	public static void lateJoin(Player p) {
		if (!isGameRunning) return;
		p.setGameMode(GameMode.SPECTATOR);
	}
	
	/**
	 * Eliminates a Player and then removes it updating stats..
	 * @param p Player to eliminate
	 */
	public static void eliminateAndRemovePlayer(Player p) {
		if (!isGameRunning) return;
		if (p.getKiller() == null) Bukkit.broadcast(Component.text("§6» §a" + p.getName() + " §7 died."));
		else Bukkit.broadcast(Component.text("§6» §a" + p.getName() + " §7 was killed by §a" + p.getKiller().getName() + "§7."));
		removePlayer(p);
	}
	
	/**
	 * Removes a Player from the Game and ends it in case only 1 player is remaining
	 * @param p Player to eliminate
	 */
	public static void removePlayer(Player p) {
		if (!isGameRunning) return;
		if (!isPlayerPlaying(p)) return;
		playersInTheGame.remove(p);
		p.setGameMode(GameMode.SPECTATOR);
		if (playersInTheGame.size() < 1) stop(); 
	}
	
	/**
	 * Returns whether a player is in the current Game
	 * @param entity Player to check
	 * @return Is the player in the list of players
	 */
	public static boolean isPlayerPlaying(@NotNull Player entity) {
		if (!isGameRunning) return false;
		return playersInTheGame.contains(entity);
	}
	
}