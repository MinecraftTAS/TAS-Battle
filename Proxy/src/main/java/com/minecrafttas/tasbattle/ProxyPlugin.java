package com.minecrafttas.tasbattle;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Proxy plugin class
 * @author Pancake
 */
@Plugin(name = "TASBattle Proxy Plugin", version = "${version}", id = "${modid}", authors = { "Pancake" }, 
		url = "https://github.com/MinecraftTAS/TAS-Battle", description = "basic proxy management plugin for the tasbattle proxy server")
public class ProxyPlugin {
	
	private final ProxyServer server;
    
    /**
     * Construct proxy plugin
     * @param server Proxy server instance
     */
	@Inject
	public ProxyPlugin(ProxyServer server) {
		this.server = server;
	}

	/**
	 * Initialize proxy plugin
	 * @param e Proxy initialization event
	 */
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent e) {

	}
	
}
