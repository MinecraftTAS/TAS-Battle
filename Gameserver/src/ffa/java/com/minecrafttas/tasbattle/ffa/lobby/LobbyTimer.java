package com.minecrafttas.tasbattle.ffa.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Timer for launching the game
 * @author Pancake
 */
class LobbyTimer {

	/**
	 * Players in the lobby
	 */
	private List<Player> players = new ArrayList<>();

	/**
	 * Timer task
	 */
	private BukkitTask task;

	/**
	 * Timer starting time
	 */
	private int startTime;

	/**
	 * Current timer time
	 */
	private int time;

	/**
	 * Minimal amount of players required for time to start
	 */
	private int min;

	/**
	 * Maximal amount of players until forcestart
	 */
	private int max;

	/**
	 * Consumer that will be called when the timer hits zero
	 */
	private Consumer<List<Player>> start;

	/**
	 * Creates a new lobby timer and starts it
	 * @param time Time to start counting down from
	 * @param min Minimal amount of players required
	 * @param max Maximal amount of players before fast start
	 * @param start Game start runnable
	 */
	public LobbyTimer(JavaPlugin plugin, int startTime, int min, int max, Consumer<List<Player>> start) {
		this.startTime = startTime;
		this.time = startTime;
		this.min = min;
		this.max = max;
		this.start = start;
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
		if (this.players.size() >= this.max && this.time > 30)
			this.forceStart();
		else if (this.players.size() < this.min)
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
	 * Forcibly updates the countdown to 30 seconds
	 */
	public void forceStart() {
		if (this.task.isCancelled())
			return;

		this.time = 30;
	}

	/**
	 * Removes a player from the timer
	 * @param p Player
	 */
	public void removePlayer(Player p) {
		if (this.task.isCancelled())
			return;

		this.players.remove(p);
	}

	/**
	 * Adds a player to the timer
	 * @param p Player
	 */
	public void addPlayer(Player p) {
		if (this.task.isCancelled())
			return;

		this.players.add(p);
	}

}
