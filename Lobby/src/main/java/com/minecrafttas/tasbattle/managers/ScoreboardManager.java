package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleLobby;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

/**
 * Scoreboard manager managing scoreboard and titles
 * @author Pancake
 */
public class ScoreboardManager implements Listener {

    /**
     * Initialize scoreboard manager
     * @param plugin Plugin
     */
    public ScoreboardManager(TASBattleLobby plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();
        var scoreboard = p.getScoreboard();

        // unregister previous objective
        var existingObjective = scoreboard.getObjective(p.getName());
        if (existingObjective != null)
            existingObjective.unregister();

        // register objective
        var objective = scoreboard.registerNewObjective(p.getName(), Criteria.DUMMY, MiniMessage.miniMessage().deserialize("<bold><red>TAS</red><gold>Battle</bold> <white>Lobby</white>"), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(" ").setScore(5);
        objective.getScore("§fWelcome to the").setScore(4);
        objective.getScore("§fMinecraft §bTAS Battle §fServer").setScore(3);
        objective.getScore("").setScore(2);
        objective.getScore("§8https://discord.gg/jGhNxpd").setScore(1);
        objective.getScore("§8https://minecrafttas.com").setScore(0);
    }

}
