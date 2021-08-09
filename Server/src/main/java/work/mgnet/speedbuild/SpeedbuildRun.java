package work.mgnet.speedbuild;

import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import work.mgnet.Tournament;

public class SpeedbuildRun {
	
	public static long timeStart = 0L;
	
	public static void reset() {
		for (Entry<Location, Material> b : SpeedbuildListener.blocksPlaced.entrySet()) {
			b.getKey().getBlock().setType(b.getValue());
		}
	}
	
	public static void loadBuild() {
		SpeedbuildRun.reset();
		SpeedbuildListener.demo.clear();
		SpeedbuildConfiguration.pos.clear();
		timeStart = System.currentTimeMillis();
		SpeedbuildListener.blocksPlaced.clear();
		for (int i = 0; i < 12; i++) {
			try {Player x = Bukkit.getWorlds().get(0).getPlayers().get(i);
			Location loc = SpeedbuildConfiguration.locs[i].clone();
			
			x.setGameMode(GameMode.ADVENTURE);
			x.getInventory().clear();
			x.setAllowFlight(true);
			x.setFlying(true);
			SpeedbuildConfiguration.pos.put(x, loc);
			x.teleport(loc.clone().add(.5, 0, .5));} catch (Exception e) {
				
			}
			
		}
		int build = new Random().nextInt(40); // CHange Here
		for (Entry<Player, Location> s : SpeedbuildConfiguration.pos.entrySet()) {
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {	 
					for (int z = 0; z < 7; z++) {
						
						Location copyLoc = SpeedbuildConfiguration.copyLocation.clone().add(x + (build * 8), y, z);
						Location pasteLoc = s.getValue().clone().add(x, y, z);
						
						pasteLoc.getBlock().setType(copyLoc.getBlock().getType());
						if (copyLoc.getBlock().getType() != Material.AIR || SpeedbuildListener.demo.containsKey(new Location(Bukkit.getWorlds().get(0), x, y, z))) SpeedbuildListener.demo.put(new Location(Bukkit.getWorlds().get(0), x, y, z), copyLoc.getBlock().getType());
						s.getKey().getInventory().addItem(new ItemStack(pasteLoc.getBlock().getType()));
					}
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.self, new Runnable() {
			
			@Override
			public void run() {
				for (Entry<Player, Location> s : SpeedbuildConfiguration.pos.entrySet()) {
					for (int x = 0; x < 7; x++) {
						for (int y = 0; y < 7; y++) {	 
							for (int z = 0; z < 7; z++) {
								Location pasteLoc = s.getValue().clone().add(x, y, z);
								pasteLoc.getBlock().setType(Material.AIR);
							}
						}
					}
				}
				for (int i = 0; i < 12; i++) {
					try {Player p = Bukkit.getWorlds().get(0).getPlayers().get(i);
						p.setGameMode(GameMode.SURVIVAL);
						p.setAllowFlight(true);
						p.setFlying(true);
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
					} catch (Exception e) {
						
					}
				}
			}
		}, 200L);
	}
	
}
