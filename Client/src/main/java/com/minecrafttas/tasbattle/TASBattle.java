package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.system.DataSystem;
import com.minecrafttas.tasbattle.system.DimensionSystem;
import com.minecrafttas.tasbattle.system.SpectatingSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TAS Battle mod class
 */
@Getter
public class TASBattle implements ModInitializer{
	
	public static final Logger LOGGER = LogManager.getLogger("TAS Battle");

	public static TASBattle instance;
	private TickrateChanger tickrateChanger;
	private SpectatingSystem spectatingSystem;
	private DataSystem dataSystem;
	private DimensionSystem dimensionSystem;

	@Override
	public void onInitialize() {
		instance = this;

		// initialize modules
		this.tickrateChanger = new TickrateChanger();
		this.spectatingSystem = new SpectatingSystem();
		this.dataSystem = new DataSystem();
		this.dimensionSystem = new DimensionSystem();

		LOGGER.info("TAS Battle has been initialized");
	}
	
}
