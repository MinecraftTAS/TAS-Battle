package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SlowerTickrateScenario extends AbstractScenario {

	private TASBattleGameserver plugin;

	public SlowerTickrateScenario(TASBattleGameserver plugin) {
		super("Slower Tickrate", new String[] {"Slow down the game to 3.33333tps"}, Material.SOUL_SAND);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		this.plugin.getTickrateChanger().setTickrate(3.3333333f);
	}

	
}
