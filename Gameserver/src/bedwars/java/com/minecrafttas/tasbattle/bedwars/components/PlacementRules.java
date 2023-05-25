package com.minecrafttas.tasbattle.bedwars.components;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.minecrafttas.tasbattle.TASBattle;

/**
 * Rules for placing and breaking blocks
 */
public class PlacementRules implements Listener {

	private List<Location> placedBlocks;
	
	/**
	 * Initialize placement rules
	 * @param plugin Plugin
	 */
	public PlacementRules(TASBattle plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.placedBlocks = new ArrayList<>();
	}

	/**
	 * Store player block place
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		this.placedBlocks.add(e.getBlock().getLocation());
	}
	
	/**
	 * Cancel non player placed block break
	 * @param e Event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		var location = e.getBlock().getLocation();
		if (this.placedBlocks.contains(location))
			this.placedBlocks.remove(location);
		else
			e.setCancelled(true);
	}
	
	/**
	 * Check if block was placed by player
	 * @param location Location
	 * @return Is placed by player
	 */
	public boolean isPlacedByPlayer(Location location) {
		return this.placedBlocks.contains(location);
	}
	
}
