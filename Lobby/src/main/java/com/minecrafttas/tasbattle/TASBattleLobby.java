package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.managers.DimensionChanger;
import com.minecrafttas.tasbattle.managers.TickrateChanger;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TASBattleLobby extends JavaPlugin implements Listener {

	@Getter
	private TickrateChanger tickrateChanger;

	@Getter
	private DimensionChanger dimensionChanger;

	private Location location;
	private UUID uuid;
	private String name;

	@Getter
	private BasicRestrictions basicRestrictions;

	/**
	 * Enable tasbattle lobby mod
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		this.tickrateChanger = new TickrateChanger(this);
		this.dimensionChanger = new DimensionChanger(this);

		this.basicRestrictions = new BasicRestrictions(this);
		var config = new YamlConfiguration();
		try {
			config.load(new File(this.getDataFolder(), "lobby.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load lobby configuration");
			e.printStackTrace();
		}

		// delete previous action slimes
		var world = Bukkit.getWorld(config.getString("world"));
		world.getEntitiesByClass(Slime.class).forEach(e -> e.remove());

		// spawn action slime
		this.location = new Location(world, config.getDouble("posX"), config.getDouble("posY") + .5, config.getDouble("posZ"));
		this.uuid = UUID.fromString(config.getString("uuid"));
		this.name = config.getString("name");
		this.actionSlime = (Slime) world.spawnEntity(location, EntityType.SLIME);
		this.actionSlime.customName(Component.text("Action Slime"));
		this.actionSlime.setAI(false);
		this.actionSlime.setInvulnerable(true);
		this.actionSlime.setSize(5);
		this.actionSlime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		var raytrace = e.getPlayer().rayTraceEntities(3);
		if (raytrace == null || raytrace.getHitEntity() != this.actionSlime)
			return;

		Bukkit.broadcast(Component.text("i win!"));
	}

	/**
	 * Remove join message on player join
	 * @param e Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) throws Exception {
		e.joinMessage(null);

		/* Spawn player npc */

		// get classes for reflection
		var mcserverClass = Class.forName("net.minecraft.server.MinecraftServer");
		var playerListClass = Class.forName("net.minecraft.server.players.PlayerList");
		var gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
		var serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer");
		var entityClass = Class.forName("net.minecraft.world.entity.Entity");
		var playerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action");
		var playerInfoClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
		var addPlayerClass = Class.forName("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket");
		var connectionClass = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl");
		var packetClass = Class.forName("net.minecraft.network.protocol.Packet");

		// var mcserver = MinecraftServer.getServer();
		var mcserver = mcserverClass.getMethod("getServer").invoke(null);
		// var playerList = mcserver.getPlayerList();
		var playerList = mcserverClass.getMethod("getPlayerList").invoke(mcserver);
		// var player = playerList.getPlayer(e.getPlayer.getUniqueId());
		var player = playerListClass.getMethod("getPlayer", UUID.class).invoke(playerList, e.getPlayer().getUniqueId());
		// var level = player.getLevel();
		var level = entityClass.getMethod("getLevel").invoke(player);
		// var gameProfile = new GameProfile(this.uuid, this.name);
		var gameProfile = gameProfileClass.getConstructors()[0].newInstance(this.uuid, this.name);
		// var fakePlayer = new ServerPlayer(mcserver, (ServerLevel) player.level, profile);
		var fakePlayer = serverPlayerClass.getConstructors()[0].newInstance(mcserver, level, gameProfile);
		// fakePlayer.setPosRaw(this.location.x(), this.location.y(), this.location.z());
		entityClass.getMethod("setPosRaw", double.class, double.class, double.class).invoke(fakePlayer, this.location.x(), this.location.y(), this.location.z());
		// var playerInfo = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, fakePlayer);
		var playerInfo = playerInfoClass.getConstructors()[1].newInstance(playerInfoActionClass.getEnumConstants()[0], fakePlayer);
		// var addPlayer = new ClientboundAddPlayerPacket(fakePlayer);
		var addPlayer = addPlayerClass.getConstructors()[0].newInstance(fakePlayer);
		// var connection = player.connection;
		var connection = serverPlayerClass.getField("connection").get(player);
		// connection.send(playerInfo); connection.send(addPlayer);
		var connectionSendMethod = connectionClass.getMethod("send", packetClass);
		connectionSendMethod.invoke(connection, playerInfo);
		connectionSendMethod.invoke(connection, addPlayer);
	}

	/**
	 * Remove quit message on player quit
	 * @param e Player quit event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.quitMessage(null);
	}

}
