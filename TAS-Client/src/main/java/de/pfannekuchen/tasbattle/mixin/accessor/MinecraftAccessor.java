package de.pfannekuchen.tasbattle.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

	@Accessor
	public Timer getTimer();
	
}
