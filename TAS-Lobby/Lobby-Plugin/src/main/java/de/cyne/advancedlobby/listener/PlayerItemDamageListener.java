package de.cyne.advancedlobby.listener;

import de.cyne.advancedlobby.AdvancedLobby;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class PlayerItemDamageListener implements Listener {

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        if (e.getItem().getType() == Material.FISHING_ROD && e.getItem().hasItemMeta() && ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals(ChatColor.stripColor(AdvancedLobby.getString("hotbar_items.gadget.equipped.displayname").replace("%gadget%", AdvancedLobby.getString("inventories.cosmetics_gadgets.grappling_hook_gadget.displayname"))))) {
            e.setCancelled(true);
        }
    }

}