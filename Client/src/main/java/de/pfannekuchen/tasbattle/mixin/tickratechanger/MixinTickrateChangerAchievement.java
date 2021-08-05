package de.pfannekuchen.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import de.pfannekuchen.tasbattle.TASBattle;

@Mixin(targets = "net/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance")
public class MixinTickrateChangerAchievement {

 		@ModifyVariable(method = "Lnet/minecraft/client/gui/components/toasts/ToastComponent$ToastInstance;render(IILcom/mojang/blaze3d/vertex/PoseStack;)Z", at = @At(value = "STORE"), ordinal = 0, index = 4)
 		public long modifyAnimationTime(long animationTimer) {
 			return (long) (System.currentTimeMillis() * (TASBattle.tickrate / 20F));
 		}

}