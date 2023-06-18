package com.minecrafttas.tasbattle.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
	
	/**
	 * Hook scroll event to spectator manager
	 * @param i Scroll amount
	 * @return Scroll amount
	 */
	@ModifyVariable(method = "onScroll", at = @At(value = "STORE"), index = 9, ordinal = 0)
	public int hook_ScrollVar(int i) {
		if (!TASBattle.getInstance().spectatingSystem.isSpectating())
			return i;
		
		TASBattle.getInstance().getSpectatingSystem().onScroll(i);
		return i;
	}
}
