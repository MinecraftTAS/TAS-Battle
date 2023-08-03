package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleGameserver;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Custom log file since we already made one for velocity...
 * @author Pancake
 */
public class GameserverTelemetry implements Listener {

    public static final File LOG_DIR = new File("/home/tasbattle/preview/telemetry/games/");
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss", Locale.ENGLISH);

    private FileWriter logWriter;
    private ExecutorService logWriterExecutor;

    /**
     * Initialize lobby telemetry
     * @param plugin Plugin
     */
    public GameserverTelemetry(TASBattleGameserver plugin) throws IOException {
        // register events
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // open log file
        this.logWriter = new FileWriter(new File(LOG_DIR, "game_" + Instant.now().getEpochSecond() + ".log"), StandardCharsets.UTF_8);
        this.logWriter.write(String.format("[%s STARTUP ]: plugins: %s\n", FORMAT.format(Date.from(Instant.now())), Arrays.stream(plugin.getServer().getPluginManager().getPlugins()).map(p -> p.getName()).collect(Collectors.joining(", "))));
        this.logWriter.flush();

        // create executor
        this.logWriterExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Write to the log asynchronously
     * @param text Text to write
     */
    public void write(String text) {
        if (this.logWriterExecutor.isShutdown())
            return;

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
     * Log connections
     * @param e Event
     */
    @EventHandler
    public void onConnect(PlayerJoinEvent e) throws IOException {
        var player = e.getPlayer();
        this.write(String.format("[%s CONNECT ]: %s %s (%s)\n", FORMAT.format(Date.from(Instant.now())), player.getName(), player.getUniqueId(), player.getAddress().getHostString()));
    }

    /**
     * Log chatting
     * @param e Event
     */
    @EventHandler
    public void onChat(AsyncChatEvent e) throws IOException {
        this.write(String.format("[%s CHAT    ]: %s: %s\n", FORMAT.format(Date.from(Instant.now())), e.getPlayer().getName(), ((TextComponent) e.message()).content()));
    }

    /**
     * Log anonymous commands
     * @param e Event
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) throws IOException {
        this.write(String.format("[%s CHAT    ]: %s: %s\n", FORMAT.format(Date.from(Instant.now())), e.getPlayer().getName(), e.getMessage()));
    }

    /**
     * Log disconnections
     * @param e Event
     */
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) throws IOException {
        this.write(String.format("[%s DCONNECT]: %s\n", FORMAT.format(Date.from(Instant.now())), e.getPlayer().getName()));
    }

    /**
     * Log proxy shutdowns
     * @param e Event
     */
    public void onShutdown() throws IOException {
        this.logWriterExecutor.close();

        this.logWriter.write(String.format("[%s SHUTDOWN]:\n", FORMAT.format(Date.from(Instant.now()))));
        this.logWriter.flush();
        this.logWriter.close();
    }

}
