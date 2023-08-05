package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

import java.util.List;

/**
 * Scenario that shows player health
 */
public class ShowHealthScenario extends AbstractScenario {

	public ShowHealthScenario() {
		super("Show Health", new String[] {"Shows the players health on tab", "and popped totems in chat"}, Material.RED_DYE);
	}

	@Override
	public void gameStart(List<Player> participants) {
		var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			
		// health
		var objective2 = scoreboard.registerNewObjective("hp", Criteria.HEALTH, Component.text("HP"), RenderType.HEARTS);
		objective2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
		for (var participant : participants)
			objective2.getScore(participant).setScore((int) participant.getHealth());
	}

	@EventHandler
	public void onTotemPop(PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.TOTEM_OF_UNDYING)
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + e.getPlayer().getName() + "</green> popped a totem! <aqua>(" + (e.getPlayer().getInventory().all(Material.TOTEM_OF_UNDYING).size() - 1) + " remaining)</aqua></gray>"));;
	}

}
