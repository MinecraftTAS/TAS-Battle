package de.pfannekuchen.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.pfannekuchen.tasbattle.TASBattle;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

	@Inject(method = "onScroll", at = @At("HEAD"))
	private void onScroll(long l, double d, double e, CallbackInfo ci) {
		TASBattle.scroll += ((e*1.75)/7.0);
	}
	
}
