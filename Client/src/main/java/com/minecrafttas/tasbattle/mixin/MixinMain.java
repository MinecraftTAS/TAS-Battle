package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.User;
import net.minecraft.client.main.Main;

@Mixin(Main.class)
public class MixinMain {

	@ModifyVariable(method = "main", at = @At("STORE"), index = 58, ordinal = 0)
	private static User modifyUserCreation(User user) {
		return user;
	}
}
