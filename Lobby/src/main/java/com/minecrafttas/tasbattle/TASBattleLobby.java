package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TASBattleLobby extends JavaPlugin implements Listener {

	@Getter
	private TickrateChanger tickrateChanger;

	/**
	 * Enable tasbattle lobby mod
	 */
	@Override
	public void onEnable() {
		this.tickrateChanger = new TickrateChanger(this);
		Bukkit.getPluginManager().registerEvents(this, this);

		try {
			Class.forName("net.minecraft.network.protocol.game.ClientboundRespawnPacket").getField("SHOULD_OVERRIDE").setBoolean(null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Remove join message on player join
	 * @param e Player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setHealth(0.0); // immediately kill player for end override to become active
		e.joinMessage(null);
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
