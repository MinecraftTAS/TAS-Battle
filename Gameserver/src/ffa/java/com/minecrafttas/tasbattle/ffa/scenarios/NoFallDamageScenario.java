package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class NoFallDamageScenario extends AbstractScenario {

	public NoFallDamageScenario() {
		super("No fall damage", new String[] {"Disables fall damage"}, Material.FEATHER);
	}

	@Override
	public void gameStart(List<Player> participants) {
		participants.stream().findFirst().ifPresent(c -> c.getWorld().setGameRule(GameRule.FALL_DAMAGE, false));
	}
	
}
