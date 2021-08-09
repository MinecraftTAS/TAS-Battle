package work.mgnet.paperextensions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import work.mgnet.paperextensions.Configuration;

public class RestrictionListener implements Listener {
	
	@EventHandler
	public void restrictCommands(PlayerCommandPreprocessEvent e) {
		if (!e.getPlayer().hasPermission("essentials.skip")) {
			
			if (Configuration.blockDD && (e.getMessage().contains(" ") ? e.getMessage().split(" ")[0].contains(":") : e.getMessage().contains(":"))) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§9TAS§6Battle§9 » §cCommand not found");
			} else if (Configuration.blockedCMDs.contains(e.getMessage().contains(" ") ? e.getMessage().split(" ")[0].replaceAll("/", "") : e.getMessage().replaceAll("/", ""))) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§9TAS§6Battle§9 » §cCommand not found");
			}
			
		}
		if (Configuration.forceblockedCMDs.contains(e.getMessage().contains(" ") ? e.getMessage().split(" ")[0].replaceAll("/", "") : e.getMessage().replaceAll("/", ""))) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§9TAS§6Battle§9 » §cCommand not found");
		}
	}
	
	
	@EventHandler
	public void restrictInteract(HangingBreakByEntityEvent e) {
		if (Configuration.restrictInteract && !e.getEntity().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictInteract(PlayerInteractEvent e) {
		if (Configuration.restrictInteract && !e.getPlayer().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictPlace(BlockPlaceEvent e) {
		if (Configuration.restrictBreakAndPlace && !e.getPlayer().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictPlace(BlockBreakEvent e) {
		if (Configuration.restrictBreakAndPlace && !e.getPlayer().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictCrafting(CraftItemEvent e) {
		if (Configuration.restrictCrafting && !e.getWhoClicked().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictDropping(PlayerDropItemEvent e) {
		if (Configuration.restrictDrop && !e.getPlayer().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictCrafting(PlayerSwapHandItemsEvent e) {
		if (Configuration.restrictOffhand && !e.getPlayer().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void restrictInventory(InventoryClickEvent e) {
		if (Configuration.restrictInventory && !e.getWhoClicked().hasPermission("essentials.bypass")) {
			e.setCancelled(true);
		}
	}
	
}
