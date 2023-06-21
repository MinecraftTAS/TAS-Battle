package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

/**
 * TAS Battle plugin lobby command manager
 * @author Pancake
 */
public class LobbyCommand {

    // plugin configuration
    private String lobbyCommand;
    private String[] lobbyAliases;
    private String lobbyServer;
    private String lobbyErrorMessage;
    private List<String> lobbyEnabledServers;

    /**
     * Initialize Lobby Command
     * @param plugin Plugin
     */
    public LobbyCommand(TASBattleProxy plugin) {
        var server = plugin.getServer();
        var config = plugin.getProperties();

        // load configuration
        this.lobbyCommand = config.getProperty("lobby_command");
        this.lobbyAliases = config.getProperty("lobby_aliases").split("\\,");
        this.lobbyServer = config.getProperty("lobby_server");
        this.lobbyErrorMessage = config.getProperty("lobby_error_message");
        this.lobbyEnabledServers = Arrays.stream(config.getProperty("lobby_enabled_servers").split("\\,")).toList();

        // register lobby command
        server.getCommandManager().register(this.lobbyCommand, (RawCommand) invocation -> {
            if (invocation.source() instanceof Player player && this.lobbyEnabledServers.contains(player.getCurrentServer().get().getServerInfo().getName()))
                player.createConnectionRequest(server.getServer(this.lobbyServer).get()).fireAndForget();
            else
                invocation.source().sendMessage(Component.text(this.lobbyErrorMessage));
        }, this.lobbyAliases);
    }

}
