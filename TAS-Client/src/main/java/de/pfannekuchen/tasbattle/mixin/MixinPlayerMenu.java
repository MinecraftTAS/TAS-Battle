package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;

@Mixin(PlayerMenuItem.class)
public class MixinPlayerMenu {

	@Shadow @Final private GameProfile profile;
	@Shadow @Final private ResourceLocation location;
	@Shadow @Final private Component name;
	
	@SuppressWarnings("resource")
	@Inject(method = "selectItem", at = @At("HEAD"), cancellable = true)
	public void inject_selectItem(SpectatorMenu spectatorMenu, CallbackInfo ci) {
		System.out.println("Starting to spectate " + name.getString());
		Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
		Minecraft.getInstance().getConnection().send(ServerboundInteractPacket.createAttackPacket(Minecraft.getInstance().level.getPlayerByUUID(this.profile.getId()), false));
		ci.cancel();
	}
	
}
