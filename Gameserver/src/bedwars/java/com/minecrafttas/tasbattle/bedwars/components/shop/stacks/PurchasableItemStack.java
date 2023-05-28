package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

/**
 * Purchasable item stack in shop inventory
 */
public class PurchasableItemStack extends ItemStack {
	
	/**
	 * Price types for shop
	 */
	@RequiredArgsConstructor
	public enum Price {
		IRON("§f_ Iron", Material.IRON_INGOT), GOLD("§6_ Gold", Material.GOLD_INGOT), DIAMOND("§b_ Diamond", Material.DIAMOND), EMERALD("§2_ Emerald", Material.EMERALD);
		
		@Getter private final String displayName;
		@Getter private final Material type;
		
	}
	
	@Getter private final Price price;
	@Getter private final int priceAmount;
	
	/**
	 * Initialize purchasable item stack
	 * @param price Price type
	 * @param amount Price amount
	 * @param material Material of item
	 * @param count Item count
	 * @param name Item name
	 */
	public PurchasableItemStack(Price price, int amount, Material material, int count, String name) {
		super(material, count);
		this.price = price;
		this.priceAmount = amount;
		this.editMeta(e -> {
			e.displayName(Component.text("§f" + name));
			e.lore(Arrays.asList(Component.text("§7Cost: " + price.getDisplayName().replace("_", amount + ""))));
		});
	}
	
	/**
	 * Create purchase runnable
	 * @return
	 */
	public Runnable purchase() {
		return null;
	}
	
}