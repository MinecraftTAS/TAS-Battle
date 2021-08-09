package work.mgnet.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import net.kyori.adventure.text.Component;

public class BlockUtils {

	public static void changeSign(Block block, String line1, String line2, String line3, String line4) {
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			
			sign.line(0, Component.text(line1));
			sign.line(1, Component.text(line2));
			sign.line(2, Component.text(line3));
			sign.line(3, Component.text(line4));
			
			sign.update();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void editHead(Block block, String owner) {
		if (owner.equalsIgnoreCase("X")) block.setType(Material.SKELETON_WALL_SKULL);
		else block.setType(Material.PLAYER_WALL_HEAD);
		try {
			Skull head = (Skull) block.getState();
			head.setRotation(BlockFace.NORTH);
			
			head.setOwner(owner);
		
			head.update();
		}
		catch (Exception e) {
			
		}
	}
	
}
