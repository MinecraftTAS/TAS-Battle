package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * TAS Battle plugin data manager
 * @author Pancake
 */
public class DataManager {

    public static final ChannelIdentifier TASBATTLE_DATA = MinecraftChannelIdentifier.create("tasbattle", "data");

    private String tags; // player:tag,player...
    private String capes; // player:url,player...

    /**
     * Initialize Data Manager
     * @param plugin Plugin
     */
    public DataManager(TASBattleProxy plugin) {
        var server = plugin.getServer();
        var config = plugin.getProperties();

        // register events and channel
        server.getEventManager().register(plugin, this);
        server.getChannelRegistrar().register(TASBATTLE_DATA);

        // load configuration
        this.tags = config.getProperty("tags");
        this.capes = config.getProperty("capes");
    }

    /**
     * Send tas battle data to client on plugin message
     * @param e Plugin message event
     */
    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        if (!TASBATTLE_DATA.getId().equals(e.getIdentifier().getId()) || this.tags == null || this.capes == null)
            return;

        // send tas battle data to client
        var bytes = (this.tags + '\n' + this.capes).getBytes(StandardCharsets.UTF_8);
        var byteBuffer = ByteBuffer.allocate(4 + bytes.length);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        ((Player) e.getSource()).sendPluginMessage(TASBATTLE_DATA, byteBuffer.array());
        e.setResult(PluginMessageEvent.ForwardResult.handled());
    }

}
