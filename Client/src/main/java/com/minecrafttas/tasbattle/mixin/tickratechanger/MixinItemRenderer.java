package com.minecrafttas.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.minecrafttas.tasbattle.TickrateChanger;

/**
 * Slows down the enchantment <i>glint</i>
 * 
 * @author Scribble
 */
@Mixin(net.minecraft.client.renderer.RenderStateShard.class)
public class MixinItemRenderer {
	@ModifyVariable(method = "setupGlintTexturing", at = @At(value = "STORE"), index = 1, ordinal = 0)
	private static long modifyrenderEffect(long ignored) {
		return (long) (System.currentTimeMillis() * TickrateChanger.getInstance().getGamespeed() * 8L);
	}
}