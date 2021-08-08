package de.pfannekuchen.tasbattle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import de.pfannekuchen.tasbattle.util.BukkitSerialization;
import net.kyori.adventure.text.Component;

public class Game {

	public static boolean isGameRunning;
	
	private static ArrayList<Player> playersInTheGame = new ArrayList<>();
	private static World world;
	
	/**
	 * Starts the Game
	 */
	public static void start() {
		if (isGameRunning) return;
		isGameRunning = true;
		FileUtil.copy(new File(FFA.dir, Configuration.getInstance().currentArena.name), new File(FFA.dir, "game"));
		world = Bukkit.createWorld(WorldCreator.name("game").environment(Environment.NORMAL).generateStructures(false).type(WorldType.FLAT));
		Random r = new Random();
		for (Player player : playersInTheGame) {
			int x = r.nextInt(100) - 50;
			int z = r.nextInt(100) - 50;
			player.teleport(new Location(world, x, world.getHighestBlockAt(x, z).getLocation().getY() + 1, z));
			player.setGameMode(GameMode.SURVIVAL);
			player.setFoodLevel(20);
			player.setExp(0f);
			player.sendMessage(Component.text("§b»§7 The Game started. Kill everyone to win"));
			try {
				BukkitSerialization.playerInventoryToBase64(player, Configuration.getInstance().currentKit.data);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ends the Game
	 */
	public static void stop() {
		if (!isGameRunning) return;
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(new Location(Bukkit.getWorld("lobby"), 0, 100, 0));
			player.setGameMode(GameMode.ADVENTURE);
			player.setFoodLevel(20);
			player.setExp(0f);
			player.sendMessage(Component.text("§b»§7 The Game has ended and " + playersInTheGame.get(0).getName() + " won!"));
			player.getInventory().clear();
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(FFA.PLUGIN, () -> {
			Bukkit.unloadWorld(world, false);
		}, 20L);
		isGameRunning = false;
		playersInTheGame.clear();
		world = null;
	}
	
	/**
	 * Adds a player to the list of players
	 * @param p Player to play
	 */
	public static void join(Player p) {
		if (isGameRunning) return;
		p.sendMessage(Component.text("§b»§a " + p.getName() + " is ready."));
		playersInTheGame.add(p);
		if (playersInTheGame.size() >= Bukkit.getOnlinePlayers().size()) start();
	}
	
	/**
	 * Lets a player spectate a game that is running
	 * @param p Player that will spectate
	 */
	public static void lateJoin(Player p) {
		if (!isGameRunning) return;
		p.sendMessage(Component.text("§b»§a " + p.getName() + " is not spectating."));
		p.setGameMode(GameMode.SPECTATOR);
	}
	
	/**
	 * Eliminates a Player and then removes it updating stats..
	 * @param p Player to eliminate
	 */
	public static void eliminateAndRemovePlayer(Player p) {
		if (!isGameRunning) return;
		if (p.getKiller() == null) Bukkit.broadcast(Component.text("§6» §a" + p.getName() + "§7 died."));
		else Bukkit.broadcast(Component.text("§6» §a" + p.getName() + "§7 was killed by §a" + p.getKiller().getName() + "§7."));
		removePlayer(p);
	}
	
	/**
	 * Removes a Player from the Game and ends it in case only 1 player is remaining
	 * @param p Player to eliminate
	 */
	public static void removePlayer(Player p) {
		if (!isGameRunning) {
			p.sendMessage(Component.text("§b»§c " + p.getName() + " is not ready."));
			playersInTheGame.remove(p);
			return;
		}
		if (!isPlayerPlaying(p)) return;
		playersInTheGame.remove(p);
		p.setGameMode(GameMode.SPECTATOR);
		if (playersInTheGame.size() <= 1) stop(); 
		else p.teleport(new Location(world, 0, 100, 0));
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