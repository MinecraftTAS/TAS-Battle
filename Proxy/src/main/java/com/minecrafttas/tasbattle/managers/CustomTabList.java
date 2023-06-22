package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

/**
 * Custom tab list and actionb ar
 * @author Pancake
 */
public class CustomTabList {

    /**
     * Initialize custom tab list
     * @param plugin Plugin
     */
    public CustomTabList(TASBattleProxy plugin) {
        var server = plugin.getServer();
        server.getEventManager().register(plugin, this);
        server.getScheduler().buildTask(plugin, () -> {
            for (var p : server.getServer("lobbypreview").get().getPlayersConnected())
                if (server.getPlayerCount() == 1)
                    p.sendActionBar(Component.text("§fThere is currently §a" + server.getPlayerCount() + " player§f online."));
            else
                    p.sendActionBar(Component.text("§fThere are currently §a" + server.getPlayerCount() + " players§f online."));
        }).repeat(2L, TimeUnit.SECONDS).schedule();
    }

    /**
     * Update tab list on login
     * @param e Post login event
     */
    @Subscribe
    public void onPlayerJoin(ServerConnectedEvent e) {
        e.getPlayer().sendPlayerListHeaderAndFooter(
                Component.text("§c§lTAS§6§lBattle"),
                Component.text("§7Official TAS Battle Minecraft Server")
        );
    }

}
