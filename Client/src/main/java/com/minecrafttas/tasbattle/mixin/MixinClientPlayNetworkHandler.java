package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.TickrateChanger;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

/**
 * This mixin is purely responsible for the hooking up networking events
 * @author Pancake
 */
@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetworkHandler {

	/**
	 * Update client tickrate when receiving custom payload
	 * @param ci Callback Info
	 */
	@Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
	public void hookCustomPayloadEvent(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (!packet.getIdentifier().equals(TickrateChanger.IDENTIFIER))
			return;
			
		TASBattle.getInstance().getTickrateChanger().changeTickrate(packet.getData().readFloat());
		ci.cancel();
	}

}