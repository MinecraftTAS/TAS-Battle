package com.minecrafttas.tasbattle.ffa.managers;

public class ScenarioManager /*extends LobbyManager */{

//	/**
//	 * Abstract scenario of the ffa game
//	 * @author Pancake
//	 */
//	public abstract class AbstractScenario {
//
//		/**
//		 * Title of the scenario
//		 */
//		private String title;
//
//		/**
//		 * Description of the scenario
//		 */
//		private String description;
//
//		/**
//		 * Creates a new scenario to the game
//		 * @param title Title of the scenario
//		 * @param description Description of the scenario
//		 */
//		public AbstractScenario(String title, String description) {
//			this.title = title;
//			this.description = description;
//		}
//
//		/**
//		 * Called when the game starts
//		 */
//		public abstract void gameStart();
//
//		/**
//		 * Called when the game ends
//		 */
//		public abstract void gameEnd();
//
//		/**
//		 * Returns the title of the scenario
//		 * @return Title of the scenario
//		 */
//		public String getTitle() {
//			return this.title;
//		}
//
//		/**
//		 * Returns the description of the scenario
//		 * @return Description of the scenario
//		 */
//		public String getDescription() {
//			return this.description;
//		}
//
//	}
//	
//	public ScenarioManager() {
//		super("Scenarios", true);
//	}
//
//	@Override
//	protected List<Item> getItems() {
//		return Arrays.asList(
//			this.createItem("20 Hearts", "Every player has 20 hearts instead of 10", Material.RED_DYE),
//			this.createItem("Strength", "Every player has Strength I", Material.IRON_SWORD),
//			this.createItem("Dynamic speed", "Only decreases the game speed when players are close", Material.CLOCK),
//			this.createItem("No drops", "Players and blocks do not drop", Material.GRASS_BLOCK)
//		);
//	}
//
//	@Override
//	protected String getItemBaseLore() {
//		return "\n\n§bClick to toggle!";
//	}
//
//	@Override
//	protected Material getItem() {
//		return Material.COMPASS;
//	}
//
//	@Override
//	protected List<Component> getItemLore() {
//		return Arrays.asList(Component.text("§5Every FFA game can be customized with scenarios."), Component.text("§5These are small additions to the rules that"), Component.text("§5allow for unique and fun gameplay."));
//	}

}
