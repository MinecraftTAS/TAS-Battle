package com.minecrafttas.tasbattle;

import com.minecrafttas.tasbattle.tickratechanger.TickrateChanger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class TASBattleLobby extends JavaPlugin {

	@Getter
	private TickrateChanger tickrateChanger;

	/**
	 * Enable tasbattle lobby mod
	 */
	@Override
	public void onEnable() {
		this.tickrateChanger = new TickrateChanger(this);
		
	}
	
}
