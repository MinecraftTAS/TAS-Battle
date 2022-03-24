package de.pfannekuchen.tasbattle.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Timer;

@Mixin(Timer.class)
public interface TimerAccessor {

	@Accessor @Mutable
	public void setMsPerTick(float T);
	
}
