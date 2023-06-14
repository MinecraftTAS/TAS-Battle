package com.minecrafttas.tasbattle;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Proxy plugin class
 * @author Pancake
 */
@Plugin(name = "TASBattle Proxy Plugin", version = "${version}", id = "${modid}", authors = { "Pancake" }, 
		url = "https://github.com/MinecraftTAS/TAS-Battle", description = "basic proxy management plugin for the tasbattle proxy server")
public class ProxyPlugin {
	
	private static final List<UUID> ADMIN_UUIDS = Arrays.asList(
		UUID.fromString("f3112feb-00c1-4de8-9829-53b940342996"), // scribble
		UUID.fromString("b8abdafc-5002-40df-ab68-63206ea4c7e8"), // scribble (tasbot)
		UUID.fromString("faed0946-bb7f-4fcc-bff1-5fb9ef75a066"), // pancake
		UUID.fromString("455798c9-eed4-47f2-aeae-12d071e0ce69"), // pancake (taspvp)
		UUID.fromString("06e8982e-608e-4633-bc2b-6ea1af91b052")  // pancake (tasbattle)
	);
	
	private final ProxyServer server;
    private final PermissionProvider permissionProvider;
    
    /**
     * Construct proxy plugin
     * @param server Proxy server instance
     */
	@Inject
	public ProxyPlugin(ProxyServer server) {
		this.server = server;
		
		// create permission function
		this.permissionProvider = new PermissionProvider() {
			
			@Override
			public PermissionFunction createFunction(PermissionSubject subject) {
				// grant all permissions to admins and the console
				if ((subject instanceof Player player && ADMIN_UUIDS.contains(player.getUniqueId())) || subject instanceof ConsoleCommandSource)
					return PermissionFunction.ALWAYS_TRUE;
				
				return PermissionFunction.ALWAYS_FALSE;
			}
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
	}
	
}
