package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.language.LanguageManager;


@Mixin(LanguageManager.class)
public class MixinLanguageManager {
	
	@Shadow
	private String currentCode;
	
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	public void inject_Init(String currentCode, CallbackInfo ci) {
		new com.minecrafttas.tasbattle.LanguageManager(currentCode, "tasbattle");
	}
	
 	@ModifyArg(method = "onResourceManagerReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/language/I18n;setLanguage(Lnet/minecraft/locale/Language;)V"), index = 0)
 	public net.minecraft.locale.Language inject_onResourceManagerReload(net.minecraft.locale.Language clientLanguage) {
  		com.minecrafttas.tasbattle.LanguageManager.getInstance().reload((net.minecraft.client.resources.language.ClientLanguage) clientLanguage);
  		return clientLanguage;
 	}
	
	
	@Inject(method = "setSelected", at = @At("RETURN"))
	public void inject_setSelected(CallbackInfo ci) {
		com.minecrafttas.tasbattle.LanguageManager.getInstance().setLanguage(currentCode);
	}
}
