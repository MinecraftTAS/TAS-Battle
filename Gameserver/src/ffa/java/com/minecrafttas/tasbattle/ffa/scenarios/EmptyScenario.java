package com.minecrafttas.tasbattle.ffa.scenarios;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;

public class EmptyScenario extends AbstractScenario {

	public EmptyScenario(JavaPlugin plugin, String title, String[] description, Material type) {
		super(title, description, type);
	}

	@Override
	public void gameStart(List<Player> participants) {
		System.out.println("start");
	}

}
