package com.minecrafttas.tasbattle.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minecrafttas.tasbattle.TASBattleGameserver;
import com.minecrafttas.tasbattle.stats.structs.PlayerData;
import com.minecrafttas.tasbattle.stats.structs.Stats;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Stats manager for managing stats in all gamemodes
 */
public class StatsManager implements CommandExecutor {

    private static final File STATS = new File("/home/tasbattle/stats/");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private volatile Stats cachedStats;
    private final TASBattleGameserver plugin;
    private final Path storage;
    @Getter
    private final String mode;

    /**
     * Initialize stats manager
     * @param mode Gamemode
     * @throws IOException Filesystem exception
     */
    public StatsManager(TASBattleGameserver plugin, String mode) throws IOException {
        this.plugin = plugin;
        this.storage = new File(STATS, mode + ".json").toPath();
        this.mode = mode;
        if (Files.exists(this.storage))
            this.cachedStats = GSON.fromJson(Files.readString(this.storage, StandardCharsets.UTF_8), Stats.class);
        else
            this.cachedStats = new Stats(this.mode);

        plugin.getCommand("stats").setExecutor(this);
    }

    /**
     * Asynchronously load, edit and save the player stats
     * @param action Edit action
     */
    public void editStats(Consumer<Stats> action) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                var stats = new Stats(this.mode);
                if (Files.exists(this.storage))
                    stats = GSON.fromJson(Files.readString(this.storage, StandardCharsets.UTF_8), Stats.class);

                action.accept(stats);

                stats.getPlayers().sort(Comparator.comparingInt(PlayerData::calculateIndex).reversed());

                // empty the file first
                try (var pw = new PrintWriter(this.storage.toFile())) {
                    pw.write("");
                }

                // then write it - shouldn't be necessary but for some reason it doesn't work otherwise
                Files.writeString(this.storage, GSON.toJson(stats), StandardCharsets.UTF_8);

                this.cachedStats = stats;
            } catch (IOException e) {
                this.plugin.getSLF4JLogger().error("Unable to save stats!", e);
            }
        });
    }

    /**
     * Get player data from a stats instance
     * @param stats Stats instance
     * @param id Player ID
     * @return Player data
     */
    public PlayerData getPlayerStats(Stats stats, UUID id) {
        for (var player : stats.getPlayers())
            if (player.getId().equals(id))
                return player;
        var data = new PlayerData(id);
        stats.getPlayers().add(data);
        return data;
    }

    /**
     * Return top 10 players names from the cached stats
     */
    public String[] getLeaderboard() {
        var players = this.cachedStats.getPlayers();
        return players.subList(0, Math.min(10, players.size())).stream().map(PlayerData::getUsername).toArray(String[]::new);
    }

    /**
     * Handle /stats command
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return true;

        if (args.length == 0) {
            var stats = this.getPlayerStats(this.cachedStats, ((Player) sender).getUniqueId());
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<aqua>»</aqua> <white>Showing stats for</white> <aqua><player></aqua><newline>" +
                    "<aqua>»</aqua> <white>Rank:</white> <green><prank></green><newline>" +
                    "<aqua>»</aqua> <white>Kills:</white> <green><pkills></green><newline>" +
                    "<aqua>»</aqua> <white>Deaths:</white> <green><pdeaths></green><newline>" +
                    "<aqua>»</aqua> <white>KDR:</white> <green><pkdr></green><newline>" +
                    "<aqua>»</aqua> <white>Wins:</white> <green><pwins></green><newline>" +
                    "<aqua>»</aqua> <white>Losses:</white> <green><plosses></green><newline>" +
                    "<aqua>»</aqua> <white>WLR</white> <green><pwlr></green><newline>"
            ,
                    Placeholder.unparsed("player", "" + stats.getUsername()),
                    Placeholder.unparsed("prank", "" + (this.cachedStats.getPlayers().indexOf(stats) + 1)),
                    Placeholder.unparsed("pkills", "" + stats.getKills()),
                    Placeholder.unparsed("pdeaths", "" + stats.getDeaths()),
                    Placeholder.unparsed("pkdr", "" + (int) (stats.getKills() / Math.max(1.0D, stats.getDeaths()) * 100.0D) / 100.0D),
                    Placeholder.unparsed("pwins", "" + stats.getWins()),
                    Placeholder.unparsed("plosses", "" + stats.getLosses()),
                    Placeholder.unparsed("pwlr", "" + (int) (stats.getWins() / Math.max(1.0D, stats.getLosses()) * 100.0D) / 100.0D))
            );
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>»</aqua> <white>Looking up stats for <aqua><player></aqua>...</white>", Placeholder.unparsed("player", args[0])));
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                var stats = this.getPlayerStats(this.cachedStats, Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<aqua>»</aqua> <white>Showing stats for</white> <aqua><player></aqua><newline>" +
                                "<aqua>»</aqua> <white>Rank:</white> <green><prank></green><newline>" +
                                "<aqua>»</aqua> <white>Kills:</white> <green><pkills></green><newline>" +
                                "<aqua>»</aqua> <white>Deaths:</white> <green><pdeaths></green><newline>" +
                                "<aqua>»</aqua> <white>KDR:</white> <green><pkdr></green><newline>" +
                                "<aqua>»</aqua> <white>Wins:</white> <green><pwins></green><newline>" +
                                "<aqua>»</aqua> <white>Losses:</white> <green><plosses></green><newline>" +
                                "<aqua>»</aqua> <white>WLR</white> <green><pwlr></green><newline>"
                        ,
                                Placeholder.unparsed("player", "" + stats.getUsername()),
                                Placeholder.unparsed("prank", "" + (this.cachedStats.getPlayers().indexOf(stats) + 1)),
                                Placeholder.unparsed("pkills", "" + stats.getKills()),
                                Placeholder.unparsed("pdeaths", "" + stats.getDeaths()),
                                Placeholder.unparsed("pkdr", "" + (int) (stats.getKills() / Math.max(1.0D, stats.getDeaths()) * 100.0D) / 100.0D),
                                Placeholder.unparsed("pwins", "" + stats.getWins()),
                                Placeholder.unparsed("plosses", "" + stats.getLosses()),
                                Placeholder.unparsed("pwlr", "" + (int) (stats.getWins() / Math.max(1.0D, stats.getLosses()) * 100.0D) / 100.0D))
                );
            });
        }



        return true;
    }
}
