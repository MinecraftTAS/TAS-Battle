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
	
	private long systemTimeSinceTC = System.currentTimeMillis(); // system time passed since last tickrate change
	private long gameTime = System.currentTimeMillis(); // game time passed (since last tickrate update)
	
	public void changeTickrate(float tickrate) {
		if(tickrate<=0 || tickrate > 1000) return;
		TASBattle.LOGGER.debug("Changing tickrate to {}", tickrate);
		
		long millis = System.currentTimeMillis();

		// calculate time passed without tickrate changing
		long timePassed = millis - this.systemTimeSinceTC;
		this.gameTime += (long) (timePassed * this.gamespeed);
		this.systemTimeSinceTC = millis;
		
		this.tickrate=tickrate;
		this.gamespeed = tickrate/20;
		this.msPerTick = (long) (1000L/tickrate);
		
		Minecraft mc = Minecraft.getInstance();
		mc.timer.msPerTick = msPerTick;
	}
	
	/**
	 * Returns the amount of milliseconds passed without including the tickrate changing
	 * @return Milliseconds
	 */
	public long getMilliseconds() {
		return (long) (this.gameTime + ((System.currentTimeMillis() - this.systemTimeSinceTC) * this.gamespeed));
	}
}
