package com.minecrafttas.tasbattle.managers;

import com.minecrafttas.tasbattle.TASBattleLobby;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Action and npc managing class
 * @author Pancake
 */
public class EntityManager implements Listener {

    private TASBattleLobby plugin;
    private Slime actionSlime;

    private Location location;
    private UUID uuid;
    private String name;

    /**
     * Initialize entity manager
     * @param plugin Plugin
     */
    public EntityManager(TASBattleLobby plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;

        // try to load configuration
        try {
            var config = new YamlConfiguration();
            config.load(new File(plugin.getDataFolder(), "lobby.yml"));

            // spawn action slime
            var world = Bukkit.getWorld(config.getString("world"));
            this.location = new Location(world, config.getDouble("posX"), config.getDouble("posY") + .5, config.getDouble("posZ"));
            this.uuid = UUID.fromString(config.getString("uuid"));
            this.name = config.getString("name");
            this.actionSlime = (Slime) world.spawnEntity(location, EntityType.SLIME);
            this.actionSlime.customName(Component.text("Action Slime"));
            this.actionSlime.setAI(false);
            this.actionSlime.setInvulnerable(true);
            this.actionSlime.setSize(5);
            this.actionSlime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false));

            // delete previous action slimes
            for (var entity : world.getChunkAt(this.location).getEntities())
                if (entity instanceof Slime && entity != this.actionSlime)
                    entity.remove();

        } catch (Exception e) {
            this.plugin.getSLF4JLogger().error("Unable to read lobby configuration!", e);
            e.printStackTrace();
        }

    }

    /**
     * Trigger server action on interaction
     * @param e Player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) throws IOException {
        var player = e.getPlayer();
        var raytrace = player.rayTraceEntities(3);
        if (raytrace == null || raytrace.getHitEntity() != this.actionSlime)
            return;

        this.plugin.getServerManagement().joinServer(player);
    }

    /**
     * Spawn player npc on join
     * @param e Player join event
     * @throws Exception Reflection exception
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws Exception {
        var paperPlayer = e.getPlayer();

        // get classes for reflection
        var mcserverClass = Class.forName("net.minecraft.server.MinecraftServer");
        var playerListClass = Class.forName("net.minecraft.server.players.PlayerList");
        var gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
        var serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer");
        var playerClass = Class.forName("net.minecraft.world.entity.player.Player");
        var entityClass = Class.forName("net.minecraft.world.entity.Entity");
        var synchedEntityDataClass = Class.forName("net.minecraft.network.syncher.SynchedEntityData");
        var synchedEntityDataDataItemClass = Class.forName("net.minecraft.network.syncher.SynchedEntityData$DataItem");
        var playerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action");
        var playerInfoClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
        var addPlayerClass = Class.forName("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket");
        var changeSkinClass = Class.forName("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket");
        var connectionClass = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl");
        var packetClass = Class.forName("net.minecraft.network.protocol.Packet");

        // var mcserver = MinecraftServer.getServer();
        var mcserver = mcserverClass.getMethod("getServer").invoke(null);
        // var playerList = mcserver.getPlayerList();
        var playerList = mcserverClass.getMethod("getPlayerList").invoke(mcserver);
        // var player = playerList.getPlayer(paperPlayer.getUniqueId());
        var player = playerListClass.getMethod("getPlayer", UUID.class).invoke(playerList, paperPlayer.getUniqueId());
        // var level = player.getLevel();
        var level = entityClass.getMethod("getLevel").invoke(player);
        // var gameProfile = new GameProfile(this.uuid, this.name);
        var gameProfile = gameProfileClass.getConstructors()[0].newInstance(this.uuid, this.name);
        // var fakePlayer = new ServerPlayer(mcserver, (ServerLevel) player.level, profile);
        var fakePlayer = serverPlayerClass.getConstructors()[0].newInstance(mcserver, level, gameProfile);
        // fakePlayer.setPosRaw(this.location.x(), this.location.y(), this.location.z());
        entityClass.getMethod("setPosRaw", double.class, double.class, double.class).invoke(fakePlayer, this.location.x(), this.location.y(), this.location.z());
        // var fakePlayerId = fakePlayer.getId();
        var fakePlayerId = entityClass.getMethod("getId").invoke(fakePlayer);
        // var synchedEntityData = fakePlayer.getEntityData();
        var synchedEntityData = entityClass.getMethod("getEntityData").invoke(fakePlayer);
        // var dataValues = synchedEntityData.getNonDefaultValues();
        var dataValues = synchedEntityDataClass.getMethod("getNonDefaultValues").invoke(synchedEntityData);
        // var DATA_PLAYER_MODE_CUSTOMISATION = Player.DATA_PLAYER_MODE_CUSTOMISATION;
        var DATA_PLAYER_MODE_CUSTOMISATION_field = playerClass.getDeclaredField("DATA_PLAYER_MODE_CUSTOMISATION");
        DATA_PLAYER_MODE_CUSTOMISATION_field.setAccessible(true);
        var DATA_PLAYER_MODE_CUSTOMISATION = DATA_PLAYER_MODE_CUSTOMISATION_field.get(null);
        // var synchedEntityDataDataItem = new DataItem(DATA_PLAYER_MODE_CUSTOMISATION, 0b11111111);
        var synchedEntityDataDataItem = synchedEntityDataDataItemClass.getConstructors()[0].newInstance(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0b11111111);
        // var synchedEntityDataDataValue = synchedEntityDataDataItem.value();
        var synchedEntityDataDataValue = synchedEntityDataDataItemClass.getMethod("value").invoke(synchedEntityDataDataItem);
        // dataValues.add(synchedEntityDataDataValue);
        dataValues.getClass().getMethod("add", Object.class).invoke(dataValues, synchedEntityDataDataValue);
        // var playerInfo = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, fakePlayer);
        var playerInfo = playerInfoClass.getConstructors()[1].newInstance(playerInfoActionClass.getEnumConstants()[0], fakePlayer);
        // var addPlayer = new ClientboundAddPlayerPacket(fakePlayer);
        var addPlayer = addPlayerClass.getConstructors()[0].newInstance(fakePlayer);
        // var changeSkin = new ClientboundSetEntityDataPacket(fakePlayerId, dataValues);
        var changeSkin = changeSkinClass.getConstructors()[1].newInstance(fakePlayerId, dataValues);
        // var connection = player.connection;
        var connection = serverPlayerClass.getField("connection").get(player);
        // connection.send(playerInfo); connection.send(addPlayer); connection.send(changeSkin);
        var connectionSendMethod = connectionClass.getMethod("send", packetClass);
        connectionSendMethod.invoke(connection, playerInfo);
        connectionSendMethod.invoke(connection, addPlayer);
        connectionSendMethod.invoke(connection, changeSkin);

        // turn player around since npcs spawn that way
        var paperPlayerLoc = paperPlayer.getLocation();
        paperPlayerLoc.setYaw(-180.0f);
        paperPlayer.teleport(paperPlayerLoc);
    }

}
