package com.minecrafttas.tasbattle.ffa.managers;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.minecrafttas.tasbattle.gui.ListInventory.Item;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

public class KitManager extends LobbyManager {

	public KitManager() {
		super("Kits", false);
	}

	@Override
	protected List<Item> getItems() {
		return Arrays.asList(
			this.createItem("Kit 1", "B", Material.DIAMOND_CHESTPLATE),
			this.createItem("Kit 2", "D", Material.IRON_SWORD),
			this.createItem("Kit 3", "C", Material.IRON_CHESTPLATE),
			this.createItem("Kit 4", "A", Material.CHAINMAIL_BOOTS)
		);
	}

	@Override
	protected String getItemBaseLore() {
		return "\n\n" + ChatColor.AQUA + "Click to select!";
	}

	@Override
	protected Material getItem() {
		return Material.CHEST;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(ChatColor.DARK_PURPLE + "Vote for a kit"), Component.text(ChatColor.DARK_PURPLE + "Every player will spawn with the same gear"));
	}

}
