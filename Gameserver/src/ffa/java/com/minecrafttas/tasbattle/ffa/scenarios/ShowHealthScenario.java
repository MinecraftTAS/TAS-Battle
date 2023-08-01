package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

import java.util.List;

/**
 * Scenario that shows player health
 */
public class ShowHealthScenario extends AbstractScenario {

	public ShowHealthScenario() {
		super("Show Health", new String[] {"Shows the players health above the player", "and their totem count on tab"}, Material.RED_DYE);
	}

	@Override
	public void gameStart(List<Player> participants) {
		var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		// totems popped
		var objective = scoreboard.registerNewObjective("totem", Criteria.statistic(Statistic.USE_ITEM, Material.TOTEM_OF_UNDYING), Component.text("Totems"), RenderType.INTEGER);
		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
		// health
		var objective2 = scoreboard.registerNewObjective("hp", Criteria.HEALTH, Component.text("HP"), RenderType.HEARTS);
		objective2.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
		for (var participant : participants)
			objective2.getScore(participant).setScore((int) participant.getHealth());
	}

}
