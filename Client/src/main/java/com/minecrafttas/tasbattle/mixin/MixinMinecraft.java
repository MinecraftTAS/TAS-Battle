package com.minecrafttas.tasbattle.mixin;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.system.KeybindSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;

/**
 * This mixin hooks the minecraft class game loop and modifies other aspects of the minecraft class
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

	@Shadow public abstract Minecraft getInstance();

	/**
	 * Hook game loop method
	 * @param ci Callback info
	 */
	@Inject(method = "runTick", at = @At("HEAD"))
	public void inject_runTick(CallbackInfo ci) {
		KeybindSystem.onGameLoop((Minecraft) (Object) this);
	}

	/**
	 * Disable spectating system on level change
	 * @param ci Callback info
	 */
	@Inject(method = "setLevel", at = @At("HEAD"))
	public void inject_runLevel(CallbackInfo ci) {
		TASBattle.instance.getSpectatingSystem().getSelectedPlayers().clear();
		TASBattle.instance.getSpectatingSystem().spectate(null);
		TASBattle.instance.getSpectatingSystem().setShowHUD(false);
	}

	/***
	 * Disable screen opening when spectating
	 * @param mc Instance of Minecraft
	 * @param screen Screen to open
	 */
	@Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
	public void redirect_handleKeyboard(Minecraft mc, Screen screen) {
		if(TASBattle.instance.getSpectatingSystem().isSpectating() && mc.player.isSpectator())
			return;

		mc.setScreen(screen);
	}
	
	/**
	 * Modify return screen
	 * @param s Screen
	 * @param ci Callback Info
	 */
	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	public void onSetScreen(Screen s, CallbackInfo ci) {
		// redirect multiplayer screen to main menu
		if (s instanceof JoinMultiplayerScreen) {
			((Minecraft) (Object) this).setScreen(new TitleScreen());
			ci.cancel();
		}

		// reset tickrate when entering menu
		else if (s instanceof TitleScreen)
			TASBattle.instance.getTickrateChanger().changeTickrate(20.0f);
	}

	@Inject(method = "destroy", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/lang/System;exit(I)V"))
	public void onClose(CallbackInfo ci) {
		var mod = FabricLoader.getInstance().getModContainer("tasbattle").orElseGet(null).getOrigin().getPaths().get(0);
		if (Files.isDirectory(mod))
			return;

		try {
			TASBattle.LOGGER.info("Updating TAS Battle mod...");
			var data = URI.create("https://data.mgnet.work/tasbattle/client.jar").toURL().openStream().readAllBytes();

			var out = new FileOutputStream(mod.toFile());
			out.write(data);
			out.flush();
			out.close();

			TASBattle.LOGGER.info("TAS Battle mod update successful.");
		} catch (Exception e) {
			TASBattle.LOGGER.error("TAS Battle mod update unsuccessful.", e);
		}
	}

}
