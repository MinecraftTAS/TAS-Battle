package com.minecrafttas.tasbattle.ffa.components;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
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

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static com.minecrafttas.tasbattle.managers.GameserverTelemetry.FORMAT;

public class GameLogic implements Listener {

	private static final String SPEC_CHANNEL = "spectatingsystem:data";

	private TASBattleGameserver plugin;
	private World world;
	private List<Player> players;
	@Getter
	private boolean finished;

	/**
	 * Initialize game logic
	 * @param plugin Plugin
	 * @param world World
	 * @param player Participating players
	 */
	public GameLogic(TASBattleGameserver plugin, World world, List<Player> players) {
		var messenger = Bukkit.getMessenger();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		messenger.registerOutgoingPluginChannel(plugin, SPEC_CHANNEL);

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
			this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: %s died\n", FORMAT.format(Date.from(Instant.now())), p.getName()));
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> died</gray>"));
		} else {
			this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: %s killed by %s\n", FORMAT.format(Date.from(Instant.now())), p.getName(), killer.getName()));
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> was slain by <green>" + killer.getName() + "</green></gray>"));
			killer.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.0f);
		}

		// update stats
		var statsManager = this.plugin.getStatsManager();
		statsManager.editStats(stats -> {
			var player = statsManager.getPlayerStats(stats, p.getUniqueId());
			player.setUsername(p.getName());
			player.setDeaths(player.getDeaths() + 1);
			player.setLosses(player.getLosses() + 1);
			this.plugin.getTelemetry().write(String.format("[%s STATS   ]: %s, deaths: %s, losses: %s\n", FORMAT.format(Date.from(Instant.now())), p.getName(), player.getDeaths() + "", player.getLosses() + ""));

			if (killer == null)
				return;

			player = statsManager.getPlayerStats(stats, killer.getUniqueId());
			player.setUsername(killer.getName());
			player.setKills(player.getKills() + 1);
			this.plugin.getTelemetry().write(String.format("[%s STATS   ]: %s, kills: %s\n", FORMAT.format(Date.from(Instant.now())), killer.getName(), player.getKills() + ""));
		});

		// end game on last player
		if (this.players.size() == 1)
			stopGame(this.players.get(0));
	}
	
	/**
	 * Stop the game and declare a winner
	 * @param p Winner
	 */
	public void stopGame(Player p) {
		this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: game end\n", FORMAT.format(Date.from(Instant.now()))));
		this.players.clear();

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
			this.plugin.getTelemetry().write(String.format("[%s SERVER  ]: winner: %s\n", FORMAT.format(Date.from(Instant.now())), p.getName()));
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + p.getName() + "</green> won the game!</gray>"));
			p.showTitle(Title.title(MiniMessage.miniMessage().deserialize("<red>You won!</red>"), Component.empty()));

			// update stats
			var statsManager = this.plugin.getStatsManager();
			statsManager.editStats(stats -> {
				var player = statsManager.getPlayerStats(stats, p.getUniqueId());
				player.setUsername(p.getName());
				player.setWins(player.getWins() + 1);
				try {
					this.plugin.getTelemetry().write(String.format("[%s STATS   ]: %s, wins: %s\n", FORMAT.format(Date.from(Instant.now())), p.getName(), player.getWins() + ""));
					this.plugin.getTelemetry().onShutdown();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} else {
			try {
				this.plugin.getTelemetry().onShutdown();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
		player.sendPluginMessage(this.plugin, SPEC_CHANNEL, new byte[1]);
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
		Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
			p.sendPluginMessage(this.plugin, SPEC_CHANNEL, new byte[1]);
		}, 4L);
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
