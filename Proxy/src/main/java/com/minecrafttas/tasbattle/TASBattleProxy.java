package com.minecrafttas.tasbattle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.Inject;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import net.kyori.adventure.text.Component;

/**
 * Proxy plugin class
 * @author Pancake
 */
@Plugin(name = "TAS-Battle-Proxy", version = "1.0.0-SNAPSHOT", id = "proxy", authors = { "Pancake" },
		url = "https://github.com/MinecraftTAS/TAS-Battle", description = "tas battle proxy plugin")
public class TASBattleProxy {

	public static final ChannelIdentifier TASBATTLE_DATA = MinecraftChannelIdentifier.create("tasbattle", "data");

	// proxy instance
	private final ProxyServer server;
	
	// plugin configuration
	private String lobbyCommand;
	private String[] lobbyAliases;
	private String lobbyServer;
	private String lobbyErrorMessage;
	private List<String> lobbyEnabledServers;
	private List<UUID> admins;
	private String tags; // player:tag,player...
	private String capes; // player:url,player...
    
	// permission provider
	private PermissionProvider permissionProvider;
	
    /**
     * Construct proxy plugin
     * @param server Proxy server instance
     * @throws IOException Configuration could not be loaded
     */
	@Inject
	public TASBattleProxy(ProxyServer server, @DataDirectory Path dataDirectory) throws IOException {
		this.server = server;
		
		// load configuration or regenerate defaults
		var properties = new Properties();
		var configFile = dataDirectory.resolve("config.xml");
		if (Files.exists(configFile)) {
			// load all properties
			properties.loadFromXML(Files.newInputStream(configFile));
			this.lobbyCommand = properties.getProperty("lobby_command");
			this.lobbyAliases = properties.getProperty("lobby_aliases").split("\\,");
			this.lobbyServer = properties.getProperty("lobby_server");
			this.lobbyErrorMessage = properties.getProperty("lobby_error_message");
			this.lobbyEnabledServers = Arrays.stream(properties.getProperty("lobby_enabled_servers").split("\\,")).toList();
			this.admins = Arrays.stream(properties.getProperty("admin_uuids").split("\\,")).map(s -> UUID.fromString(s)).toList();
			this.tags = properties.getProperty("tags");
			this.capes = properties.getProperty("capes");
		} else {
			// set default properties and save
			Files.createDirectories(dataDirectory);
			properties.setProperty("lobby_command", "");
			properties.setProperty("lobby_aliases", "");
			properties.setProperty("lobby_server", "");
			properties.setProperty("lobby_error_message", "");
			properties.setProperty("lobby_enabled_servers", "");
			properties.setProperty("admin_uuids", "");
			properties.setProperty("tags", "");
			properties.setProperty("capes", "");
			properties.storeToXML(Files.newOutputStream(configFile, StandardOpenOption.CREATE), null);
			throw new IOException("Configuration is not set up.");
		}
		
		// create permission function
		this.permissionProvider = subject -> {
			if ((subject instanceof Player player && this.admins.contains(player.getUniqueId())) || subject instanceof ConsoleCommandSource) // grant all permissions to admins and the console
				return PermissionFunction.ALWAYS_TRUE;
			
			return PermissionFunction.ALWAYS_FALSE;
		};
		
	}

	/**
	 * Initialize proxy plugin
	 * @param e Proxy initialization event
	 */
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent e) {
		// update permission on setup
		this.server.getEventManager().register(this, PermissionsSetupEvent.class, event -> event.setProvider(this.permissionProvider));
		
		// register lobby command
		this.server.getCommandManager().register(this.lobbyCommand, (RawCommand) invocation -> {
			
			if (invocation.source() instanceof Player player && this.lobbyEnabledServers.contains(player.getCurrentServer().get().getServerInfo().getName()))
				player.createConnectionRequest(this.server.getServer(this.lobbyServer).get()).fireAndForget();
			else
				invocation.source().sendMessage(Component.text(this.lobbyErrorMessage));
			
		}, this.lobbyAliases);
		
		// register plugin message
		this.server.getChannelRegistrar().register(TASBATTLE_DATA);
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
		e.setResult(ForwardResult.handled());
	}
	
}
