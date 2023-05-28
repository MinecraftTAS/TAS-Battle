package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.KeybindSystem;
import com.minecrafttas.tasbattle.SpectatorManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Inject(method = "runTick", at = @At("HEAD"))
	public void inject_run(CallbackInfo ci) {
		KeybindSystem.onGameLoop((Minecraft)(Object)this);
	}
	
	
	@Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
	public void redirect_handleKeyboard(Minecraft parentIn, Screen screen) {
		if(SpectatorManager.getInstance().isSpectating() && parentIn.player.isSpectator()) {
			return;
		}
		parentIn.setScreen(screen);
	}
}
