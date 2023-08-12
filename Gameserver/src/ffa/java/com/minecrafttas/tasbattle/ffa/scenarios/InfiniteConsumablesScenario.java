package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class InfiniteConsumablesScenario extends AbstractScenario {

	private JavaPlugin plugin;

	public InfiniteConsumablesScenario(JavaPlugin plugin) {
		super("Infinite consumables", new String[] {"Most consumables won't be... consumed?"}, Material.ARROW);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
		participants.forEach(this::refillInventory);
	}
	
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		e.setReplacement(e.getItem());
		this.refillInventory(e.getPlayer());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		this.refillInventory(e.getPlayer());
	}

	@EventHandler
	public void onEntityPlace(EntityPlaceEvent e) {
		var player = e.getPlayer();
		var inv = player.getInventory();
		switch (e.getEntityType()) {
			case ARMOR_STAND -> inv.setItem(e.getHand(), new ItemStack(Material.ARMOR_STAND, 16));
			case ENDER_CRYSTAL -> inv.setItem(e.getHand(), new ItemStack(Material.END_CRYSTAL, 64));
			case BOAT -> inv.setItem(e.getHand(), new ItemStack(Material.OAK_BOAT));
			case MINECART -> inv.setItem(e.getHand(), new ItemStack(Material.MINECART));
			case MINECART_CHEST -> inv.setItem(e.getHand(), new ItemStack(Material.CHEST_MINECART));
			case MINECART_COMMAND -> inv.setItem(e.getHand(), new ItemStack(Material.COMMAND_BLOCK_MINECART));
			case MINECART_FURNACE -> inv.setItem(e.getHand(), new ItemStack(Material.FURNACE_MINECART));
			case MINECART_HOPPER -> inv.setItem(e.getHand(), new ItemStack(Material.HOPPER_MINECART));
			case MINECART_TNT -> inv.setItem(e.getHand(), new ItemStack(Material.TNT_MINECART));
			default -> {}
		}

		this.refillInventory(player);
	}

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent e) {
		this.refillInventory(e.getPlayer());
	}

	private void refillInventory(Player player) {
		for (var item : player.getInventory()) {
			if (item == null || (item.getType() == Material.BUCKET))
				continue;

			item.setAmount(item.getMaxStackSize());
		}

		player.updateInventory();
	}

}
