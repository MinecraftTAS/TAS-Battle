package work.mgnet.blockrun;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import work.mgnet.Games;
import work.mgnet.Tournament;

public class BlockrunRun {
	
	public static HashMap<Location, Material> blocks = new HashMap<Location, Material>();
	public static Location map = null;
	
	public static void resetBlocks() {
		for (Entry<Location, Material> block : blocks.entrySet()) {
			Location loc = block.getKey();
			loc.setY(100);
			loc.getBlock().setType(block.getValue());
		}
		blocks.clear();
	}
	
	public static Runnable fallingBlocksThread() {
		return new Runnable() {
			
			@Override
			public void run() {
				if (Tournament.CURRENTGAME == Games.BLOCKRUN) {
					for (Player runnerer : Bukkit.getOnlinePlayers()) {
						if (runnerer.getGameMode() == GameMode.ADVENTURE) {
							Location bl = runnerer.getLocation().subtract(0, 1, 0).getBlock().getLocation().clone();
							bl.setY(100);
							if (bl.getBlock().getType() == Material.AIR) continue;
							blocks.put(bl, bl.getBlock().getType());
							Bukkit.getScheduler().runTaskLater(Tournament.self, new Runnable() {
								
								@Override
								public void run() {
									
									Bukkit.getScheduler().runTaskLater(Tournament.self, new Runnable() {
										
										@Override
										public void run() {
											Material lol = bl.getBlock().getType();
											bl.getBlock().breakNaturally();
											runnerer.getWorld().spawnFallingBlock(bl.add(.5, -.26, .5), lol.createBlockData());
										}
									}, 2L);
								}
							}, 5L);
						}
					}
				}
			}
		};
	}
	
}
