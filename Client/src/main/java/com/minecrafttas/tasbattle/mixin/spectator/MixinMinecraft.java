package com.minecrafttas.tasbattle.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Inject(method = "run", at = @At("HEAD"))
	public void inject_run(CallbackInfo ci) {
		TASBattle.spectatormanager.onKey();
	}
}
