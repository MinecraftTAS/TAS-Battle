package com.minecrafttas.tasbattle.ffa.lobby;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.common.Events;
import com.minecrafttas.tasbattle.ffa.FFA;

import net.kyori.adventure.text.Component;

/**
 * Event listener during lobby phase
 * @author Pancake
 */
public class Lobby implements Events {

	/**
	 * Lobby timer instance
	 */
	private LobbyTimer timer = new LobbyTimer(90, 2, 3, FFA::startGame);

	/**
	 * Lobby kit manager
	 */
	private LobbyKitManager kits = new LobbyKitManager();

	/**
	 * Lobby scenario manager
	 */
	private LobbyScenarioManager scenarios = new LobbyScenarioManager();

	/**
	 * Update the lobby countdown when a player joins the server and edit the players inventory
	 */
	@Override
	public void playerJoin(Player player) {
		this.timer.addPlayer(player);

		// update inventory
		var inv = player.getInventory();
		inv.clear();

		var kitsItem = new ItemStack(Material.CHEST);
		kitsItem.editMeta(m -> {
			m.displayName(Component.text(ChatColor.WHITE + "Kits"));
			m.lore(Arrays.asList(Component.text(ChatColor.DARK_PURPLE + "Vote for a kit"), Component.text(ChatColor.DARK_PURPLE + "Every player will spawn with the same gear")));
		});
		inv.setItem(0, kitsItem);

		var scenariosItem = new ItemStack(Material.COMPASS);
		scenariosItem.editMeta(m -> {
			m.displayName(Component.text(ChatColor.WHITE + "Scenarios"));
			m.lore(Arrays.asList(Component.text(ChatColor.DARK_PURPLE + "Every FFA game can be customized with scenarios."), Component.text(ChatColor.DARK_PURPLE + "These are small additions to the rules that"), Component.text(ChatColor.DARK_PURPLE + "allow for unique and fun gameplay.")));
		});
		inv.setItem(1, scenariosItem);

		var barrierItem = new ItemStack(Material.RED_BED);
		barrierItem.editMeta(m -> {
			m.displayName(Component.text(ChatColor.RED + "Leave the game"));
		});
		inv.setItem(8, barrierItem);

		player.updateInventory(); // not taking any risks ._.
	}

	/**
	 * Update the lobby countdown when a player leaves the server
	 */
	@Override
	public void playerLeave(Player player) {
		this.timer.removePlayer(player);
	}

	/**
	 * Opens the scenarios and kits menu or disconnects the player on interaction
	 * @see #playerInteract(Player, Action, Block, Material, ItemStack)
	 */
	private void playerInteract2(Player player, Action action, Block clickedBlock, Material material, ItemStack item) {
		if (item == null)
			return;
		if (item.getType() == Material.CHEST)
			this.kits.openInventory(player);
		else if (item.getType() == Material.COMPASS)
			this.scenarios.openInventory(player);
		else if (item.getType() == Material.RED_BED)
			player.kick(Component.text("You left the game."), Cause.SELF_INTERACTION);
	}

	/**
	 * Interacts with the scenarios and kits menu in a ui
	 * @see #playerClick(Player, ClickType, int, ItemStack, ItemStack, Inventory)
	 */
	private void playerClick2(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) {
		this.scenarios.interact(p, clickedItem);
		this.kits.interact(p, clickedItem);
	}

	// @formatter:off

	// Restrict basic player events
	@Override public boolean playerBreak(Player player, Block block) { return true; }
	@Override public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) { return true; }
	@Override public boolean playerDrop(Player player, Item item) { return true; }
	@Override public boolean entityDamage(Entity entity, double damage, DamageCause cause) { return true; }
	@Override public boolean playerConsume(Player player, ItemStack item) { return true; }
	@Override public boolean entityPickup(LivingEntity entity, Item item) { return true; }
	@Override public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) { this.playerInteract2(player, action, clickedBlock, material, item); return true; }
	@Override public boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) { this.playerClick2(p, click, slot, clickedItem, cursor, inventory); return true; }

	// Non-required events
	@Override public boolean entityExplosion(Entity entity, Location loc) { return false; }
	@Override public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) { return drops; }


}
