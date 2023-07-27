package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

/**
 * TAS Battle plugin lobby command manager
 * @author Pancake
 */
public class LobbyCommand {

    public static final List<String> ALLOWED_SERVERS = List.of("ffa", "bedwars", "skywars", "juggernaut", "knockffa", "cores", "survivalgames", "speeduhc");

    /**
     * Initialize Lobby Command
     * @param plugin Plugin
     */
    public LobbyCommand(TASBattleProxy plugin) {
        var server = plugin.getServer();

        // register command
        var lobby = server.getServer("lobby").get();
        server.getCommandManager().register("lobby", (RawCommand) invocation -> {
            if (invocation.source() instanceof Player player && ALLOWED_SERVERS.contains(player.getCurrentServer().get().getServerInfo().getName()))
                player.createConnectionRequest(lobby).fireAndForget();
            else
                invocation.source().sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot use that here.</red>"));
        }, new String[] {"l", "spawn", "leave", "hub"});
    }

}
