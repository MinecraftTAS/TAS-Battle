package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	/**
	 * Triggers an Event in {@link TASBattle#onGameInitialize()} when the game first enters the game loop.
	 * @param ci Mixin Data
	 */
	@Inject(method = "run", at = @At("HEAD"))
	public void onGameInit(CallbackInfo ci) {
		TASBattle.onGameInitialize();
	}
	
}
