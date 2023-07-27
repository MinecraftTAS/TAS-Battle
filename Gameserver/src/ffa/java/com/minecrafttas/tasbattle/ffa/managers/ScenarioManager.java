package com.minecrafttas.tasbattle.ffa.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
				meta.displayName(MiniMessage.miniMessage().deserialize("<!italic>" + scenario.getTitle()));
				var lore = new ArrayList<Component>();
				lore.add(MiniMessage.miniMessage().deserialize("<!italic><red>This scenario is disabled</red>"));
				lore.add(Component.text(""));
				Arrays.stream(scenario.getDescription()).forEach(c -> lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_purple>" + c + "</dark_purple>")));
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
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <green>" + player.getName() + " <red>disabled</red> " + scenario.getTitle() + "</green>"));
			player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 0.75f));
			
			item.editMeta(meta -> {
				meta.removeEnchant(Enchantment.LUCK);
				var lore = new ArrayList<>(meta.lore());
				lore.set(0, MiniMessage.miniMessage().deserialize("<!italic><red>This scenario is disabled</red>"));
				meta.lore(lore);
			});
			this.scenarios.put(scenario, item);
		} else {
			this.enabled.add(scenario);
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <green>" + player.getName() + " enabled <white>" + scenario.getTitle() + "</white></green>"));
			player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Source.PLAYER, 0.3f, 1f));
		
			item.editMeta(meta -> {
				meta.addEnchant(Enchantment.LUCK, 10, true);
				var lore = new ArrayList<>(meta.lore());
				lore.set(0, MiniMessage.miniMessage().deserialize("<!italic><red>This scenario is enabled</red>"));
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
		return Arrays.asList(Component.text(""), MiniMessage.miniMessage().deserialize("<!italic><dark_purple>Every FFA game can be customized with scenarios.</dark_purple>"), MiniMessage.miniMessage().deserialize("<!italic><dark_purple>Scenarios are small additions to the game that</dark_purple>"), MiniMessage.miniMessage().deserialize("<!italic><dark_purple>allow for unique and fun gameplay.</dark_purple>"));
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
