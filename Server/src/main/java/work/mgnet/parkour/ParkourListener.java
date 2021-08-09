package work.mgnet.parkour;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class ParkourListener implements Listener {

	public static HashMap<String, Location> checkpoints = new HashMap<>();
	public static ArrayList<Player> done = new ArrayList<>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().getY() < 80 && Tournament.CURRENTGAME == Games.PARKOUR) {
			e.getPlayer().teleport(checkpoints.get(e.getPlayer().getName()));
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (Tournament.CURRENTGAME == Games.PARKOUR) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
				
				@Override
				public void run() {
					e.getEntity().teleport(checkpoints.get(e.getEntity().getName()));
					e.getEntity().setGameMode(GameMode.ADVENTURE);
				}
			}, 20L);
		}
	}
	
	@EventHandler
	public void onCheckpoint(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL && e.hasBlock() && Tournament.CURRENTGAME == Games.PARKOUR) {
			if (e.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
				
				if (checkpoints.containsKey(e.getPlayer().getName())) {
					if (checkpoints.get(e.getPlayer().getName()).getBlock().getLocation().distance(e.getPlayer().getLocation().getBlock().getLocation()) <= 10) return;
					checkpoints.remove(e.getPlayer().getName());
				}
				e.getPlayer().sendMessage(Prefix.PARKOUR + "You hit a checkpoint!");
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
				checkpoints.put(e.getPlayer().getName(), e.getPlayer().getLocation().clone());
			} else if (e.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
				done.add(e.getPlayer());
				if (done.size() == 1) {
					StatisticManager.addPoints(e.getPlayer().getName(), 5);
					e.getPlayer().sendMessage(Prefix.PARKOUR + "You won and got 5 points!");
					Bukkit.broadcast(Component.text(Prefix.PARKOUR + e.getPlayer().getName() + " was first!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 2) {
					StatisticManager.addPoints(e.getPlayer().getName(), 2);
					e.getPlayer().sendMessage(Prefix.PARKOUR + "You finished second and got 2 points!");
					Bukkit.broadcast(Component.text(Prefix.PARKOUR + e.getPlayer().getName() + " was second!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 3) {
					StatisticManager.addPoints(e.getPlayer().getName(), 1);
					e.getPlayer().sendMessage(Prefix.PARKOUR + "You finished third and got 1 points!");
					Bukkit.broadcast(Component.text(Prefix.PARKOUR + e.getPlayer().getName() + " was third!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				}
				
				if (done.size() == Bukkit.getOnlinePlayers().size() || done.size() == 3) {
					Bukkit.broadcast(Component.text(Prefix.PARKOUR + "The Game has ended!"));
					try {
						UtilListener.updateTickrate(20f);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.teleport(player.getWorld().getSpawnLocation());
						player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
						player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
						player.setGameMode(GameMode.ADVENTURE);
						Tournament.CURRENTGAME = Games.NONE;
						done.clear();
						checkpoints.clear();
						player.setHealth(20);
					}
				}
			}
		} 
	}
	
}
