package com.minecrafttas.tasbattle.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Timer for launching the game
 * @author Pancake
 */
class LobbyTimer implements CommandExecutor {
	
	private List<Player> players = new ArrayList<>();

	private List<Player> forcestart = new ArrayList<>();

	private BukkitTask task;
	private int startTime, time;
	private int minPlayers, maxPlayers;
	private Consumer<List<Player>> start;

	/**
	 * Create new lobby timer
	 * @param time Time to start counting down from
	 * @param minPlayers Minimal amount of players required
	 * @param max Maximal amount of players before fast start
	 * @param start Game start runnable
	 */
	public LobbyTimer(JavaPlugin plugin, int startTime, int minPlayers, int maxPlayers, Consumer<List<Player>> start) {
		this.startTime = startTime;
		this.time = startTime;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.start = start;

		plugin.getCommand("forcestart").setExecutor(this);

		// Run advance() every second
		this.task = new BukkitRunnable() {
			@Override
			public void run() {
				LobbyTimer.this.advance();
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	/**
	 * Advances the timer
	 */
	private void advance() {
		// Advance timer
		if (this.players.size() >= this.maxPlayers && this.time > 20)
			this.forceStart();
		else if (this.players.size() < this.minPlayers)
			this.time = this.startTime;
		else
			this.time--;

		// Update player levels and play sound
		for (Player p : this.players) {
			p.setLevel(this.time);
			p.setExp(Math.max(0.0f, Math.min(1.0f, this.time / (float) this.startTime)));
			if (this.time < 10)
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, this.time <= 0 ? 1.0f : 0.7f);
		}

		if (this.time <= 0) {
			this.task.cancel();
			this.start.accept(this.players);
		}
	}

	/**
	 * Update countdown to 30 seconds
	 */
	public void forceStart() {
		this.time = 15;
	}

	/**
	 * Remove player from timer
	 * @param p Player
	 */
	public void removePlayer(Player p) {
		this.players.remove(p);
	}

	/**
	 * Add player to timer
	 * @param p Player
	 */
	public void addPlayer(Player p) {
		this.players.add(p);
	}
	
	/**
	 * Is game already running
	 * @return Game running
	 */
	public boolean isGameRunning() {
		return this.task.isCancelled();
	}

	/**
	 * Forcestart timer
	 * @param sender Source of the command
	 * @param command Command which was executed
	 * @param label Alias of the command which was used
	 * @param args Passed command arguments
	 * @return Success
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (this.time < 15)
			return true;

		if (args.length == 1 && args[0].equals("force") && sender.isOp()) {
			Bukkit.broadcast(Component.text("§b» §7Lobby has been force started."));
			this.forceStart();
		} else if (!this.forcestart.contains((Player) sender)) {
			Bukkit.broadcast(Component.text("§b» §a" + sender.getName() + "§7 voted for forcestart."));
			this.forcestart.add((Player) sender);

			if (this.forcestart.size() >= this.players.size()) {
				Bukkit.broadcast(Component.text("§b» §7Lobby has been force started."));
				this.forceStart();
			}
		}

		return true;
	}

}
