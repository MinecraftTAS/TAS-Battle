package work.mgnet.dropper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class DropperListener implements Listener {
	 
	public static ArrayList<Player> done = new ArrayList<>();
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (Tournament.CURRENTGAME == Games.DROPPER) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable()  {
					
				@Override
				public void run() {
					e.getEntity().setGameMode(GameMode.ADVENTURE);
					e.getEntity().teleport(DropperConfiguration.playLoc);
				}
			}, 20L);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFinish(PlayerMoveEvent e) {
		if (Tournament.CURRENTGAME == Games.DROPPER && e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			if (e.getPlayer().getLocation().getBlock().getType() == Material.WATER) {
				Bukkit.broadcastMessage(Prefix.DROPPER + e.getPlayer().getName() + " landed in a pit of water!");
				done.add(e.getPlayer());
				
				if (done.size() == 1) {
					StatisticManager.addPoints(e.getPlayer().getName(), 5);
					e.getPlayer().sendMessage(Prefix.DROPPER + "You won and got 5 points!");
					Bukkit.broadcastMessage(Prefix.DROPPER + e.getPlayer().getName() + " was first!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 2) {
					StatisticManager.addPoints(e.getPlayer().getName(), 2);
					e.getPlayer().sendMessage(Prefix.DROPPER + "You finished second and got 2 points!");
					Bukkit.broadcastMessage(Prefix.DROPPER + e.getPlayer().getName() + " was second!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 3) {
					StatisticManager.addPoints(e.getPlayer().getName(), 1);
					e.getPlayer().sendMessage(Prefix.DROPPER + "You finished third and got 1 points!");
					Bukkit.broadcastMessage(Prefix.DROPPER + e.getPlayer().getName() + " was third!");
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				}
				
				if (done.size() == Bukkit.getOnlinePlayers().size() || done.size() == 3) {
					Bukkit.broadcastMessage(Prefix.DROPPER + "The Game has ended!");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.teleport(player.getWorld().getSpawnLocation());
						player.getWorld().setGameRuleValue("fallDamage", "false");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:stopsound @a");
						player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
						player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
						player.setGameMode(GameMode.ADVENTURE);
						Tournament.CURRENTGAME = Games.NONE;
						done.clear();
						player.setHealth(20);
					}
					try {
						UtilListener.updateTickrate(20f);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
				// Game Over
				
				
			}
		}
	}
	
}
