package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class InfiniteConsumablesScenario extends AbstractScenario {

	private JavaPlugin plugin;

	public InfiniteConsumablesScenario(JavaPlugin plugin) {
		super("Infinite consumables", new String[] {"Consumables won't be... consumed?"}, Material.BARRIER);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
	
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		e.setReplacement(e.getItem());
	}
	
}
