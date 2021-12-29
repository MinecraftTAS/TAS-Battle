package de.pfannekuchen.fallback;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class FallbackServer extends Plugin implements Listener {

	@EventHandler
	public void onServerKick(ServerKickEvent event) {
		if ("lobby".equals(event.getKickedFrom().getName())) return;
		event.setCancelServer(ProxyServer.getInstance().getServerInfo("lobby"));
		event.setCancelled(true);
		
	}
	
	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new Command("spawn", null, "hub", "l", "lobby") {
			@Override
			public void execute(CommandSender sender, String[] args) {
				((ProxiedPlayer) sender).connect(getProxy().getServerInfo("lobby"));
			}
		});
		super.onEnable();
	}
	
}
