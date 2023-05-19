package com.minecrafttas.tasbattle.ffa.managers;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.minecrafttas.tasbattle.gamemode.GameMode;
import com.minecrafttas.tasbattle.gui.ListInventory.Item;
import com.minecrafttas.tasbattle.lobby.LobbyManager;

import net.kyori.adventure.text.Component;

public class ScenarioManager extends LobbyManager {

	/**
	 * Abstract scenario of the ffa game
	 * @author Pancake
	 */
	public abstract class AbstractScenario implements GameMode {

		/**
		 * Title of the scenario
		 */
		private String title;

		/**
		 * Description of the scenario
		 */
		private String description;

		/**
		 * Creates a new scenario to the game
		 * @param title Title of the scenario
		 * @param description Description of the scenario
		 */
		public AbstractScenario(String title, String description) {
			this.title = title;
			this.description = description;
		}

		/**
		 * Called when the game starts
		 */
		public abstract void gameStart();

		/**
		 * Called when the game ends
		 */
		public abstract void gameEnd();

		/**
		 * Returns the title of the scenario
		 * @return Title of the scenario
		 */
		public String getTitle() {
			return this.title;
		}

		/**
		 * Returns the description of the scenario
		 * @return Description of the scenario
		 */
		public String getDescription() {
			return this.description;
		}

	}
	
	public ScenarioManager() {
		super("Scenarios", true);
	}

	@Override
	protected List<Item> getItems() {
		return Arrays.asList(
			this.createItem("20 Hearts", "Every player has 20 hearts instead of 10", Material.RED_DYE),
			this.createItem("Strength", "Every player has Strength I", Material.IRON_SWORD),
			this.createItem("Dynamic speed", "Only decreases the game speed when players are close", Material.CLOCK),
			this.createItem("No drops", "Players and blocks do not drop", Material.GRASS_BLOCK)
		);
	}

	@Override
	protected String getItemBaseLore() {
		return "\n\n" + ChatColor.AQUA + "Click to toggle!";
	}

	@Override
	protected Material getItem() {
		return Material.COMPASS;
	}

	@Override
	protected List<Component> getItemLore() {
		return Arrays.asList(Component.text(ChatColor.DARK_PURPLE + "Every FFA game can be customized with scenarios."), Component.text(ChatColor.DARK_PURPLE + "These are small additions to the rules that"), Component.text(ChatColor.DARK_PURPLE + "allow for unique and fun gameplay."));
	}

}
