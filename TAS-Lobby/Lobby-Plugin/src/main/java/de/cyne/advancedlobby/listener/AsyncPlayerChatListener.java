package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");

        for (Player silentPlayers : AdvancedLobby.silentLobby) {
            e.getRecipients().remove(silentPlayers);
        }

        if (AdvancedLobby.silentLobby.contains(p)) {
            e.setCancelled(true);
            p.sendMessage(Locale.SILENTLOBBY_CHAT_BLOCKED.getMessage(p));
        }

        if (AdvancedLobby.globalMute) {
            if (!p.hasPermission("advancedlobby.globalmute.bypass")) {
                e.setCancelled(true);
                p.sendMessage(Locale.GLOBALMUTE_CHAT_BLOCKED.getMessage(p));
            }
        }

        if (AdvancedLobby.cfg.getBoolean("chat_format.enabled")) {
            if (p.hasPermission("advancedlobby.chatcolor")) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }
            e.setFormat(AdvancedLobby.getString("chat_format.format").replace("%player%", AdvancedLobby.getName(p)).replace("%message%", message));
        }

    }

}
