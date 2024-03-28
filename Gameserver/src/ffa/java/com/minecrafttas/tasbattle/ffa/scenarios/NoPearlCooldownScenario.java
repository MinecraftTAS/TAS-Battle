package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import io.papermc.paper.event.player.PlayerItemCooldownEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class NoPearlCooldownScenario extends AbstractScenario {

	private final JavaPlugin plugin;

	public NoPearlCooldownScenario(JavaPlugin plugin) {
		super("No pearl cooldown", new String[] {"This scenario disables ender pearl cooldown"}, Material.ENDER_PEARL);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onCooldown(PlayerItemCooldownEvent e) {
		if (e.getType() == Material.ENDER_PEARL)
			e.setCancelled(true);
	}

}
