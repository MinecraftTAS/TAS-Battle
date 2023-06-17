package com.minecrafttas.tasbattle.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
	
	/**
	 * Turn spectating system instead of player when spectating
	 * @param player Player
	 * @param pitchD Pitch
	 * @param yawD Yaw
	 */
	@Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
	public void redirect_turnPlayer(LocalPlayer player, double pitchD, double yawD) {
		if (player.isSpectator())
			TASBattle.getInstance().getSpectatingSystem().onMouse(player, pitchD, yawD);
		else
			player.turn(pitchD, yawD);
	}
	
	/**
	 * Hook scroll event to spectator manager
	 * @param i Scroll amount
	 * @return Scroll amount
	 */
	@ModifyVariable(method = "onScroll", at = @At(value = "STORE"), index = 9, ordinal = 0)
	public int hook_ScrollVar(int i) {
		TASBattle.getInstance().getSpectatingSystem().onScroll(i);
		return i;
	}
}
