package com.minecrafttas.tasbattle.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minecrafttas.tasbattle.stats.structs.PlayerData;
import com.minecrafttas.tasbattle.stats.structs.Stats;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Stats manager for managing stats in all gamemodes
 */
public class StatsManager {

    private static final File STATS = new File("/home/tasbattle/preview/stats/");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Path storage;
    private String mode;
    private Stats cachedStats;

    /**
     * Initialize stats manager
     * @param mode
     * @throws IOException
     */
    public StatsManager(String mode) throws IOException {
        this.storage = new File(STATS, mode + ".json").toPath();
        if (Files.exists(this.storage))
            this.cachedStats = GSON.fromJson(Files.readString(this.storage, StandardCharsets.UTF_8), Stats.class);
        else
            this.cachedStats = new Stats(this.mode);
    }

    /**
     * Load, edit and save the player stats
     * @param action Edit action
     * @throws IOException Filesystem exception
     */
    public void editStats(Consumer<Stats> action) throws IOException {
        var stats = new Stats(this.mode);
        if (Files.exists(this.storage))
            stats = GSON.fromJson(Files.readString(this.storage, StandardCharsets.UTF_8), Stats.class);

        action.accept(stats);

        stats.getPlayers().sort(Comparator.comparingInt(PlayerData::calculateIndex));
        Files.writeString(this.storage, GSON.toJson(stats), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);

        this.cachedStats = stats;
    }

    /**
     * Get player data from the stats cache
     * @param id Player ID
     * @return Player data
     */
    public PlayerData getPlayerStats(UUID id) {
        var data = new PlayerData(id);
        for (var player : this.cachedStats.getPlayers()) {
            if (player.getId().equals(id)) {
                data = player;
                break;
            }
        }
        return data;
    }

    /**
     * Return top 10 players names from the cached stats
     */
    public String[] getLeaderboard() {
        return this.cachedStats.getPlayers().subList(0, 10).stream().map(PlayerData::getUsername).toArray(String[]::new);
    }

}
