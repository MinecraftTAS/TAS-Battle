package com.minecrafttas.tasbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TASBattle implements ModInitializer{

	@Getter
	private static boolean isDevEnvironment=false;
	
	public static final Logger LOGGER = LogManager.getLogger("TAS Battle");
	
	public TickrateChanger tickrateChanger;
	
	public SpectatorManager spectatorManager;
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initialized TAS Battle");
		isDevEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();
		
		tickrateChanger = new TickrateChanger();
		
		spectatorManager = new SpectatorManager();
	}
	
}
