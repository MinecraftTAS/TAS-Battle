package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.kyori.adventure.text.Component;

/**
 * TAS Battle chat system
 * @author Pancake
 */
public class ChatSystem {

    private TASBattleProxy plugin;

    /**
     * Initialize Chat System
     * @param plugin Plugin
     */
    public ChatSystem(TASBattleProxy plugin) {
        this.plugin = plugin;

        // register events and channel
        var server = plugin.getServer();
        server.getEventManager().register(plugin, this);
    }

    /**
     * Resend chat message as global message
     * @param e Player chat event
     */
    @Subscribe
    public void onChat(PlayerChatEvent e) {
        this.plugin.getServer().sendMessage(Component.text("§a" + e.getPlayer().getUsername() + " §b» §f" + e.getMessage()));
    }

}
