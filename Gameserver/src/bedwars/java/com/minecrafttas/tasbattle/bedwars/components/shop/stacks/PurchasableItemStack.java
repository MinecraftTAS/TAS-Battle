package com.minecrafttas.tasbattle.bedwars.components.shop.stacks;

import com.minecrafttas.tasbattle.gui.ClickableInventory.Interaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purchasable item stack in shop inventory
 */
@Getter
public class PurchasableItemStack extends ItemStack {
	
	/**
	 * Price types for shop
	 */
	@RequiredArgsConstructor
	public enum Price {
		IRON("<white>_ Iron</white>", Material.IRON_INGOT), GOLD("<gold>_ Gold</gold>", Material.GOLD_INGOT), DIAMOND("<aqua>_ Diamond</aqua>", Material.DIAMOND), EMERALD("<dark_green>_ Emerald</dark_green>", Material.EMERALD);
		
		@Getter private final String displayName;
		@Getter private final Material type;
		
	}
	
	private final Price price;
	private final int priceAmount;
	
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
			e.displayName(MiniMessage.miniMessage().deserialize("<!italic><white>" + name + "</white>"));
			e.lore(List.of(MiniMessage.miniMessage().deserialize("<!italic><gray>Cost: " + price.getDisplayName().replace("_", amount + "") + "</gray>")));
		});
	}
	
	/**
	 * Purchase the item stack
	 * @param p Player
	 * @return Was successful
	 */
	public boolean reward(Player p) {
		// add item to inventory
		var purchaseItemStack = this.clone();
		purchaseItemStack.lore(null);
		p.getInventory().addItem(purchaseItemStack);
		
		return true;
	}
	
	/**
	 * Create purchase interaction
	 * @return Interaction callback
	 */
	public Interaction purchase() {
		return p -> {
			// find available materials
			var items = p.getInventory().all(this.price.type);
			int available = 0;
			for (var item : items.entrySet())
				available += item.getValue().getAmount();
			
			// find and sort materials
			var itemStacks = new ArrayList<>(items.values());
			itemStacks.sort((o1, o2) -> o2.getAmount() - o1.getAmount());
			Collections.reverse(itemStacks);
			
			if (available >= this.priceAmount && this.reward(p)) {
				
				// pay items
				int cost = this.priceAmount;
				for (var itemStack : itemStacks) {
					
					// remove items from inventory
					if (cost >= itemStack.getAmount()) {
						cost -= itemStack.getAmount();
						p.getInventory().removeItem(itemStack);
					} else {
						itemStack.setAmount(itemStack.getAmount() - cost);
						cost = 0;
					}
					
					if (cost <= 0)
						break;
				}
				
				// play sound
				p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.BLOCK, 0.3f, 2.0f));
			} else {
				// play sound
				p.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_LAND, Source.BLOCK, 0.3f, 1.0f));
			}
		};
	}
	
}
