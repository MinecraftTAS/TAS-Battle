package com.minecrafttas.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.renderer.RenderStateShard;

/**
 * Slows down the enchantment glint
 * @author Pancake
 */
@Mixin(RenderStateShard.class)
public class MixinItemRenderer {
	
	/**
	 * Redirect system millis call and replace with game time
	 * @return Game time
	 */
	@Redirect(method = "setupGlintTexturing", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J"))
	private static long modifyGlintTexturing() {
		return TASBattle.getInstance().getTickrateChanger().getMilliseconds();
	}
	
}