package com.minecrafttas.tasbattle.mixin.hooks;

import com.minecrafttas.tasbattle.TASBattle;
import com.minecrafttas.tasbattle.system.DataSystem;
import com.minecrafttas.tasbattle.system.DimensionSystem;
import com.minecrafttas.tasbattle.system.SpectatingSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

/**
 * This mixin is purely responsible for the hooking up networking events
 * @author Pancake
 */
@Mixin(ClientPacketListener.class)
public class HookClientPacketListener {

	/**
	 * Update client tickrate when receiving custom payload
	 * @param ci Callback Info
	 */
	@Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
	public void hookCustomPayloadEvent(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
		if (packet.getIdentifier().equals(TickrateChanger.IDENTIFIER)) {
			TASBattle.instance.getTickrateChanger().changeTickrate(packet.getData().readFloat());
			ci.cancel();
		} else if (packet.getIdentifier().equals(DataSystem.IDENTIFIER)) {
			try {
				var data = packet.getData();
				var bytes = new byte[data.readInt()];
				data.readBytes(bytes);
				TASBattle.instance.getDataSystem().parseData(new String(bytes, StandardCharsets.UTF_8));
			} catch (Exception e) {
				TASBattle.LOGGER.error("Invalid data from server!", e);
			}
			ci.cancel();
		} else if (packet.getIdentifier().equals(DimensionSystem.IDENTIFIER)) {
			TASBattle.instance.getDimensionSystem().changeDimension();
			ci.cancel();
		} else if (packet.getIdentifier().equals(SpectatingSystem.IDENTIFIER)) {
			TASBattle.instance.getSpectatingSystem().setShowHUD(true);
			ci.cancel();
		}
	}

	/**
	 * Disable insecure chat toast
	 */
	@Redirect(method = "handleServerData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
	public void hookHandleServerDataToast(ToastComponent toastComponent, Toast toast) {
		// don't show
	}

	/**
	 * Pass camera entity to spectating system
	 * @param mc Minecraft instance
	 * @param e Entity
	 */
	@Redirect(method = "handleSetCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setCameraEntity(Lnet/minecraft/world/entity/Entity;)V"))
	public void hookHandleSetCamera(Minecraft mc, Entity e) {
		TASBattle.instance.getSpectatingSystem().spectate(mc.player.equals(e) ? null : e);
	}

	/**
	 * Don't send player packets when spectating
	 * @param packet Packet
	 * @param ci Callback Info
	 */
	@Inject(method = "send", at = @At("HEAD"), cancellable = true)
	public void hookSendPacket(Packet<?> packet, CallbackInfo ci) {
		if (TASBattle.instance.getSpectatingSystem().isSpectating() && packet instanceof ServerboundMovePlayerPacket || packet instanceof ServerboundPlayerInputPacket || packet instanceof ServerboundPlayerCommandPacket)
			ci.cancel();
	}

}
