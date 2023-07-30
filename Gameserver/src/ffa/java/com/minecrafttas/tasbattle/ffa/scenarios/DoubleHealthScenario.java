package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Scenario that doubles player health
 */
public class DoubleHealthScenario extends AbstractScenario {

	public DoubleHealthScenario() {
		super("20 Hearts", new String[] {"Doubles the amount of hp for every player"}, Material.GOLDEN_APPLE);
	}

	@Override
	public void gameStart(List<Player> participants) {
		for (var p : participants) {
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
			p.setHealth(40.0);
		}
	}

}
