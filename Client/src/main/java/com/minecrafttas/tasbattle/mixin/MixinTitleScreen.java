package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
	
	
	@Redirect(method = "createNormalMenuOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;addRenderableWidget(Lnet/minecraft/client/gui/components/Button;)Lnet/minecraft/client/gui/components/Button;"))
	public Button redirect_createNormalMenuOptions(Screen parentIn, Button button) {
		TASBattle.LOGGER.info("Initializing main menu");
		return button;
	}
}
