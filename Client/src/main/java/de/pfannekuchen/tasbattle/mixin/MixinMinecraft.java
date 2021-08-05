package de.pfannekuchen.tasbattle.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.pfannekuchen.tasbattle.TASBattle;
import de.pfannekuchen.tasbattle.gui.TASBattleScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Nullable @Shadow private ServerData currentServer;
	@Nullable @Shadow private LocalPlayer player;
	
	/**
	 * Triggers an Event in {@link TASBattle#onGameInitialize()} when the game first enters the game loop.
	 * @param ci Mixin Data
	 */
	@Inject(method = "run", at = @At("HEAD"))
	public void onGameInit(CallbackInfo ci) {
		TASBattle.onGameInitialize();
	}
	
	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	public void onSetScreen(Screen s, CallbackInfo ci) {
		if (s == null && player != null) {
			ClientPlayNetworking.send(new ResourceLocation("tickratechanger", "data"), new FriendlyByteBuf(Unpooled.buffer(1)));
		} else if (s instanceof JoinMultiplayerScreen) {
			((Minecraft) (Object) this).setScreen(new TASBattleScreen(null));
			ci.cancel();
		}
	}
	
}
