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
    private static final String TAGS = "faed0946-bb7f-4fcc-bff1-5fb9ef75a066:§4Owner,f3112feb-00c1-4de8-9829-53b940342996:§cCo-Owner,b61f3d56-4ce8-4142-a14b-2021a95134dc:§6TBT #1,574c4f11-d9a1-4552-b338-b3897903cf66:§7TBT #1,7b27458e-d3e0-4279-86db-702230e8b237:§cTBT #1"; // player:tag,player...
    private static final String CAPES = "faed0946-bb7f-4fcc-bff1-5fb9ef75a066:tasbattle/capes/pancape.png,f3112feb-00c1-4de8-9829-53b940342996:tasbattle/capes/scribble.png,b61f3d56-4ce8-4142-a14b-2021a95134dc:tasbattle/capes/taswinner1.png"; // player:url,player...

    /**
     * Initialize Data Manager
     * @param plugin Plugin
     */
    public DataManager(TASBattleProxy plugin) {
        // register events and channel
        var server = plugin.getServer();
        server.getEventManager().register(plugin, this);
        server.getChannelRegistrar().register(TASBATTLE_DATA);
    }

    /**
     * Send tas battle data to client on plugin message
     * @param e Plugin message event
     */
    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {
        if (!TASBATTLE_DATA.getId().equals(e.getIdentifier().getId()))
            return;

        // send tas battle data to client
        var bytes = (TAGS + '\n' + CAPES).getBytes(StandardCharsets.UTF_8);
        var byteBuffer = ByteBuffer.allocate(4 + bytes.length);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        ((Player) e.getSource()).sendPluginMessage(TASBATTLE_DATA, byteBuffer.array());
        e.setResult(PluginMessageEvent.ForwardResult.handled());
    }

}
