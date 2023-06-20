package com.minecrafttas.tasbattle.ffa.scenarios;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;

import net.kyori.adventure.text.Component;

/**
 * Scenario that shows player health
 */
public class ShowHealthScenario extends AbstractScenario {

	public ShowHealthScenario() {
		super("Show Health", new String[] {"Shows the players health on tab"}, Material.RED_DYE);
	}

	@Override
	public void gameStart(List<Player> participants) {
		var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		// playerlist health
		var objective = scoreboard.registerNewObjective("hp_playerlist", Criteria.HEALTH, Component.text("HP"), RenderType.HEARTS);
		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
		// playerlist health
		var objective2 = scoreboard.registerNewObjective("hp_nametag", Criteria.HEALTH, Component.text("HP"), RenderType.HEARTS);
		objective2.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
		for (var participant : participants) {
			objective.getScore(participant).setScore((int) participant.getHealth());
			objective2.getScore(participant).setScore((int) participant.getHealth());
		}
	}

}
