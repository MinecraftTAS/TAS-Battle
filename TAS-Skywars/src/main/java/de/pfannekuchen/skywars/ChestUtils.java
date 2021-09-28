package de.pfannekuchen.skywars;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ChestUtils implements Listener {

	private static ArrayList<Location> locations = new ArrayList<>();
	
	@EventHandler
	public void onChestOpen(InventoryOpenEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		if (holder != null) {
			if (holder instanceof Chest) {
				if (!locations.contains(((Chest) holder).getLocation())) {
					locations.add(((Chest) holder).getLocation());
					ItemStack[] items = Game.refillChest();
					Random rng = new Random();
					for (int i = 0; i < items.length; i++) {
						((Chest) holder).getBlockInventory().setItem(rng.nextInt(27), items[i]);
					}
					e.setCancelled(true);
					e.getPlayer().openInventory(holder.getInventory());
				}
			}
		}
	}
	
}
