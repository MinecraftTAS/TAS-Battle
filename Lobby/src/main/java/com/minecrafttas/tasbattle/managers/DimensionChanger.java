package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

/**
 * Dimension changer module
 * @author Pancake
 */
public class DimensionChanger implements PluginMessageListener {

    private static final String DC_CHANNEL = "dimensionchanger:data";

    private TASBattleLobby plugin;

    /**
     * Initialize dimension changer module
     * @param plugin Plugin
     */
    public DimensionChanger(TASBattleLobby plugin) {
        this.plugin = plugin;

        // register plugin channels
        var messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, DC_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, DC_CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, Player player, byte[] message) {
        player.sendPluginMessage(this.plugin, DC_CHANNEL, new byte[0]);
    }

}
