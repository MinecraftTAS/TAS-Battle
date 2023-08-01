package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class NoHitDelayScenario extends AbstractScenario {

	public NoHitDelayScenario() {
		super("No hit-delay", new String[] {"Don't confuse this with no hit cooldown,", "this will get rid of invincibility frames!"}, Material.NETHER_STAR);
	}

	@Override
	public void gameStart(List<Player> participants) {
		for (var p : participants)
			p.setMaximumNoDamageTicks(1);
	}
	
}
