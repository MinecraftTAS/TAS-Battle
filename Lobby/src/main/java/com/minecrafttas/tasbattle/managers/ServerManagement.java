package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class manages the server backend to ensure a gameserver is always available
 * @author Pancake
 */
public class ServerManagement implements Listener {

    private static final String BUNGEE_CHANNEL = "BungeeCord";

    private static final String[] SERVERS = new String[] {
            "gameserver01"
    };

    private volatile TASBattleLobby plugin;
    private volatile String activeServer;

    /**
     * Initialize server management
     * @param plugin Plugin
     */
    public ServerManagement(TASBattleLobby plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                if (this.checkActiveServer())
                    return;

                this.activateServer();
            } catch (Exception e) {
                this.plugin.getSLF4JLogger().error("Unable to check for active server!", e);
            }
        }, 10L, 10L);

        this.plugin = plugin;
    }

    /**
     * Join active server
     */
    public void joinServer(Player p) throws IOException {
        if (this.activeServer == null)
            return;

        var stream = new ByteArrayOutputStream();
        var dataStream = new DataOutputStream(stream);
        dataStream.writeUTF("Connect");
        dataStream.writeUTF(this.activeServer + "preview");
        p.sendPluginMessage(this.plugin, BUNGEE_CHANNEL, stream.toByteArray());
        stream.close();
    }

    /**
     * Sets a server active
     * @param server Server to launch
     * @throws Exception Terminal exception
     */
    public void activateServer() throws Exception {
        if (this.activeServer != null)
            return;

        var server = SERVERS[0];
        Runtime.getRuntime().exec(("systemctl --user start " + server + "-preview").split(" "));
        this.activeServer = server;
        this.plugin.getSLF4JLogger().info("Active server has been changed to {}",  this.activeServer);
    }

    /**
     * Verify the current active server is still active
     * @throws Exception Terminal exception
     */
    public boolean checkActiveServer() throws Exception {
        if (this.activeServer == null)
            return false;

        var status = Runtime.getRuntime().exec(("systemctl --user --quiet is-active " + this.activeServer + "-preview").split(" ")).waitFor();
        if (status != 0) {
            this.activeServer = null;
            return false;
        }

        return true;
    }

}
