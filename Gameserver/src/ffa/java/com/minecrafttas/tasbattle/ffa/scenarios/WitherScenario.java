package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import java.util.List;

/**
 * Scenario that doubles player health
 */
public class WitherScenario extends AbstractScenario {

	public WitherScenario() {
		super("Wither", new String[] {"Spawns a wither above every player"}, Material.WITHER_SKELETON_SKULL);
	}

	@Override
	public void gameStart(List<Player> participants) {
		for (var p : participants)
			p.getWorld().spawnEntity(p.getLocation().add(0, 10, 0), EntityType.WITHER);
	}

}
