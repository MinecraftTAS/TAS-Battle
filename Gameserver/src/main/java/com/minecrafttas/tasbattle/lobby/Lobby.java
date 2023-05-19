package com.minecrafttas.tasbattle.lobby;

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

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TASBattle.AbstractGameMode;
import com.minecrafttas.tasbattle.gamemode.GameMode;

import net.kyori.adventure.text.Component;

/**
 * Event listener during lobby phase
 * @author Pancake
 */
public class Lobby implements GameMode {
	
	private LobbyTimer timer;
	private List<LobbyManager> managers;
	
	/**
	 * Initialize lobby
	 * @param plugin Plugin
	 * @param gameMode Game mode
	 */
	public Lobby(TASBattle plugin, AbstractGameMode gameMode) {
		this.managers = gameMode.createManagers();
		this.timer = new LobbyTimer(plugin, 90, 2, 3, gameMode::startGameMode);
	}

	/**
	 * Update lobby countdown and edit inventory when player joins the server
	 */
	@Override
	public void playerJoin(Player player) {
		this.timer.addPlayer(player);

		// update inventory
		var inv = player.getInventory();
		inv.clear();

		int i = 0;
		for (LobbyManager manager : this.managers) {
			var item = new ItemStack(manager.getItem());
			item.editMeta(m -> {
				m.displayName(Component.text(ChatColor.WHITE + manager.getInventoryTitle()));
				m.lore(manager.getItemLore());
			});
			inv.setItem(i++, item);
		}
		
		var leaveItem = new ItemStack(Material.RED_BED);
		leaveItem.editMeta(m -> {
			m.displayName(Component.text(ChatColor.RED + "Leave the game"));
		});
		inv.setItem(8, leaveItem);
		
		// TODO: setup config items here
		
		player.updateInventory(); // not taking any risks ._.
	}

	/**
	 * Update lobby countdown when player leaves the server
	 */
	@Override
	public void playerLeave(Player player) {
		this.timer.removePlayer(player);
	}

	/**
	 * Handle player interaction for configuration
	 * @see #playerInteract(Player, Action, Block, Material, ItemStack)
	 */
	private void playerInteract2(Player player, Action action, Block clickedBlock, Material material, ItemStack item) {
		if (item == null)
			return;
		
		for (LobbyManager manager : this.managers)
			if (item.getType() == manager.getItem())
				manager.openInventory(player);
		
		if (item.getType() == Material.RED_BED)
			player.kick(Component.text("You left the game."), Cause.SELF_INTERACTION);
	}

	/**
	 * Handle configuration interactions
	 * @see #playerClick(Player, ClickType, int, ItemStack, ItemStack, Inventory)
	 */
	private void playerClick2(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) {
		for (LobbyManager manager : this.managers)
			manager.onInteract(p, clickedItem);
	}

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
