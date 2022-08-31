package de.pancake.ffa;

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

import de.pancake.common.Events;

public class Lobby implements Events {

	// @formatter:off
	@Override public void playerJoin(Player player) {}
	@Override public void playerLeave(Player player) {}
	@Override public boolean playerBreak(Player player, Block block) { return false; }
	@Override public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) { return false; }
	@Override public boolean entityDamage(Entity entity, double damage, DamageCause cause) { return false; }
	@Override public boolean playerDrop(Player player, Item item) { return false; }
	@Override public boolean entityPickup(LivingEntity entity, Item item) { return false; }
	@Override public boolean playerConsume(Player player, ItemStack item) { return false; }
	@Override public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) { return false; }
	@Override public boolean entityExplosion(Entity entity, Location loc) { return false; }
	@Override public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) { return drops; }

}
