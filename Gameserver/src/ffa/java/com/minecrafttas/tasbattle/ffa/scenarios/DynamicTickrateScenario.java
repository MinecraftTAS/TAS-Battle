package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import com.minecrafttas.tasbattle.ffa.FFA;
import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class DynamicTickrateScenario extends AbstractScenario {

	private final TASBattleGameserver plugin;

	private float latestTickrate = 20.0f;

	public DynamicTickrateScenario(TASBattleGameserver plugin) {
		super("Dynamic Tickrate", new String[] {"Slow down the game to 3.33333tps while near other players", "and keep it at 10 on distance"}, Material.OBSIDIAN);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
			if (((FFA) this.plugin.getGameMode()).getGameLogic().isFinished())
				return;

			// calculate distances
			var smallestDistance = 1000.0;
			for (var p1 : participants) {
				for (var p2 : participants) {
					if (p1 == p2)
						continue;

					var distance = p1.getLocation().distance(p2.getLocation());
					if (distance < smallestDistance)
						smallestDistance = distance;
				}

				// send current tickrate actionbar
				p1.sendActionBar(MiniMessage.miniMessage().deserialize(String.format("<aqua>%.2f</aqua><white>tps</white>", latestTickrate)));
			}

			// update tickrates
			float newTickrate;
			if (smallestDistance > 40)
				newTickrate = 10.0f;
			else if (smallestDistance > 8)
				newTickrate = 5.0f;
			else
				newTickrate = 3.3333333f;

			if (newTickrate != this.latestTickrate) {
				this.plugin.getTickrateChanger().setTickrate(newTickrate);
				this.latestTickrate = newTickrate;
			}
		}, 2L, 2L);
	}

}
