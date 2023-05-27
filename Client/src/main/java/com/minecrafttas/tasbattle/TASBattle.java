package com.minecrafttas.tasbattle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TASBattle implements ModInitializer{

	@Getter
	private static boolean isDevEnvironment=false;
	
	public static final Logger LOGGER = LogManager.getLogger("TASBattle");
	
	public static final Tickratechanger tickratechanger = new Tickratechanger();
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initialized TASBattle");
		isDevEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();
	}

}
