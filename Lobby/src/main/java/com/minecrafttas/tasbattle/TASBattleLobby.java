package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.managers.BasicRestrictions;
import com.minecrafttas.tasbattle.managers.DimensionChanger;
import com.minecrafttas.tasbattle.managers.EntityManager;
import com.minecrafttas.tasbattle.managers.TickrateChanger;
import lombok.Getter;
import org.bukkit.Bukkit;
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

	/**
	 * Enable tasbattle lobby mod
	 */
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		this.tickrateChanger = new TickrateChanger(this);
		this.dimensionChanger = new DimensionChanger(this);
		this.entityManager = new EntityManager(this);
		this.basicRestrictions = new BasicRestrictions(this);
	}

}
