package com.minecrafttas.tasbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecrafttas.tasbattle.system.SpectatorSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;

/**
 * TAS Battle mod class
 */
public class TASBattle implements ModInitializer{
	
	public static final Logger LOGGER = LogManager.getLogger("TAS Battle");

	@Getter
	private static TASBattle instance;
	
	@Getter
	public TickrateChanger tickrateChanger;
	
	@Getter
	public SpectatorSystem spectatorManager;
	
	@Override
	public void onInitialize() {
		instance = this;

		// initialize modules
		this.tickrateChanger = new TickrateChanger();
		this.spectatorManager = new SpectatorSystem();
		
		LOGGER.info("TAS Battle has been initialized");
	}
	
}
