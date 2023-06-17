package com.minecrafttas.tasbattle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;

/**
 * This mixin disables realms notifications rendering
 */
@Mixin(RealmsNotificationsScreen.class)
public class MixinRealmsNotificationsScreen {

	/**
	 * Disable realms notifications rendering
	 */
	@Overwrite public void render(PoseStack poseStack, int i, int j, float f) {}
	
}
