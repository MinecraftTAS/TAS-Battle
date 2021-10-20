package de.cyne.advancedlobby.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        String[] lines = e.getLines();
        if (p.hasPermission("advancedlobby.admin")) {
            for (int i = 0; i <= 3; i++) {
                e.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
            }
        }
    }

}
