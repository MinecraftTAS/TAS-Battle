package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NamedItemStack extends ItemStack {
	
	/**
	 * Initialize named item stack
	 * @param material Material of item
	 * @param count Item count
	 * @param name Item name
	 */
	public NamedItemStack(Material material, int count, String name) {
		super(material, count);
		this.editMeta(e -> e.displayName(MiniMessage.miniMessage().deserialize("<!italic><white>" + name + "</white>")));
	}
	
}
