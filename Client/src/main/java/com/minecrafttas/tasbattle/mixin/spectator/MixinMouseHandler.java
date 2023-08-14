package com.minecrafttas.tasbattle.mixin.spectator;

import com.minecrafttas.tasbattle.TASBattle;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
	
	/**
	 * Hook scroll event to spectator manager
	 * @param i Scroll amount
	 * @return Scroll amount
	 */
	@ModifyVariable(method = "onScroll", at = @At(value = "STORE"), index = 9, ordinal = 0)
	public int hook_ScrollVar(int i) {
		var spectatingSystem = TASBattle.instance.getSpectatingSystem();
		if (!spectatingSystem.isSpectating())
			return i;

		spectatingSystem.onScroll(i);
		return i;
	}
}
