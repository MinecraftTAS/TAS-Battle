package work.mgnet.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.dropper.DropperListener;
import work.mgnet.duel.DuelRun;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.replika.ReplicaListener;

@SuppressWarnings("deprecation")
public class UtilListener implements Listener, PluginMessageListener {

	public static File saved_tickrate;
	public static float tickrate = 20;

	public static volatile List<UUID> queuedPlayers = new ArrayList<>();
	public static boolean enablePVP = false;
	
	public static void updateTickrate(float float1) throws Exception {
		tickrate = float1;
		Field f = Class.forName("net.minecraft.server.MinecraftServer").getDeclaredField("tickrateServer");
		f.setAccessible(true);
		f.setFloat(null, float1);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendPluginMessage(Tournament.self, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(float1).array());
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase("tickratechanger:data")) {
			queuedPlayers.remove(player.getUniqueId());
			player.sendPluginMessage(Tournament.self, "tickratechanger:data", ByteBuffer.allocate(4).putFloat(tickrate).array());
		}
	}
	
	@EventHandler
	public void onRecipe(PlayerRecipeDiscoverEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onLateJoin(PlayerJoinEvent e) {
		queuedPlayers.add(e.getPlayer().getUniqueId());
		new Thread(() -> {
			try {
				Thread.sleep(2500);
			} catch (Exception e2) {
				
			}
			if (queuedPlayers.contains(e.getPlayer().getUniqueId()) && e.getPlayer().isOnline()) {
				queuedPlayers.remove(e.getPlayer().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getPlayer().kick(Component.text("Login Failed."));
					}
				}.runTask(Tournament.self);
			}
		}).start();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.getPlayer().setHealth(0.0);
		if (Tournament.CURRENTGAME == Games.DROPPER) {
			
			if (DropperListener.done.size() == Bukkit.getOnlinePlayers().size() - 1 || DropperListener.done.size() == 3) {
				Bukkit.broadcastMessage(Prefix.DROPPER + "The Game has ended!");
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.teleport(player.getWorld().getSpawnLocation());
					player.getWorld().setGameRuleValue("fallDamage", "false");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:stopsound @a");
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
					player.setGameMode(GameMode.ADVENTURE);
					Tournament.CURRENTGAME = Games.NONE;
					DropperListener.done.clear();
					try {
						UtilListener.updateTickrate(20f);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					player.setHealth(20);
				}
			}
			
		} else if (Tournament.CURRENTGAME == Games.REPLICA) {
			if (ReplicaListener.done.size() == Bukkit.getOnlinePlayers().size() - 1 || ReplicaListener.done.size() == 3) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (Player p2 : Bukkit.getOnlinePlayers()) {
						p.showPlayer(p2);
						p2.showPlayer(p);
						
					}
					Configuration.restrictInventory = true;
					Configuration.restrictInteract = true;
					p.setAllowFlight(false);
					p.getInventory().clear();
					Tournament.CURRENTGAME = Games.NONE;
					p.teleport(p.getWorld().getSpawnLocation());
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
					p.resetMaxHealth();
					p.setHealth(20.0);
					ReplicaListener.done.clear();
					p.setGameMode(GameMode.ADVENTURE);
				}
			}
		}
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getPlayer().getLocation().getBlockY() < 0) {
			if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
				e.getPlayer().teleport(e.getPlayer().getLocation().add(0, 100, 0));
			} else {
				e.getPlayer().damage(100);
			}
			
		}
	}
	
	@EventHandler
	public void onJoin(PlayerPreLoginEvent e) {
		if (Tournament.isTournament) e.disallow(Result.KICK_OTHER, "§8There is currently a Tournament running");
		if (Tournament.CURRENTGAME != Games.NONE) {
			e.disallow(Result.KICK_OTHER, "§8There is currently a game running, the players have been notified.");
			Bukkit.broadcast(Component.text("§a" + e.getName() + " would like to join."));
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)  {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				e.getPlayer().setGameMode(GameMode.ADVENTURE);
				e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
			}
		}, 25L);
		try {
			e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		} catch (Exception e2) {
			
		}
	}
	
	@EventHandler
	public void pvpEvent(EntityDamageByEntityEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			e.setCancelled(!enablePVP);
			try {
				if (e.getEntity().getName().equalsIgnoreCase(DuelRun.pvp1) || e.getEntity().getName().equalsIgnoreCase(DuelRun.pvp2)) e.setCancelled(false);
			} catch (Exception e2) {
				
			}
		}
	}
	
}
