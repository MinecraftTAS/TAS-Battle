package com.minecrafttas.tasbattle.ffa.components;

import java.util.List;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minecrafttas.tasbattle.TASBattleGameserver;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class GameLogic implements Listener {

	private TASBattleGameserver plugin;
	private World world;
	private List<Player> players;
	private boolean finished;

	/**
	 * Initialize game logic
	 * @param plugin Plugin
	 * @param world World
	 * @param player Participating players
	 */
	public GameLogic(TASBattleGameserver plugin, World world, List<Player> players) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.world = world;
		this.players = players;
		
		plugin.getTickrateChanger().setTickrate(4.0f);
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
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> died</gray>"));
		} else {
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> was slain by <green>" + killer.getName() + "</green></gray>"));
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
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> won the game!</gray>"));
			p.showTitle(Title.title(MiniMessage.miniMessage().deserialize("<red>You won!</red>"), Component.empty()));
		}

		// crash server in 16 ticks
		this.finished = true;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> Runtime.getRuntime().halt(0), 16L);
	}
	
	/**
	 * Handle player death (respawn)
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		var p = e.getPlayer();
		p.playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, 0.6f, 1f);
		this.removePlayer(p);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		var respawnLocation = this.world.getSpawnLocation();
		var player = e.getPlayer();
		e.setRespawnLocation(respawnLocation);
		player.setGameMode(GameMode.SPECTATOR);
		this.plugin.getTickrateChanger().updatePlayer(player);
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

		e.joinMessage(null);
	}
	
	/**
	 * Handle player damage (entity damage)
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e instanceof Player player)
			if (!this.players.contains(player))
				e.setCancelled(true);
	}
	
	/**
	 * Handle player disconnect
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		var p = e.getPlayer();

		// early exit server
		if (Bukkit.getOnlinePlayers().size() == 1 && this.finished)
			Runtime.getRuntime().halt(0);

		// eliminate player from game
		if (this.players.contains(p)) {
			this.removePlayer(e.getPlayer());
		}

		e.quitMessage(null);
	}
	
	/**
	 * Handle chest open event (player interact)
	 * @param e Event
	 */
	@EventHandler
	public void onChestOpen(PlayerInteractEvent e) {
		if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST))
			e.setCancelled(true);
	}
	
	/**
	 * Handle chest explode event (entity/block explode)
	 * @param e Event
	 */
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent e) {
		e.blockList().removeIf(c -> (c.getType() == Material.CHEST || c.getType() == Material.TRAPPED_CHEST));
	}
	
	@EventHandler
	public void onEntityExplode(BlockExplodeEvent e) {
		e.blockList().removeIf(c -> (c.getType() == Material.CHEST || c.getType() == Material.TRAPPED_CHEST));
	}
	
}
