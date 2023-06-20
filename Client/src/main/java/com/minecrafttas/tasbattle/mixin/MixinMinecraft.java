package com.minecrafttas.tasbattle.mixin;

import java.nio.charset.StandardCharsets;

import com.minecrafttas.tasbattle.system.DimensionSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.system.DataSystem;
import com.minecrafttas.tasbattle.system.KeybindSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

/**
 * This mixin hooks the minecraft class game loop and modifies other aspects of the minecraft class
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Shadow
	private LocalPlayer player;
	
	/**
	 * Hook game loop method
	 * @param ci Callback info
	 */
	@Inject(method = "runTick", at = @At("HEAD"))
	public void inject_run(CallbackInfo ci) {
		KeybindSystem.onGameLoop((Minecraft)(Object)this);
	}
	
	/***
	 * Disable screen opening when spectating
	 * @param mc Instance of Minecraft
	 * @param screen Screen to open
	 */
	@Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
	public void redirect_handleKeyboard(Minecraft mc, Screen screen) {
		if(TASBattle.getInstance().getSpectatingSystem().isSpectating() && mc.player.isSpectator())
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
			TASBattle.getInstance().getTickrateChanger().changeTickrate(20.0f);
	}
}
