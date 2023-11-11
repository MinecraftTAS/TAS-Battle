package com.minecrafttas.tasbattle.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * This mixin disables realms notifications rendering
 */
@Mixin(RealmsNotificationsScreen.class)
public class MixinRealmsNotificationsScreen {

	/**
	 * Disable realms notifications rendering
	 * @author Pancake
	 * @reason Disable realms notifications rendering
	 */
	@Overwrite public void render(PoseStack poseStack, int i, int j, float f) {}
	
}
