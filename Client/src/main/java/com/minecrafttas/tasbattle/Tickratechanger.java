package com.minecrafttas.tasbattle;

import lombok.Getter;
import net.minecraft.client.Minecraft;

public class Tickratechanger {

	@Getter
	private float tickrate;
	
	@Getter
	private float gamespeed;
	
	@Getter
	private long msPerTick;
	
	public void changeTickrate(float tickrate) {
		if(tickrate<=0 || tickrate > 1000) return;
		TASBattle.LOGGER.debug("Changing tickrate to {}", tickrate);
		
		this.tickrate=tickrate;
		this.gamespeed = tickrate/20;
		this.msPerTick = (long) (1000L/tickrate);
		
		changeClientTickrate(msPerTick);
	}
	
	private void changeClientTickrate(long msPerTick) {
		Minecraft mc = Minecraft.getInstance();
		mc.timer.msPerTick = msPerTick;
	}
}
