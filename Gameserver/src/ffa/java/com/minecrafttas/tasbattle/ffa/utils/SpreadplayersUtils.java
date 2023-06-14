package com.minecrafttas.tasbattle.ffa.utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Util for spreading players across a map
 * @author Pancake
 */
public class SpreadplayersUtils {

	/**
	 * Spread players across a given square radius
	 * @param players Players to spread
	 * @param l Base location
	 * @param r Square radius
	 */
	public static void spreadplayers(List<Player> players, Location l, double r) {
    	for (Player p : players) {
    		Block b;
    		do {
    			int x = (int) nextDouble(l.x()-r, l.x()+r);
    			int z = (int) nextDouble(l.z()-r, l.z()+r);
    			b = l.getWorld().getHighestBlockAt(x, z);
    		} while (b.getType() == Material.BARRIER);
    		
    		p.setFallDistance(0.0f);
    		p.teleport(b.getLocation().add(0, 1, 0));
        }
	}
	
    /**
     * Generate a double between a given lower and upper bound
     * @param lower Lower bound
     * @param upper Upper bound
     * @return Random double between lower and upper
     */
    private static double nextDouble(double lower, double upper) {
         return Math.random() * (upper - lower) + lower;
    }
	
}
