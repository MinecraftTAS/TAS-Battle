package de.cyne.advancedlobby;

import de.cyne.advancedlobby.commands.*;
import de.cyne.advancedlobby.cosmetics.Cosmetics;
import de.cyne.advancedlobby.listener.*;
import de.cyne.advancedlobby.metrics.Metrics;
import de.cyne.advancedlobby.misc.ActionbarScheduler;
import de.cyne.advancedlobby.misc.HiderType;
import de.cyne.advancedlobby.misc.Updater;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdvancedLobby extends JavaPlugin {

    private static AdvancedLobby instance;

    public static File file = new File("plugins/AdvancedLobby", "config.yml");
    public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static File fileLocations = new File("plugins/AdvancedLobby", "locations.yml");
    public static FileConfiguration cfgL = YamlConfiguration.loadConfiguration(fileLocations);

    public static File fileMessages = new File("plugins/AdvancedLobby", "messages.yml");
    public static FileConfiguration cfgM = YamlConfiguration.loadConfiguration(fileMessages);

    public static File fileSounds = new File("plugins/AdvancedLobby", "sounds.yml");
    public static FileConfiguration cfgS = YamlConfiguration.loadConfiguration(fileSounds);

    public static ArrayList<String> actionbarMessages = new ArrayList<>();
    public static ArrayList<Player> build = new ArrayList<>();
    public static ArrayList<Player> fly = new ArrayList<>();
    public static ArrayList<Player> shield = new ArrayList<>();
    public static ArrayList<Player> silentLobby = new ArrayList<>();
    public static HashMap<Player, ItemStack[]> buildInventory = new HashMap<>();
    public static HashMap<Player, HiderType> playerHider = new HashMap<>();

    public static boolean globalMute = false;
    public static boolean updateAvailable = false;
    public static boolean placeholderApi = false;

    public static boolean multiWorld_mode;

    public static ArrayList<World> lobbyWorlds = new ArrayList<>();

    public static ActionbarScheduler scheduler;
    public static Updater updater;

    public static HashMap<String, ErrorType> errors = new HashMap<>();

    public Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tickratechanger:data");
        this.createFiles();
        this.loadFiles();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            AdvancedLobby.getInstance().log("PlaceholderAPI was found. Connected.");
            placeholderApi = true;
        }

        updater = new Updater(35799);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AdvancedLobby.getInstance(), () -> {
            updater.run();
        }, 0L, 20 * 60 * 60 * 24); //once a day

        if (cfg.getBoolean("actionbar.enabled")) {
            actionbarMessages.addAll(AdvancedLobby.cfg.getStringList("actionbar.messages"));
            scheduler = new ActionbarScheduler(actionbarMessages);
            scheduler.start();
        }

        multiWorld_mode = AdvancedLobby.cfg.getBoolean("multiworld_mode.enabled");

        for (World world : Bukkit.getWorlds()) {
            if (AdvancedLobby.cfg.getStringList("lobby_worlds").contains(world.getName())) {
                lobbyWorlds.add(world);
            }
        }

        this.prepareLobbyWorlds();

        this.registerCommands();
        this.registerListener();

        Cosmetics.startBalloonTask();

        metrics = new Metrics(AdvancedLobby.getInstance(), 7014);
        metrics.addCustomChart(new Metrics.SimplePie("singleworld_mode", () -> multiWorld_mode ? "enabled" : "disabled"));
    }

    @Override
    public void onDisable() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (Cosmetics.balloons.containsKey(players)) {
                Cosmetics.balloons.get(players).remove();
            }
        }
    }

    private void prepareLobbyWorlds() {
        for (World world : multiWorld_mode ? AdvancedLobby.lobbyWorlds : Bukkit.getWorlds()) {
            String weatherType = AdvancedLobby.cfg.getString("weather.weather_type").toUpperCase();
            switch (weatherType) {
                case ("CLEAR"):
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case ("RAIN"):
                    world.setStorm(true);
                    world.setThundering(false);
                    break;
                case ("THUNDER"):
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
            }
        }
    }

    private void registerCommands() {
        AdvancedLobby.getInstance().getCommand("advancedlobby").setExecutor(new AdvancedLobbyCommand());
        AdvancedLobby.getInstance().getCommand("build").setExecutor(new BuildCommand());
        AdvancedLobby.getInstance().getCommand("chatclear").setExecutor(new ChatClearCommand());
        AdvancedLobby.getInstance().getCommand("fly").setExecutor(new FlyCommand());
        AdvancedLobby.getInstance().getCommand("gamemode").setExecutor(new GameModeCommand());
        AdvancedLobby.getInstance().getCommand("globalmute").setExecutor(new GlobalMuteCommand());
        if (multiWorld_mode) {
            AdvancedLobby.getInstance().getCommand("lobby").setExecutor(new LobbyCommand());
        }
        AdvancedLobby.getInstance().getCommand("teleportall").setExecutor(new TeleportAllCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new HangingBreakByEntityListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new LeavesDecayListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerArmorStandManipulateListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerBucketEmptyListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerBucketFillListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorldListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerFishListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemConsumeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemDamageListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new DoubleJumListener(), AdvancedLobby.getInstance());
        if (!isOneEightVersion()) {
            Bukkit.getPluginManager().registerEvents(new PlayerSwapHandItemsListener(), AdvancedLobby.getInstance());
        }
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerUnleashEntityListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new ServerListPingListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), AdvancedLobby.getInstance());
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(), AdvancedLobby.getInstance());
    }

    public void createFiles() {
        if (!AdvancedLobby.fileLocations.exists() | !AdvancedLobby.fileMessages.exists() | !AdvancedLobby.fileSounds.exists()) {
            AdvancedLobby.getInstance().getLogger().info("One or more files were not found. Creating..");
            if (!AdvancedLobby.fileLocations.exists()) {
                AdvancedLobby.fileLocations.getParentFile().mkdirs();
                try {
                    fileLocations.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!AdvancedLobby.fileMessages.exists()) {
                AdvancedLobby.fileMessages.getParentFile().mkdirs();
                AdvancedLobby.getInstance().saveResource("messages.yml", false);
            }
            if (!AdvancedLobby.fileSounds.exists()) {
                AdvancedLobby.fileMessages.getParentFile().mkdirs();
                AdvancedLobby.getInstance().saveResource("sounds.yml", false);
            }
        }
    }

    public void loadFiles() {
        try {
            AdvancedLobby.getInstance().getLogger().info("Loading files..");
            AdvancedLobby.cfg.load(AdvancedLobby.file);
            AdvancedLobby.cfgL.load(AdvancedLobby.fileLocations);
            AdvancedLobby.cfgM.load(AdvancedLobby.fileMessages);
            AdvancedLobby.cfgS.load(AdvancedLobby.fileSounds);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(File file, FileConfiguration cfg) {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String GO_BACK_SKULL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6L"
            + "y90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzY"
            + "jJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";

    public static void playSound(Player player, Location location, String path) {
        try {
            if (AdvancedLobby.cfgS.getBoolean(path + ".enabled")) {
                player.playSound(location,
                        Sound.valueOf(AdvancedLobby.cfgS.getString(path + ".sound")),
                        AdvancedLobby.cfgS.getInt(path + ".volume"),
                        AdvancedLobby.cfgS.getInt(path + ".pitch"));
            }
        } catch (Exception ex) {
            AdvancedLobby.errors.put(path, ErrorType.SOUND);
        }
    }

    public static Material getMaterial(String materialString) {
        try {
            Material material = Material.getMaterial(AdvancedLobby.cfg.getString(materialString));
            if (material == null) {
                AdvancedLobby.errors.put(materialString, ErrorType.MATERIAL);
                return Material.BARRIER;
            }
            return material;
        } catch (Exception ex) {
            AdvancedLobby.errors.put(materialString, ErrorType.MATERIAL);
            return Material.BARRIER;
        }
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static boolean isLegacyVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) <= 12;
    }

    public static boolean isOneEightVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) == 8;
    }

    public static String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', AdvancedLobby.cfg.getString(path));
    }

    public static String getPlaceholderString(Player player, String path) {
    	return "";
//        return PlaceholderAPI.setPlaceholders(player, AdvancedLobby.cfg.getString(path).replace("&", "§"));
    }

    public static int getInt(String path) {
        return AdvancedLobby.cfg.getInt(path);
    }

    public static String getName(Player player) {
        return AdvancedLobby.cfg.getBoolean("use_displaynames") ? player.getDisplayName() : player.getName();
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getDescription().getName() + "] " + message);
    }

    public enum ErrorType {
        SOUND, MATERIAL
    }

    public static AdvancedLobby getInstance() {
        return instance;
    }

}