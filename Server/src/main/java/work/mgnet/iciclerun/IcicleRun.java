package work.mgnet.iciclerun;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import work.mgnet.Games;
import work.mgnet.Tournament;

public class IcicleRun {

	public static void clearFallingBlocks() {
		for (Location location : IcicleListener.fallingBlocks) {
			location.getBlock().setType(Material.AIR);
		}
		IcicleListener.fallingBlocks.clear();
	}
	
	public static Runnable fallingBlocksThread() {
		return new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (Tournament.CURRENTGAME == Games.ICICLERUN) {
					for (Player runnerer : Bukkit.getOnlinePlayers()) {
						if (runnerer.getMaxHealth() == 1) runnerer.getWorld().spawnFallingBlock(runnerer.getLocation().getBlock().getLocation().add(0.5, 1.5, 0.5), Material.PACKED_ICE.createBlockData());
					}
				}
			}
		};
	}
	
}
