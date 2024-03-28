package com.minecrafttas.tasbattle.ffa.scenarios;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;

public class DisableDropsScenario extends AbstractScenario {

	private final JavaPlugin plugin;
	
	public DisableDropsScenario(JavaPlugin plugin) {
		super("Disable Drops", new String[] {"Items and players don't drop anymore"}, Material.BARRIER);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
	
	@EventHandler
	public void onBlockDrop(BlockDropItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDrop(EntityDeathEvent e) {
		e.getDrops().clear();
		e.setDroppedExp(0);
	}
	
}
