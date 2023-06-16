package com.minecrafttas.tasbattle.ffa.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

public class KitManager extends LobbyManager {

	@Getter
	private Inventory inventory;

	@Getter
	private Map<Player, ItemStack> votes; // TODO: replace item stack with kit 
	
	public KitManager(JavaPlugin plugin) {
		super(plugin);
		this.votes = new HashMap<>();
		this.inventory = Bukkit.createInventory(null, 27, Component.text("Kits"));
		
		// FIXME: test items
		for (int i = 0; i < 9; i++) {
			var item = new ItemStack(Material.values()[i+1]);
			item.editMeta(meta -> {
				meta.displayName(Component.text("some random test kit"));
				meta.lore(Arrays.asList(Component.text("§70 players voted for this kit"), Component.text(""), Component.text("§5test item")));
			});
			this.inventory.addItem(item);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		var player = (Player) e.getWhoClicked();
		var item = e.getCurrentItem();

		if (e.getInventory() != this.inventory || !this.isActive() || item == null)
			return;
		
		// update votes
		this.votes.put(player, item);
		for (var itemStack : this.inventory.getContents()) {
			if (itemStack == null)
				continue;
			
			// calculate votes for item
			int voteCount = 0;
			for (var vote : this.votes.entrySet())
				if (vote.getValue().equals(itemStack))
					voteCount++;
			
			// update first line of lore
			var lore = new ArrayList<>(itemStack.lore());
			lore.set(0, Component.text("§7" + voteCount + " players voted for this kit"));
			itemStack.lore(lore);
		}
		
		player.sendMessage(Component.text("§b» §7You voted for §a").append(item.getItemMeta().displayName()));
		player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 1f));
		
	}
	
	@Override
	protected Material getItem() {
		return Material.CHEST;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(""), Component.text("§5The most voted kit will be equipped to"), Component.text("§5all players at the beginning of the game"));
	}

	@Override
	public void interact(Player p) {
		p.openInventory(this.inventory);
	}

	@Override
	protected String getName() {
		return "Vote for a kit";
	}

}
