package com.minecrafttas.tasbattle.bedwars.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * Mechanics of different items such as tnt, swords and more
 */
public class ItemPhysics {

	private static final List<Material> UNDROPPABLE_ITEMS = Arrays.asList(
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

	private List<Location> blocks;
	private Map<Player, Integer> pickaxeTiers;
	private Map<Player, Integer> axeTiers;
	private Map<Player, Integer> shearsTiers;	
	private Map<Player, Integer> armorTiers;
	private List<Player> players;
	private World world;
	
	/**
	 * Initialize item physics
	 * @param world Game world
	 */
	public ItemPhysics(World world) {
		this.world = world;
		
		this.blocks = new ArrayList<>();
		this.pickaxeTiers = new HashMap<>();
		this.axeTiers = new HashMap<>();
		this.shearsTiers = new HashMap<>();
		this.armorTiers = new HashMap<>();
	}
	
	/**
	 * Start item physics
	 * @param players Item physics
	 */
	public void startGame(List<Player> players) {
		this.players = players;
	}
	
	/**
	 * Tick item physics
	 * @param players Game participants
	 * @param w Game world
	 */
	public void tick() {
		// update every player
		for (var p : this.players) {
			var inv = p.getInventory();
			
			// verify at least one sword is in the inventory
			if (!inv.contains(Material.WOODEN_SWORD) && !inv.contains(Material.STONE_SWORD) && !inv.contains(Material.IRON_SWORD) && !inv.contains(Material.DIAMOND_SWORD))
				inv.addItem(new ItemStack(Material.WOODEN_SWORD));
			
			// verify the correct tier of armor is in the inventory
			var bootsType = inv.getBoots() == null ? Material.AIR : inv.getBoots().getType();
			var leggingsType = inv.getLeggings() == null ? Material.AIR : inv.getLeggings().getType();
			if (!(bootsType == Material.LEATHER_BOOTS && leggingsType == Material.LEATHER_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) == -1) {
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			} else if (!(bootsType == Material.CHAINMAIL_BOOTS && leggingsType == Material.CHAINMAIL_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) != 0) {
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			} else if (!(bootsType == Material.IRON_BOOTS && leggingsType == Material.IRON_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) != 1) {
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			} else if (!(bootsType == Material.DIAMOND_BOOTS && leggingsType == Material.DIAMOND_LEGGINGS) && this.armorTiers.getOrDefault(p, -1) != 2) {
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			}
			
			// verify the correct tier of pickaxe is in the inventory
			if (!inv.contains(Material.WOODEN_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == -1)
				inv.addItem(new ItemStack(Material.WOODEN_PICKAXE));
			else if (!inv.contains(Material.STONE_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.STONE_PICKAXE));
			else if (!inv.contains(Material.IRON_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 1)
				inv.addItem(new ItemStack(Material.IRON_PICKAXE));
			else if (!inv.contains(Material.DIAMOND_PICKAXE) && this.pickaxeTiers.getOrDefault(p, -1) == 2)
				inv.addItem(new ItemStack(Material.DIAMOND_PICKAXE));
			
			// verify the correct tier of axe is in the inventory
			if (!inv.contains(Material.WOODEN_AXE) && this.axeTiers.getOrDefault(p, -1) == -1)
				inv.addItem(new ItemStack(Material.WOODEN_AXE));
			else if (!inv.contains(Material.STONE_AXE) && this.axeTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.STONE_AXE));
			else if (!inv.contains(Material.IRON_AXE) && this.axeTiers.getOrDefault(p, -1) == 1)
				inv.addItem(new ItemStack(Material.IRON_AXE));
			else if (!inv.contains(Material.DIAMOND_AXE) && this.axeTiers.getOrDefault(p, -1) == 2)
				inv.addItem(new ItemStack(Material.DIAMOND_AXE));
			
			// verify the correct tier of shears are in the inventory
			if (!inv.contains(Material.SHEARS) && this.shearsTiers.getOrDefault(p, -1) == 0)
				inv.addItem(new ItemStack(Material.SHEARS));
		}
		
		// update every villager to face player
		for (var v : this.world.getEntitiesByClass(Villager.class)) {
			for (var p : this.world.getNearbyEntitiesByType(Player.class, v.getLocation(), 10.0)) {
				var offsetVec = v.getLocation().subtract(p.getLocation());
				var eyeVec = v.getEyeLocation().setDirection(offsetVec.toVector());
				v.setRotation(180.0f + eyeVec.getYaw(), -eyeVec.getPitch());
			}
		}
	}
	
	public int getPickaxeTier(Player p) { return this.pickaxeTiers.getOrDefault(p, -1); }
	public int getAxeTier(Player p) { return this.axeTiers.getOrDefault(p, -1); }
	public int getShearsTier(Player p) { return this.shearsTiers.getOrDefault(p, -1); }
	public int getArmorTier(Player p) { return this.armorTiers.getOrDefault(p, -1); }
	public void increasePickaxeTier(Player p) { this.pickaxeTiers.put(p, Math.min(3, this.pickaxeTiers.getOrDefault(p, -1) + 1)); }
	public void increaseAxeTier(Player p) { this.axeTiers.put(p, Math.min(3, this.axeTiers.getOrDefault(p, -1) + 1)); }
	public void increaseShearsTier(Player p) { this.shearsTiers.put(p, 0); }
	public void setArmorTier(Player p, int tier) { this.armorTiers.put(p, tier); }
	
	/**
	 * Notify block place and instantly explode tnt
	 * @param l Block location
	 * @param b Block
	 */
	public void onBlockPlace(Block b) {
		this.blocks.add(b.getLocation());
		if (b.getType() == Material.TNT) {
			b.setType(Material.AIR);
			
			b.getWorld().spawn(b.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class).setFuseTicks(50);
		}
			
	}
	
	/**
	 * Should block break be cancelled
	 * @param l Block location
	 * @return Should cancel
	 */
	public boolean onBlockBreak(Location l) {
		return !this.blocks.remove(l);
	}
	
	/**
	 * Update player tier on death
	 * @param p Player
	 */
	public void onDeath(Player p) {
		if (this.pickaxeTiers.containsKey(p))
			this.pickaxeTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
		
		if (this.axeTiers.containsKey(p))
			this.axeTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
		
		if (this.shearsTiers.containsKey(p))
			this.shearsTiers.put(p, Math.max(0, this.pickaxeTiers.get(p) - 1));
	}
	
	/**
	 * Throw fireballs on right click interaction
	 * @param p Player
	 * @param i Item
	 * @return Should cancel event
	 */
	public boolean onRightClickInteract(Player p, ItemStack i) {
		if (i == null)
			return false;
		
		if (i.getType() != Material.FIRE_CHARGE)
			return false;
		
		if (i.getAmount() == 1)
			p.getInventory().remove(i);
		else
			i.setAmount(i.getAmount() - 1);
		
		var f = p.launchProjectile(Fireball.class, p.getLocation().getDirection());
		f.setInvulnerable(true);
		f.setYield(f.getYield() * 2.0f);
		
		return true;
	}
	
	/**
	 * Notify and update block break list from explosion
	 * @param type Type of explosion entity
	 * @param blocklist List of affected blocks
	 */
	public void onTntExplode(EntityType type, List<Block> blocklist) {
		for (Block block : new ArrayList<>(blocklist))
			if (!this.blocks.contains(block.getLocation()) || (type == EntityType.FIREBALL && block.getType() == Material.END_STONE))
				blocklist.remove(block);
	}
	
	/**
	 * Update tnt damage
	 * @param cause Cause of damage
	 * @param damage Damage taken
	 * @return New damage
	 */
	public double onTntDamage(DamageCause cause, double damage) {
		if (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.BLOCK_EXPLOSION)
			return Math.min(damage, 4);

		return damage;
	}
	
	/**
	 * Should cancel item drop
	 * @param i Item dropped
	 * @return Should cancel
	 */
	public boolean onDrop(ItemStack i) {
		return UNDROPPABLE_ITEMS.contains(i.getType());
	}
	
	/**
	 * Should cancel inventory interaction
	 * @param i Item Stack
	 * @return Should cancel
	 */
	public boolean onClick(ItemStack i) {
		return UNCLICKABLE_ITEMS.contains(i.getType());
	}

	// also double check explosion velocity, damage and fireball throw angle
	
}
