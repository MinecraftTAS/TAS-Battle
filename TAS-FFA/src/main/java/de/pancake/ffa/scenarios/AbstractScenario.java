package de.pancake.ffa.scenarios;

import de.pancake.common.Events;

/**
 * Abstract scenario of the ffa game
 * @author Pancake
 */
public abstract class AbstractScenario implements Events {

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
