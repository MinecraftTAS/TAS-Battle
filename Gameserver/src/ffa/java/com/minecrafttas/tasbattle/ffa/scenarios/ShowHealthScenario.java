package com.minecrafttas.tasbattle.ffa.scenarios;

import com.minecrafttas.tasbattle.ffa.managers.ScenarioManager.AbstractScenario;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

import java.util.List;

/**
 * Scenario that shows player health
 */
public class ShowHealthScenario extends AbstractScenario {

	private JavaPlugin plugin;

	public ShowHealthScenario(JavaPlugin plugin) {
		super("Show Health", new String[] {"Shows the players health on tab", "and popped totems in chat"}, Material.RED_DYE);
		this.plugin = plugin;
	}

	@Override
	public void gameStart(List<Player> participants) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);

		var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			
		// health
		var objective2 = scoreboard.registerNewObjective("hp", Criteria.HEALTH, Component.text("HP"), RenderType.HEARTS);
		objective2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			
		for (var participant : participants)
			objective2.getScore(participant).setScore((int) participant.getHealth());
	}

	@EventHandler
	public void onTotemPop(EntityResurrectEvent e) {
		if (e.getEntity() instanceof Player player && !e.isCancelled())
			Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <gray><green>" + player.getName() + "</green> popped a totem! <aqua>(" + (player.getInventory().all(Material.TOTEM_OF_UNDYING).size() - (player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING ? 0 : 1)) + " remaining)</aqua></gray>"));
	}

}
