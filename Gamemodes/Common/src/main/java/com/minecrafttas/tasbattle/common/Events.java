package com.minecrafttas.tasbattle.common;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This interface provides all events for developing a minigame phase.
 * @see CommonTASBattle#registerEvents(String, Events)
 * @author Pancake
 */
public interface Events {

	/**
	 * Called when a player joins
	 * @param player Player
	 */
	void playerJoin(Player player);

	/**
	 * Called when a player leaves
	 * @param player Player
	 */
	void playerLeave(Player player);

	/**
	 * Called when a player breaks a block
	 * @param player Player
	 * @param block Block broken
	 * @return Cancel-state
	 */
	boolean playerBreak(Player player, Block block);

	/**
	 * Called when a player places a block
	 * @param player Player
	 * @param block Block placed
	 * @param itemInHand Item in hand
	 * @param blockAgainst Block placed against
	 * @return Cancel-stand
	 */
	boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst);

	/**
	 * Called when an entity takes damage
	 * @param entity Entity
	 * @param damage Amount of damage taken
	 * @param cause Cause of damage
	 * @return Cancel-state
	 */
	boolean entityDamage(Entity entity, double damage, DamageCause cause);

	/**
	 * Called when a player drops an item
	 * @param player Player
	 * @param item Dropped item
	 * @return Cancel-state
	 */
	boolean playerDrop(Player player, Item item);

	/**
	 * Called when an entity picks up an item
	 * @param entity Entity
	 * @param item
	 * @return Cancel-state
	 */
	boolean entityPickup(LivingEntity entity, Item item);

	/**
	 * Called when the player eats something
	 * @param player Player
	 * @param item Food item
	 * @return Cancel-state
	 */
	boolean playerConsume(Player player, ItemStack item);

	/**
	 * Called when a player interacts with the world
	 * @param player Player
	 * @param action Type of interaction
	 * @param clickedBlock Clicked block (or null if air)
	 * @param material Material of clicked block (or null if air)
	 * @param item Item interacted with (can be null)
	 * @return Cancel-state
	 */
	boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item);

	/**
	 * Called when an entity explodes
	 * @param entity Entity
	 * @param loc Location of explosion
	 * @return Cancel-state
	 */
	boolean entityExplosion(Entity entity, Location loc);

	/**
	 * Called when a player dies
	 * @param player Player
	 * @param drops Item drops
	 * @return New item drops
	 */
	List<ItemStack> playerDeath(Player player, List<ItemStack> drops);

	/**
	 * Called when a player clicks in an inventory
	 * @param p Player
	 * @param click Click type
	 * @param slot Slot
	 * @param clickedItem Clicked item
	 * @param cursor Item below cursor
	 * @param inventory Inventory Inventory clicked in
	 * @return Cancel-state
	 */
	boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory);

}
