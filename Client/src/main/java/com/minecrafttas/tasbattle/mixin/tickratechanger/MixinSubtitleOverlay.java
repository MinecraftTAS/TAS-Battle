package com.minecrafttas.tasbattle.mixin.tickratechanger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecrafttas.tasbattle.TASBattle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.SubtitleOverlay;

/**
 * This mixin slows down the subtitle overlay to the tickrate
 * @author Scribble
 */
@Mixin(SubtitleOverlay.class)
@Environment(EnvType.CLIENT)
public class MixinSubtitleOverlay {
	/**
	 * Slows down the render speed by multiplying with the Gamespeed
	 * @param threethousand 3000, well
	 * @return Slowed down 3000
	 */
	@ModifyConstant(method = "render", constant = @Constant(doubleValue = 3000D))
	public double applyTickrate(double threethousand) {
		return threethousand * TASBattle.getInstance().getTickrateChanger().getGamespeed();
	}
}
