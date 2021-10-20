package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        if (AdvancedLobby.cfg.getBoolean("motd.enabled")) {
            String s = AdvancedLobby.cfg.getString("motd.first_line") + "\n" + AdvancedLobby.cfg.getString("motd.second_line");
            e.setMotd(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

}
