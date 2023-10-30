package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.Arrays;
import java.util.List;

/**
 * TAS Battle chat system
 * @author Pancake
 */
public class ChatSystem {

    private static final List<String> CHAT_SERVERS = Arrays.asList("lobby", "gameserver01");

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
            this.plugin.getServer().getServer(subserver).ifPresent(s ->
                s.sendMessage(MiniMessage.miniMessage().deserialize("<green><username></green> <aqua>»</aqua> <white><text></white>", Placeholder.unparsed("username", e.getPlayer().getUsername()), Placeholder.unparsed("text", e.getMessage()))));
    }

}
