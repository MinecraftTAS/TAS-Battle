package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

/**
 * TAS Battle chat system
 * @author Pancake
 */
public class ChatSystem {

    private static final List<String> CHAT_SERVERS = Arrays.asList("lobbypreview", "gameserver01preview");

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
        var server = e.getPlayer().getCurrentServer().get().getServerInfo();
        if (!CHAT_SERVERS.contains(server.getName()))
            return;

        for (var subserver : CHAT_SERVERS)
            this.plugin.getServer().getServer(subserver).get().sendMessage(Component.text("§a" + e.getPlayer().getUsername() + " §b» §f" + e.getMessage()));
    }

}
