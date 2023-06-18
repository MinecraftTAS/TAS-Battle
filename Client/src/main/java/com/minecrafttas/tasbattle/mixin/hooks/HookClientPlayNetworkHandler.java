package com.minecrafttas.tasbattle.mixin.hooks;

import java.nio.charset.StandardCharsets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.system.DataSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

/**
 * This mixin is purely responsible for the hooking up networking events
 * @author Pancake
 */
@Mixin(ClientPacketListener.class)
public class HookClientPlayNetworkHandler {

	/**
	 * Update client tickrate when receiving custom payload
	 * @param ci Callback Info
	 */
	@Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
	public void hookCustomPayloadEvent(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (packet.getIdentifier().equals(TickrateChanger.IDENTIFIER)) {
			TASBattle.getInstance().getTickrateChanger().changeTickrate(packet.getData().readFloat());
			ci.cancel();
		} else if (packet.getIdentifier().equals(DataSystem.IDENTIFIER)) {
			try {
				var data = packet.getData();
				var bytes = new byte[data.readInt()];
				data.readBytes(bytes);
				TASBattle.getInstance().getDataSystem().parseData(new String(bytes, StandardCharsets.UTF_8));
			} catch (Exception e) {
				TASBattle.LOGGER.error("Invalid data from server: {}", e);
			}
			ci.cancel();
		}
	}

}