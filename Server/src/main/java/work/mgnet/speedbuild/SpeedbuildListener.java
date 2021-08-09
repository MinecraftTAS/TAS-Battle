package work.mgnet.speedbuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import work.mgnet.Games;
import work.mgnet.Tournament;
import work.mgnet.paperextensions.Configuration;
import work.mgnet.statistic.StatisticManager;
import work.mgnet.utils.Prefix;
import work.mgnet.utils.UtilListener;

public class SpeedbuildListener implements Listener {

	public static HashMap<Location, Material> blocksPlaced = new HashMap<>();
	public static HashMap<Location, Material> demo = new HashMap<>();
	
	public static ArrayList<Player> done = new ArrayList<>();
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (Tournament.CURRENTGAME == Games.SPEEDBUILD) synchronized (blocksPlaced) {
			if (!blocksPlaced.containsKey(e.getBlock().getLocation())) blocksPlaced.put(e.getBlock().getLocation(), Material.AIR);
			int points = 0;
			for (Entry<Location, Material> block : demo.entrySet()) {
				if (block.getValue() == block.getKey().clone().add(SpeedbuildConfiguration.pos.get(e.getPlayer()).clone()).getBlock().getType()) {
					points++;
				}
			}
			if (points == demo.size()) {

				done.add(e.getPlayer());
				
				e.getPlayer().sendMessage(Prefix.SPEEDBUILD + "You replicated in " + ((System.currentTimeMillis() - SpeedbuildRun.timeStart) / 1000) + " seconds.");
				
				if (done.size() == 1) {
					StatisticManager.addPoints(e.getPlayer().getName(), 5);
					Bukkit.broadcast(Component.text(Prefix.SPEEDBUILD + e.getPlayer().getName() + " was first and earned 5 points!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 2) {
					StatisticManager.addPoints(e.getPlayer().getName(), 2);
					Bukkit.broadcast(Component.text(Prefix.SPEEDBUILD + e.getPlayer().getName() + " was second and earned 2 points!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				} else if (done.size() == 3) {
					StatisticManager.addPoints(e.getPlayer().getName(), 1);
					Bukkit.broadcast(Component.text(Prefix.SPEEDBUILD + e.getPlayer().getName() + " was third and earned 1 point!"));
					e.getPlayer().setGameMode(GameMode.SPECTATOR);
				}
				if (done.size() == Bukkit.getOnlinePlayers().size() || done.size() == 3) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Configuration.restrictInventory = true;
						Configuration.restrictInteract = true;
						Configuration.restrictBreakAndPlace = true;
						p.getInventory().clear();
						Tournament.CURRENTGAME = Games.NONE;
						p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1.0f, 1f);
						p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0f, 1f);
						done.clear();
						p.setGameMode(GameMode.SPECTATOR);
					}
					try {
						UtilListener.updateTickrate(20f);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
						
						@Override
						public void run() {
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.setGameMode(GameMode.ADVENTURE);
								p.teleport(p.getWorld().getSpawnLocation());
							}
						}
					}, 40L);
				}
				
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && Tournament.CURRENTGAME == Games.SPEEDBUILD && blocksPlaced.containsKey(e.getClickedBlock().getLocation())) {

			e.getPlayer().getInventory().addItem(new ItemStack(e.getClickedBlock().getType()));
			e.getClickedBlock().setType(Material.AIR);
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.2f, 1.0f);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (Tournament.CURRENTGAME == Games.SPEEDBUILD && !blocksPlaced.containsKey(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}
	
}
