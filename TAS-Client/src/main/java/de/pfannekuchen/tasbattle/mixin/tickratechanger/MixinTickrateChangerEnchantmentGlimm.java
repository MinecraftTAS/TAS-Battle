package de.pfannekuchen.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import de.pfannekuchen.tasbattle.TASBattle;

/**
 * Slows down the Enchantment *foil*
 * @author ScribbleLP
 */
@Mixin(net.minecraft.client.renderer.RenderStateShard.class)
public abstract class MixinTickrateChangerEnchantmentGlimm {
		@ModifyVariable(method = "setupGlintTexturing", at = @At(value = "STORE"), index = 1, ordinal = 0)
		private static long modifyrenderEffect(long ignored) {
			return (long) ((System.currentTimeMillis() * (TASBattle.tickrate / 20F))*8L);
		}
}