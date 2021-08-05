package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinCustomCape extends Player {

	public MixinCustomCape(Level level, BlockPos blockPos, float f, GameProfile gameProfile) { super(level, blockPos, f, gameProfile);}

	/**
	 * Replaces the Cape Texture if there is one
	 */
	@Inject(method = "getCloakTextureLocation", at = @At("HEAD"), cancellable = true)
	public void replaceCape(CallbackInfoReturnable<ResourceLocation> cir) {
		if (TASBattle.capes.containsKey(getUUID())) {
			cir.setReturnValue(TASBattle.capes.get(getUUID()));
			cir.cancel();
		}
	}
	
}
