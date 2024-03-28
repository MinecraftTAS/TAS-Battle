package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Purchasable item stack with enchantments in shop inventory with custom buy logic
 */
public class CustomItemStack extends PurchasableItemStack {

	private final Function<Player, Boolean> purchase;
	
	/**
	 * Initialize purchasable item stack with custom buy logic
	 * @param price Price type
	 * @param amount Price amount
	 * @param type Item type
	 * @param name Item name
	 * @param purchase Purchase logic
	 */
	public CustomItemStack(Price price, int amount, Material type, int count, String name, Function<Player, Boolean> purchase) {
		super(price, amount, type, count, name);
		this.purchase = purchase;
	}
	
	@Override
	public boolean reward(Player p) {
		return this.purchase.apply(p);
	}
	
}
