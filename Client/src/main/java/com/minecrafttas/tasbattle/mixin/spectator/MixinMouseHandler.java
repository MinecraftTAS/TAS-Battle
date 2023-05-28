package com.minecrafttas.tasbattle.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
	
	
	@Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public void redirect_turnPlayer(LocalPlayer player, double pitchD, double yawD) {
		if(player.isSpectator()) {
			TASBattle.spectatormanager.onMouse(player, pitchD, yawD);
		} else {
			player.turn(pitchD, yawD);
		}
	}
}
