package com.minecrafttas.tasbattle;

import com.google.inject.Inject;
import com.minecrafttas.tasbattle.managers.ChatSystem;
import com.minecrafttas.tasbattle.managers.DataManager;
import com.minecrafttas.tasbattle.managers.LobbyCommand;
import com.minecrafttas.tasbattle.managers.PermissionManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/**
 * Proxy plugin class
 * @author Pancake
 */
@Plugin(name = "TAS-Battle-Proxy", version = "1.0.0-SNAPSHOT", id = "proxy", authors = { "Pancake" },
		url = "https://github.com/MinecraftTAS/TAS-Battle", description = "tas battle proxy plugin")
	@Getter
public class TASBattleProxy {

	private final ProxyServer server;
	private final Properties properties;
	private DataManager dataManager;
	private LobbyCommand lobbyCommand;
	private PermissionManager permissionManager;
	private ChatSystem chatSystem;

	/**
     * Construct proxy plugin
     * @param server Proxy server instance
     * @throws IOException Configuration could not be loaded
     */
	@Inject
	public TASBattleProxy(ProxyServer server, @DataDirectory Path dataDirectory) throws IOException {
		this.server = server;
		this.properties = new Properties();

		// load configuration or regenerate defaults
		var configFile = dataDirectory.resolve("config.xml");
		if (Files.exists(configFile)) {
			// load all properties
			properties.loadFromXML(Files.newInputStream(configFile));
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
		
	}

	/**
	 * Initialize proxy plugin
	 * @param e Proxy initialization event
	 */
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent e) {
		// initialize managers
		this.dataManager = new DataManager(this);
		this.lobbyCommand = new LobbyCommand(this);
		this.permissionManager = new PermissionManager(this);
		this.chatSystem = new ChatSystem(this);
	}

}
