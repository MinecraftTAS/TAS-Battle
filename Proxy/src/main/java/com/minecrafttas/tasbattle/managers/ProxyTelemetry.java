package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleProxy;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Custom log file since velocity's log file is terrible
 * @author Pancake
 */
public class ProxyTelemetry {

    public static final File LOG_FILE = new File("/home/tasbattle/telemetry/proxy.log");
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss", Locale.ENGLISH);

    private FileWriter logWriter;
    private ExecutorService logWriterExecutor;
    private ProxyServer server;

    /**
     * Initialize proxy telemetry
     * @param plugin Plugin
     */
    public ProxyTelemetry(TASBattleProxy plugin) throws IOException {
        // register events and channel
        this.server = plugin.getServer();
        this.server.getEventManager().register(plugin, this);

        // open log file
        this.logWriter = new FileWriter(LOG_FILE, StandardCharsets.UTF_8, true);
        this.logWriter.write(String.format("[%s STARTUP ]: servers: %s, plugins: %s\n", FORMAT.format(Date.from(Instant.now())), server.getAllServers().stream().map(s -> s.getServerInfo().getName()).collect(Collectors.joining(", ")), server.getPluginManager().getPlugins().stream().map(p -> p.getDescription().getId()).collect(Collectors.joining(", "))));
        this.logWriter.flush();

        // create executor
        this.logWriterExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Write to the log asynchronously
     * @param text Text to write
     */
    private void write(String text) {
        this.logWriterExecutor.execute(() -> {
            try {
                this.logWriter.write(text);
                this.logWriter.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Log proxy pings
     * @param e Event
     */
    @Subscribe
    public void onPing(ProxyPingEvent e) throws IOException {
        var vhost = e.getConnection().getVirtualHost();
        this.write(String.format("[%s PING    ]: %s -> %s (%s)\n", FORMAT.format(Date.from(Instant.now())), e.getConnection().getRemoteAddress().getHostString(), vhost.isPresent() ? vhost.get().getHostString() : "unknown", e.getConnection().getProtocolVersion().getVersionIntroducedIn()));
    }

    /**
     * Log connections
     * @param e Event
     */
    @Subscribe
    public void onConnect(LoginEvent e) throws IOException {
        var player = e.getPlayer();
        var vhost = player.getVirtualHost();
        var gameProfile = player.getGameProfile();
        this.write(String.format("[%s CONNECT ]: %s %s (%s -> %s) [%s]\n", FORMAT.format(Date.from(Instant.now())), gameProfile.getName(), gameProfile.getId(), player.getRemoteAddress().getHostString(), vhost.isPresent() ? vhost.get().getHostString() : "unknown", player.getProtocolVersion().getVersionIntroducedIn()));
    }

    /**
     * Log server connections
     * @param e Event
     */
    @Subscribe
    public void onServerConnect(ServerConnectedEvent e) throws IOException {
        var prev = e.getPreviousServer();
        this.write(String.format("[%s SERVER  ]: %s: %s -> %s\n", FORMAT.format(Date.from(Instant.now())), e.getPlayer().getGameProfile().getName(), prev.isPresent() ? prev.get().getServerInfo().getName() : "proxy", e.getServer().getServerInfo().getName()));
    }

    /**
     * Log chatting
     * @param e Event
     */
    @Subscribe
    public void onChat(PlayerChatEvent e) throws IOException {
        this.write(String.format("[%s CHAT    ]: %s: %s\n", FORMAT.format(Date.from(Instant.now())), e.getPlayer().getGameProfile().getName(), e.getMessage()));
    }

    /**
     * Log commands
     * @param e Event
     */
    @Subscribe
    public void onCommand(CommandExecuteEvent e) throws IOException {
        if (e.getCommandSource() instanceof Player player)
            this.write(String.format("[%s CHAT    ]: %s: /%s\n", FORMAT.format(Date.from(Instant.now())), player.getGameProfile().getName(), e.getCommand()));
    }

    /**
     * Log disconnections
     * @param e Event
     */
    @Subscribe
    public void onDisconnect(DisconnectEvent e) throws IOException {
        var player = e.getPlayer();
        var vhost = player.getVirtualHost();
        var gameProfile = player.getGameProfile();
        this.write(String.format("[%s DCONNECT]: %s, %s\n", FORMAT.format(Date.from(Instant.now())), gameProfile.getName(), e.getLoginStatus().name()));
    }

    /**
     * Log proxy shutdowns
     * @param e Event
     */
    @Subscribe
    public void onShutdown(ProxyShutdownEvent e) throws IOException {
        this.logWriterExecutor.close();

        this.logWriter.write(String.format("[%s SHUTDOWN]:\n", FORMAT.format(Date.from(Instant.now()))));
        this.logWriter.flush();
        this.logWriter.close();
    }

}
