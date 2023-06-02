package com.minecrafttas.tasbattle.bedwars.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.minecrafttas.tasbattle.TASBattle;

/**
 * Inventory management for controlling items in inventory
 */
public class InventoryManagement implements Listener {

	private static final List<Material> UNDROPPABLE_ITEMS = Arrays.asList(
		Material.WOODEN_SWORD,
		Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE,
		Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE,
		Material.SHEARS,
		Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
		Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
		Material.IRON_LEGGINGS, Material.IRON_BOOTS,
		Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS
	);
	
	private static final List<Material> UNCLICKABLE_ITEMS = Arrays.asList(
		Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
		Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
		Material.IRON_LEGGINGS, Material.IRON_BOOTS,
		Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS
	);
	
	private List<Player> players;
	private Map<Player, Integer> pickaxeTiers;
	private Map<Player, Integer> axeTiers;
	private Map<Player, Integer> shearsTiers;	
	private Map<Player, Integer> armorTiers;
	
	/**
	 * Initialize inventory management
	 * @param plugin Plugin
	 * @param players Players
	 */
	public InventoryManagement(TASBattle plugin, List<Player> players) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.players = players;
		this.pickaxeTiers = new HashMap<>();
		this.axeTiers = new HashMap<>();
		this.shearsTiers = new HashMap<>();
		this.armorTiers = new HashMap<>();
	}
	
	/**
	 * Update player inventory on tick
	 * @param e Event
	 */
	@EventHandler
	public void onTick(ServerTickStartEvent e) {
		// verify player inventories
		for (var p : this.players) {
			var inv = p.getInventory();
			
			// verify at least one sword in inventory
			if (!inv.contains(Material.WOODEN_SWORD) && !inv.contains(Material.STONE_SWORD) && !inv.contains(Material.IRON_SWORD) && !inv.contains(Material.DIAMOND_SWORD))
				inv.addItem(new ItemStack(Material.WOODEN_SWORD));
			
			// verify no more than one wooden sword in inventory
			if (inv.all(Material.WOODEN_SWORD).size() > 1)
				inv.remove(Material.WOODEN_SWORD);
				
			// verify the correct tier of armor is in the inventory
			var bootsType = inv.getBoots() == null ? Material.AIR : inv.getBoots().getType();
			var leggingsType = inv.getLeggings() == null ? Material.AIR : inv.getLeggings().getType();
			if (!(bootsType == Material.LEATHER_BOOTS && leggingsType == Material.LEATHER_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) == -1) {
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			} else if (!(bootsType == Material.CHAINMAIL_BOOTS && leggingsType == Material.CHAINMAIL_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) == 0) {
				inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
				inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			} else if (!(bootsType == Material.IRON_BOOTS && leggingsType == Material.IRON_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) == 1) {
				inv.setBoots(new ItemStack(Material.IRON_BOOTS));
				inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			} else if (!(bootsType == Material.DIAMOND_BOOTS && leggingsType == Material.DIAMOND_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) == 2) {
				inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
				inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			}
			
			// verify the correct tier of pickaxe is in the inventory
			if (!inv.contains(Material.WOODEN_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.WOODEN_PICKAXE));
			else if (!inv.contains(Material.STONE_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 1)
				inv.addItem(new ItemStack(Material.STONE_PICKAXE));
			else if (!inv.contains(Material.IRON_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 2)
				inv.addItem(new ItemStack(Material.IRON_PICKAXE));
			else if (!inv.contains(Material.DIAMOND_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 3)
				inv.addItem(new ItemStack(Material.DIAMOND_PICKAXE));
			
			// verify the correct tier of axe is in the inventory
			if (!inv.contains(Material.WOODEN_AXE) && this.axeTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.WOODEN_AXE));
			else if (!inv.contains(Material.STONE_AXE) && this.axeTiers.getOrDefault(p, -1) == 1)
				inv.addItem(new ItemStack(Material.STONE_AXE));
			else if (!inv.contains(Material.IRON_AXE) && this.axeTiers.getOrDefault(p, -1) == 2)
				inv.addItem(new ItemStack(Material.IRON_AXE));
			else if (!inv.contains(Material.DIAMOND_AXE) && this.axeTiers.getOrDefault(p, -1) == 3)
				inv.addItem(new ItemStack(Material.DIAMOND_AXE));
			
			// verify the correct tier of shears are in the inventory
			if (!inv.contains(Material.SHEARS) && this.shearsTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.SHEARS));
		}
	}
	
	/**
	 * Reduce player item tier on death
	 * @param e Event
	 */
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		var p = e.getPlayer();
		if (this.pickaxeTiers.containsKey(p))
			this.pickaxeTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
		
		if (this.axeTiers.containsKey(p))
			this.axeTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
		
		if (this.shearsTiers.containsKey(p))
			this.shearsTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
	}
	
	/**
	 * Cancel undroppable item drops
	 * @param e Event
	 */
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (UNDROPPABLE_ITEMS.contains(e.getItemDrop().getItemStack().getType()))
			e.setCancelled(true);
	}
	
	/**
	 * Cancel unclickable item clicks
	 * @param e Event
	 */
	@EventHandler
	public void onDrop(InventoryClickEvent e) {
		var item = e.getCurrentItem();
		if (item == null)
			return;
		
		if (UNCLICKABLE_ITEMS.contains(item.getType()))
			e.setCancelled(true);
	}
	
	// get tier methods
	public int getPickaxeTier(Player p) { return this.pickaxeTiers.getOrDefault(p, -1); }
	public int getAxeTier(Player p) { return this.axeTiers.getOrDefault(p, -1); }
	public int getShearsTier(Player p) { return this.shearsTiers.getOrDefault(p, -1); }
	public int getArmorTier(Player p) { return this.armorTiers.getOrDefault(p, -1); }
	
	// set tier methods
	public boolean increasePickaxeTier(Player p) { this.pickaxeTiers.put(p, Math.min(3, this.pickaxeTiers.getOrDefault(p, -1) + 1)); return true; }
	public boolean increaseAxeTier(Player p) { this.axeTiers.put(p, Math.min(3, this.axeTiers.getOrDefault(p, -1) + 1)); return true; }
	public boolean increaseShearsTier(Player p) { this.shearsTiers.put(p, 0); return true; }
	public void setArmorTier(Player p, int tier) { this.armorTiers.put(p, tier); }
	
}
