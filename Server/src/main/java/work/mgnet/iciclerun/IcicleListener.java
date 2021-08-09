package work.mgnet.iciclerun;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.blockrun.BlockrunRun;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class IcicleListener implements Listener {

	public static ArrayList<Location> fallingBlocks = new ArrayList<Location>();
	
	public static Location map;
	
	@EventHandler
	public void onBlock(EntityChangeBlockEvent e) {
		if (e.getEntity().isOnGround()) {
			fallingBlocks.add(e.getEntity().getLocation());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		e.setDeathSound(Sound.ENTITY_ENDERMAN_DEATH);
		e.setShouldPlayDeathSound(true);
		if (Tournament.CURRENTGAME == Games.ICICLERUN) {
			Bukkit.broadcastMessage(Prefix.ICICLERUN + IcicleConfiguration.cubeDeathMessages.get(new Random().nextInt(IcicleConfiguration.cubeDeathMessages.size())).replaceAll("%", e.getEntity().getName()));
			ArrayList<Player> iciclers = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getGameMode() == GameMode.ADVENTURE) iciclers.add(player);
			}
			iciclers.remove(e.getEntity());
			e.getEntity().sendMessage(Prefix.ICICLERUN + "§6You earned " + ((3 - iciclers.size()) > 0 ? (3 - iciclers.size()) : 0) + " points for your parkouring!");
			StatisticManager.addPoints(e.getEntity().getName(), (3 - iciclers.size()) > 0 ? (3 - iciclers.size()) : 0);
			if (iciclers.size() == 1) {
				try {
					UtilListener.updateTickrate(20f);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				Tournament.CURRENTGAME = Games.NONE;
				Bukkit.broadcastMessage(Prefix.ICICLERUN + "§6% was the coolest, and earned 5 points!".replaceAll("%", iciclers.get(0).getName()));
				StatisticManager.addPoints(iciclers.get(0).getName(), 5);
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
					
					p.resetMaxHealth();
					p.setHealth(20.0);
					
					p.setGameMode(GameMode.SPECTATOR);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
					
					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							
							IcicleRun.clearFallingBlocks();
							
							p.teleport(p.getWorld().getSpawnLocation());

							p.resetMaxHealth();
							p.setHealth(20.0);
							
							p.setGameMode(GameMode.ADVENTURE);
						}
					}
				}, 100L);
			}
		}
	}
	
	@EventHandler
	public void onPreRespawn(PlayerRespawnEvent e) {
		if (Tournament.CURRENTGAME == Games.BLOCKRUN) {
			e.setRespawnLocation(BlockrunRun.map.clone());
		} else if (Tournament.CURRENTGAME == Games.SPLEEF) {
			e.setRespawnLocation(map);
		} else e.setRespawnLocation(e.getPlayer().getLocation());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent e) {
		e.getPlayer().resetMaxHealth();
		e.getPlayer().setGameMode(GameMode.SPECTATOR);
	}
	
}
