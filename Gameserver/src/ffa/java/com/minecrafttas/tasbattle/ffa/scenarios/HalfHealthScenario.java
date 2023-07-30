package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Scenario that doubles player health
 */
public class HalfHealthScenario extends AbstractScenario {

	public HalfHealthScenario() {
		super("5 Hearts", new String[] {"Halves the amount of hp for every player"}, Material.BARRIER);
	}

	@Override
	public void gameStart(List<Player> participants) {
		for (var p : participants) {
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10.0);
			p.setHealth(10.0);
		}
	}

}
