package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Custom tab list and actionbar
 * @author Pancake
 */
public class CustomTabList {

    private static final List<String> TASBATTLE_SERVERS = List.of("lobby", "gameserver01");

    /**
     * Initialize custom tab list
     * @param plugin Plugin
     */
    public CustomTabList(TASBattleProxy plugin) {
        var server = plugin.getServer();
        server.getEventManager().register(plugin, this);
        server.getScheduler().buildTask(plugin, () -> {
            server.getServer("lobby").ifPresent(c -> {
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
    public void onPlayerJoin(ServerPostConnectEvent e) {
        var player = e.getPlayer();
        var server = player.getCurrentServer().get().getServerInfo();
        if (TASBATTLE_SERVERS.contains(server.getName())) {
            player.sendPlayerListHeaderAndFooter(
                    MiniMessage.miniMessage().deserialize("<bold><red>TAS</red><gold>Battle</gold></bold>"),
                    MiniMessage.miniMessage().deserialize("<gray>Official TAS Battle Minecraft Server</gray>")
            );
        }
    }

}
