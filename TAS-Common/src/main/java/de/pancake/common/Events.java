package de.pancake.common;

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
import org.bukkit.inventory.ItemStack;

/**
 * This interface provides all events for developing a minigame phase.
 * @see CommonTASBattle#registerEvents(String, Events)
 * @author Pancake
 */
public interface Events {

	/**
	 * Called when a player joins
	 * @param p Player
	 */
	void playerJoin(Player p);

	/**
	 * Called when a player leaves
	 * @param p Player
	 */
	void playerLeave(Player p);

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
	 * @param p Player
	 * @param item Dropped item
	 * @return Cancel-state
	 */
	boolean playerDrop(Player p, Item item);

	/**
	 * Called when an entity picks up an item
	 * @param entity Entity
	 * @param item
	 * @return Cancel-state
	 */
	boolean playerPickup(LivingEntity entity, Item item);

	/**
	 * Called when the player eats something
	 * @param p Player
	 * @param item Food item
	 * @return Cancel-state
	 */
	boolean playerConsume(Player p, ItemStack item);

	/**
	 * Called when a player interacts with the world
	 * @param p Player
	 * @param action Type of interaction
	 * @param clickedBlock Clicked block (or null if air)
	 * @param material Material of clicked block (or null if air)
	 * @param item Item interacted with (can be null)
	 * @return Cancel-state
	 */
	boolean playerInteract(Player p, Action action, Block clickedBlock, Material material, ItemStack item);

	/**
	 * Called when an entity explodes
	 * @param e Entity
	 * @param loc Location of explosion
	 * @return Cancel-state
	 */
	boolean entityExplosion(Entity e, Location loc);

	/**
	 * Called when a player dies
	 * @param p Player
	 * @param drops Item drops
	 * @return New item drops
	 */
	List<ItemStack> playerDeath(Player p, List<ItemStack> drops);

}
