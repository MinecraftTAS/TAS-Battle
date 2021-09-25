package de.pfannekuchen.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.renderer.LevelRenderer;

@Mixin(LevelRenderer.class)
public class MixinTickrateChangerWorldborder {

	@ModifyVariable(method = "renderWorldBorder", at = @At(value = "STORE"), index = 20, ordinal = 4)
	public float injectf3(float f) {
		return ((System.currentTimeMillis() * (TASBattle.tickrate / 20F)) % 3000L) / 3000.0F;
	}

}