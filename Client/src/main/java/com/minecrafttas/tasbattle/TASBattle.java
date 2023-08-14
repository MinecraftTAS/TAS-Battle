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
 * @author Pancake
 */
@Getter
public class TASBattle implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("TAS Battle");

	public static TASBattle instance;
	private final TickrateChanger tickrateChanger = new TickrateChanger();
	private final SpectatingSystem spectatingSystem = new SpectatingSystem();
	private final DataSystem dataSystem = new DataSystem();
	private final DimensionSystem dimensionSystem = new DimensionSystem();

	@Override
	public void onInitialize() {
		instance = this;
	}
	
}
