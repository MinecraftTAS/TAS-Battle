package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

/**
 * Purchasable potion item stack in shop inventory
 */
public class PotionItemStack extends PurchasableItemStack {
	
	/**
	 * Initialize purchasable potion item stack
	 * @param price Price type
	 * @param amount Price amount
	 * @param effect Potion effect
	 * @param potion Potion color
	 * @param name Item name
	 */
	public PotionItemStack(Price price, int amount, PotionEffect effect, Color potion, String name) {
		super(price, amount, Material.POTION, 1, name);
		this.editMeta(e -> {
			((PotionMeta) e).addCustomEffect(effect, true);
			((PotionMeta) e).setColor(potion);
		});
	}
	
}
