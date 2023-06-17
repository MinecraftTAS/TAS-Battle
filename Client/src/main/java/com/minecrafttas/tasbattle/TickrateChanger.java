package com.minecrafttas.tasbattle;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * Main tickrate changer
 * @author Pancake
 */
public class TickrateChanger {
	
	// don't touch this miran
	
	public static final ResourceLocation IDENTIFIER = new ResourceLocation("tickratechanger", "data");

	@Getter
	private float tickrate = 20;
	
	@Getter
	private float gamespeed = 1;
	
	@Getter
	private long msPerTick = 50;
	
	private long systemTimeSinceTC = System.currentTimeMillis(); // system time passed since last tickrate change
	private long gameTime = System.currentTimeMillis(); // game time passed (since last tickrate update)

	/**
	 * Change client tickrate
	 * @param tickrate Tickrate
	 */
	public void changeTickrate(float tickrate) {
		if(tickrate < 0.1f || tickrate > 100.0f)
			return;
		
		var millis = System.currentTimeMillis();

		// calculate time passed without tickrate changing
		var timePassed = millis - this.systemTimeSinceTC;
		this.gameTime += (long) (timePassed * this.gamespeed);
		this.systemTimeSinceTC = millis;
		
		this.tickrate = tickrate;
		this.gamespeed = tickrate / 20.0f;
		this.msPerTick = (long) (1000L / tickrate);
		
		var mc = Minecraft.getInstance();
		mc.timer.msPerTick = this.msPerTick;
		
		TASBattle.LOGGER.debug("Tickrate changed to {}", tickrate);
	}
	
	/**
	 * Returns the amount of milliseconds passed without including the tickrate changing
	 * @return Milliseconds
	 */
	public long getMilliseconds() {
		return (long) (this.gameTime + ((System.currentTimeMillis() - this.systemTimeSinceTC) * this.gamespeed));
	}
}
