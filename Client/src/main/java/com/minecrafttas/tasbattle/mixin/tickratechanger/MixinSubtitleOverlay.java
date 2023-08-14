package com.minecrafttas.tasbattle.mixin.tickratechanger;

import com.minecrafttas.tasbattle.TASBattle;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * This mixin slows down the subtitle overlay to the tickrate
 * @author Scribble
 */
@Mixin(SubtitleOverlay.class)
public class MixinSubtitleOverlay {
	/**
	 * Slows down the render speed by multiplying with the Gamespeed
	 * @param threethousand 3000, well
	 * @return Slowed down 3000
	 */
	@ModifyConstant(method = "render", constant = @Constant(doubleValue = 3000D))
	public double applyTickrate(double threethousand) {
		return threethousand * TASBattle.instance.getTickrateChanger().getGamespeed();
	}
}
