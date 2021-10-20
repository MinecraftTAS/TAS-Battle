package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import de.cyne.advancedlobby.locale.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage().split(" ")[0];

        HelpTopic helpTopic = Bukkit.getHelpMap().getHelpTopic(message);

        if (helpTopic == null) {
            e.setCancelled(true);
            p.sendMessage(Locale.UNKNOWN_COMMAND.getMessage(p).replace("%command%", message));
            return;
        }

        if (AdvancedLobby.cfg.getStringList("block_commands.commands").contains(message) && !p.hasPermission("advancedlobby.admin")) {
            if (AdvancedLobby.cfg.getBoolean("block_commands.enabled")) {
                e.setCancelled(true);
                p.sendMessage(Locale.NO_PERMISSION.getMessage(p));
                return;
            }
        }

    }

}
