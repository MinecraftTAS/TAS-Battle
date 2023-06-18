package com.minecrafttas.tasbattle.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecrafttas.tasbattle.TASBattle;

import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

/**
 * This mixin modifies the camera for spectating purposes
 */
@Mixin(Camera.class)
public class MixinCamera {

	@Shadow private boolean initialized;
	@Shadow private BlockGetter level;
	@Shadow private Entity entity;
	@Shadow private boolean detached;


	/**
	 * Rewrite camera setup when spectating
	 */
	@Inject(method = "setup", cancellable = true, at = @At("HEAD"))
	public void setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
		if (!TASBattle.getInstance().spectatingSystem.isSpectating())
			return;

		ci.cancel();
		this.initialized = true;
		this.level = blockGetter;
		this.detached = bl;
		this.entity = entity;
		
		TASBattle.getInstance().getSpectatingSystem().onCamera((LocalPlayer) entity, (Camera) (Object) this, f);
	}

}
