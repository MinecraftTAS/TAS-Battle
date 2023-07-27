package com.minecrafttas.tasbattle.lobby;

import java.util.List;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import com.minecrafttas.tasbattle.TASBattleGameserver.GameMode;

import net.kyori.adventure.text.Component;

/**
 * Event listener during lobby phase
 * @author Pancake
 */
public class Lobby implements Listener {
	
	private LobbyTimer timer;
	private List<LobbyManager> managers;
	
	/**
	 * Initialize lobby
	 * @param plugin Plugin
	 * @param gameMode Game mode
	 */
	public Lobby(TASBattleGameserver plugin, GameMode gameMode) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.managers = gameMode.createManagers();
		this.timer = new LobbyTimer(plugin, 90, 2, 8, participants -> {
			for (var manager : this.managers)
				manager.setActive(false);
			gameMode.startGameMode(participants);
		});
	}

	/**
	 * Update lobby countdown and edit inventory when player joins the server
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (this.timer.isGameRunning())
			return;
		
		var player = e.getPlayer();
		this.timer.addPlayer(player);

		// update inventory
		var inv = player.getInventory();
		inv.clear();

		var i = 0;
		for (LobbyManager manager : this.managers) {
			var item = new ItemStack(manager.getItem());
			item.editMeta(m -> {
				m.displayName(MiniMessage.miniMessage().deserialize("<!italic><white>" + manager.getName() + "</white>"));
				m.lore(manager.getItemLore());
			});
			inv.setItem(i++, item);
		}
		
		var leaveItem = new ItemStack(Material.RED_BED);
		leaveItem.editMeta(m -> {
			m.displayName(MiniMessage.miniMessage().deserialize("<!italic><red>Leave the game</red>"));
		});
		inv.setItem(8, leaveItem);
		
		player.updateInventory(); // not taking any risks ._.

		e.joinMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + player.getName() + "</green> joined the game</gray>"));
	}

	/**
	 * Update lobby countdown when player leaves the server
	 * @param e Event
	 */
	@EventHandler
	public void onQuitLeave(PlayerQuitEvent e) {
		if (this.timer.isGameRunning())
			return;
		
		var player = e.getPlayer();
		this.timer.removePlayer(player);
		e.quitMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + player.getName() + "</green> left the game</gray>"));
	}

	/**
	 * Handle player interaction for configuration
	 * @param e Event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (this.timer.isGameRunning())
			return;
		
		e.setCancelled(true);
		
		var item = e.getItem();
		var player = e.getPlayer();
		
		if (item == null)
			return;
		
		for (LobbyManager manager : this.managers)
			if (item.getType() == manager.getItem())
				manager.interact(player);
		
		if (item.getType() == Material.RED_BED)
			player.kick(Component.text("You left the game."), Cause.SELF_INTERACTION);
	}

	// restrict basic player events
	@EventHandler public void onClickEvent(InventoryClickEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onPlayerBreak(BlockBreakEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onPlayerPlace(BlockPlaceEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onPlayerDrop(PlayerDropItemEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onPlayerConsume(PlayerItemConsumeEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onEntityDamage(EntityDamageEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onEntityPickup(EntityPickupItemEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }
	@EventHandler public void onHungerLoss(FoodLevelChangeEvent e) { if (!this.timer.isGameRunning()) e.setCancelled(true); }

}
