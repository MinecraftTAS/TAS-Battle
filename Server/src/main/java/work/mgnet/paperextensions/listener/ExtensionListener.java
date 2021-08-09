package work.mgnet.paperextensions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.Component;
import work.mgnet.paperextensions.Configuration;

public class ExtensionListener implements Listener {
	
	@EventHandler
	public void hideJoinAndQuitMessages(PlayerQuitEvent e) {
		if (Configuration.hideJoinAndQuit) e.quitMessage(null);
	}
	
	@EventHandler
	public void hideJoinAndQuitMessages(PlayerJoinEvent e) {
		if (Configuration.hideJoinAndQuit) e.joinMessage(null);
		if (Configuration.editTab) e.getPlayer().sendPlayerListHeaderAndFooter(Component.text("§eTASBattle §f- §cMinigames\n§bPlay Minecraft in Slowmotion!"), Component.text("§6Get Rickrolled: §e§uhttps://mgnet.work"));
	}
	
}
