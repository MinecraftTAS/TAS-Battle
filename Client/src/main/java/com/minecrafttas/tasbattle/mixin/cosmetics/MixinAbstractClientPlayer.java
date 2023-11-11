package com.minecrafttas.tasbattle.mixin.cosmetics;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin replaces the player cloak with a tasbattle cloak
 * @author Pancake
 */
@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player {
	public MixinAbstractClientPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) { super(level, blockPos, f, gameProfile); }

	/**
	 * Replace player cloak with tasbattle cloak
	 * @param cir Callback info returnable
	 */
	@Inject(method = "getCloakTextureLocation", at = @At("HEAD"), cancellable = true)
	public void replaceCape(CallbackInfoReturnable<ResourceLocation> cir) {
		var capes = TASBattle.instance.getDataSystem().getCapes();
		var uuid = this.getUUID();
		if (capes.containsKey(uuid)) {
			cir.setReturnValue(capes.get(uuid));
			cir.cancel();
		}
	}
	
}
