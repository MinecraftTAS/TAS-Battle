package com.minecrafttas.tasbattle.ffa.components;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minecrafttas.tasbattle.TASBattle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class GameLogic implements Listener {

	private TASBattle plugin;
	private World world;
	private List<Player> players;
	
	/**
	 * Initialize game logic
	 * @param plugin Plugin
	 * @param world World
	 * @param player Participating players
	 */
	public GameLogic(TASBattle plugin, World world, List<Player> players) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.world = world;
		this.players = players;
	}

	/**
	 * Remove player from game
	 * @param p Player
	 */
	public void removePlayer(Player p) {
		// remove player from game
		this.players.remove(p);
		
		// print death message
		var killer = p.getKiller();
		if (killer == null || killer == p) {
			Bukkit.broadcast(Component.text("§b» §a" + p.getName() + "§7 died"));
		} else {
			Bukkit.broadcast(Component.text("§b» §a" + p.getName() + "§7 was slain by §a" + killer.getName()));
			killer.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.0f);
		}
		
		// end game on last player
		if (this.players.size() == 1) {
			stopGame(this.players.get(0));
		}
	}
	
	/**
	 * Stop the game and declare a winner
	 * @param p Winner
	 */
	public void stopGame(Player p) {
		// decrease tickrate
		this.plugin.getTickrateChanger().setTickrate(1.0f);
		
		// play win sounds
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, SoundCategory.PLAYERS, 1f, (float) (Math.random() * 0.5f + 1f));
			player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, SoundCategory.PLAYERS, 1f, (float) (Math.random() * 0.5f + 1f));
			player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, SoundCategory.PLAYERS, 1f, (float) (Math.random() * 0.5f + 1f));
		}
		
		// announce winner
		if (p != null) {
			Bukkit.broadcast(Component.text("§b» §a" + p.getName() + "§7 won the game!"));
			p.showTitle(Title.title(Component.text("§cYou won!"), Component.empty()));
		}
		
		// crash server in 16 ticks
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Runtime.getRuntime().halt(0), 16L);
	}
	
	/**
	 * Handle player death (respawn)
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDeath(PlayerRespawnEvent e) {
		var respawnLocation = this.world.getSpawnLocation();
		var p = e.getPlayer();
		
		// update respawn location and play sound
		e.setRespawnLocation(respawnLocation);
		p.playSound(respawnLocation, Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, 0.6f, 1f);
		
		this.removePlayer(p);
	}
	
	/**
	 * Handle late player join
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent e) {
		var p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		p.teleport(this.world.getSpawnLocation());
		
		e.joinMessage(Component.text("§b» §a" + p.getName() + " §7started spectating"));
	}
	
	/**
	 * Handle player damage (entity damage)
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!this.players.contains(e.getEntity()))
			e.setCancelled(true);
	}
	
	/**
	 * Handle player disconnect
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		var p = e.getPlayer();
		
		if (this.players.contains(p)) {
			this.removePlayer(e.getPlayer());
			e.quitMessage(null);
		} else {
			e.quitMessage(Component.text("§b» §a" + p.getName() + " §7stopped spectating"));
		}
	}
	
}
