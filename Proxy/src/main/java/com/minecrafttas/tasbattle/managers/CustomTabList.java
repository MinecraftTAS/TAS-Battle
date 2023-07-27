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

    private static final List<String> TASBATTLE_SERVERS = List.of("lobbypreview", "gameserver01preview");
    private static final List<String> LAMP_SERVERS = List.of("lamp");

    /**
     * Initialize custom tab list
     * @param plugin Plugin
     */
    public CustomTabList(TASBattleProxy plugin) {
        var server = plugin.getServer();
        server.getEventManager().register(plugin, this);
        server.getScheduler().buildTask(plugin, () -> {
            server.getServer("lobbypreview").ifPresent(c -> {
                Component msg;
                if (server.getPlayerCount() == 1)
                    msg = MiniMessage.miniMessage().deserialize("<white>There is currently <green>" + server.getPlayerCount() + " player</green> online.</white>");
                else
                    msg = MiniMessage.miniMessage().deserialize("<white>There are currently <green>" + server.getPlayerCount() + " players</green> online.</white>");

                for (var p : c.getPlayersConnected())
                    p.sendActionBar(msg);
            });

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
