package com.minecrafttas.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.util.Mth;

/**
 * Slows down Audio
 * @author Scribble
 */
@Mixin(SoundEngine.class)
public class MixinSoundEngine {

	@Inject(method = "calculatePitch", at = @At(value = "HEAD"), cancellable = true)
	public void redosetPitch(SoundInstance soundInstance, CallbackInfoReturnable<Float> ci) {
		ci.setReturnValue(Mth.clamp(soundInstance.getPitch(), 0.5F, 2.0F) * (TASBattle.tickratechanger.getTickrate() / 20F));
		ci.cancel();
	}

}
