package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class HardScenario extends AbstractScenario {

	public HardScenario() {
		super("Hard difficulty", new String[] {"Change the game difficulty to hard"}, Material.BEDROCK);
	}

	@Override
	public void gameStart(List<Player> participants) {
		participants.get(0).getWorld().setDifficulty(Difficulty.HARD);
	}

}
