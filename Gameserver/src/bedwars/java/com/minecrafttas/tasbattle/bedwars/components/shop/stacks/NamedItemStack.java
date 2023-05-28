package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

public class NamedItemStack extends ItemStack {
	
	/**
	 * Initialize named item stack
	 * @param material Material of item
	 * @param count Item count
	 * @param name Item name
	 */
	public NamedItemStack(Material material, int count, String name) {
		super(material, count);
		this.editMeta(e -> e.displayName(Component.text("§f" + name)));
	}
	
}