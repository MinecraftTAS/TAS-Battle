package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.managers.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TASBattleLobby extends JavaPlugin implements Listener {

	@Getter
	private TickrateChanger tickrateChanger;

	@Getter
	private DimensionChanger dimensionChanger;

	@Getter
	private EntityManager entityManager;

	@Getter
	private BasicRestrictions basicRestrictions;

	@Getter
	private ScoreboardManager scoreboardManager;

	@Getter
	private LobbyTelemetry lobbyTelemetry;

	/**
	 * Enable tasbattle lobby mod
	 */
	@SneakyThrows
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		this.tickrateChanger = new TickrateChanger(this);
		this.dimensionChanger = new DimensionChanger(this);
		this.entityManager = new EntityManager(this);
		this.basicRestrictions = new BasicRestrictions(this);
		this.scoreboardManager = new ScoreboardManager(this);
		this.lobbyTelemetry = new LobbyTelemetry(this);
	}

	@SneakyThrows
	@Override
	public void onDisable() {
		this.lobbyTelemetry.onShutdown();
	}

	@EventHandler
	public void cancelChat(AsyncChatEvent e) {
		e.setCancelled(true);
	}

}
