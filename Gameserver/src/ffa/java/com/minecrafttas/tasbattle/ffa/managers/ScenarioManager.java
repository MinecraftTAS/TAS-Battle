package com.minecrafttas.tasbattle.ffa.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minecrafttas.tasbattle.ffa.scenarios.DisableDropsScenario;
import com.minecrafttas.tasbattle.ffa.scenarios.ShowHealthScenario;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;

/**
 * FFA Scenario Manager
 * @author Pancake
 */
public class ScenarioManager extends LobbyManager {

	/**
	 * Abstract scenario
	 * @author Pancake
	 */
	@Getter
	@AllArgsConstructor
	@EqualsAndHashCode
	public static abstract class AbstractScenario implements Listener {
		
		private String title;
		private String[] description;
		private Material type;
		
		public abstract void gameStart(List<Player> participants);
		
	}
	
	@Getter
	private Inventory inventory;

	@Getter
	private BiMap<AbstractScenario, ItemStack> scenarios;
	
	@Getter
	private List<AbstractScenario> enabled;
	
	/**
	 * Initialize scenario manager
	 * @param plugin Plugin
	 */
	public ScenarioManager(JavaPlugin plugin) {
		super(plugin);
		this.enabled = new ArrayList<>();
		this.scenarios = HashBiMap.create();
		this.inventory = Bukkit.createInventory(null, 54, Component.text("Scenarios"));
		
		var scenarioList = Arrays.asList(
			new ShowHealthScenario(),
			new DisableDropsScenario(plugin)
		);
		
		for (var scenario : scenarioList) {
			var item = new ItemStack(scenario.getType());
			item.editMeta(meta -> {
				meta.displayName(Component.text("§r§f" + scenario.getTitle()));
				var lore = new ArrayList<Component>();
				lore.add(Component.text("§cThis scenario is disabled"));
				lore.add(Component.text(""));
				Arrays.stream(scenario.getDescription()).forEach(c -> lore.add(Component.text("§r§5" + c)));
				meta.lore(lore);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			});
			this.scenarios.put(scenario, item);
			this.inventory.addItem(item);
		}
	}

	/**
	 * Handle inventory click and toggle scenario
	 * @param e Event
	 */
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		var player = (Player) e.getWhoClicked();
		var item = e.getCurrentItem();

		if (e.getInventory() != this.inventory || !this.isActive() || item == null)
			return;
		
		// find scenario clicked
		var scenario = this.scenarios.inverse().get(item);

		if (scenario == null)
			return;
		
		// update votes
		if (this.enabled.contains(scenario)) {
			this.enabled.remove(scenario);
			Bukkit.broadcast(Component.text("§b» §a" + player.getName() + " §cdisabled §a").append(Component.text("§f" + scenario.getTitle())));
			player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 0.75f));
			
			item.editMeta(meta -> {
				meta.removeEnchant(Enchantment.LUCK);
				var lore = new ArrayList<>(meta.lore());
				lore.set(0, Component.text("§cThis scenario is disabled"));
				meta.lore(lore);
			});
			this.scenarios.put(scenario, item);
		} else {
			this.enabled.add(scenario);
			Bukkit.broadcast(Component.text("§b» §a" + player.getName() + " enabled ").append(Component.text("§f" + scenario.getTitle())));
			player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 1f));
		
			item.editMeta(meta -> {
				meta.addEnchant(Enchantment.LUCK, 10, true);
				var lore = new ArrayList<>(meta.lore());
				lore.set(0, Component.text("§aThis scenario is enabled"));
				meta.lore(lore);
			});
			this.scenarios.put(scenario, item);
		}
		
	}
	
	@Override
	protected Material getItem() {
		return Material.COMPASS;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(""), Component.text("§r§5Every FFA game can be customized with scenarios."), Component.text("§r§5Scenarios are small additions to the game that"), Component.text("§r§5allow for unique and fun gameplay."));
	}
	
	@Override
	public void interact(Player p) {
		p.openInventory(this.inventory);
	}
	
	@Override
	protected String getName() {
		return "Toggle scenarios";
	}

}
