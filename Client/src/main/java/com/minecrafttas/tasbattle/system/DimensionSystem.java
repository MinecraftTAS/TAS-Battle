package com.minecrafttas.tasbattle.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

/**
 * Dimension changer class
 * @author Pancake
 */
public class DimensionSystem {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation("dimensionchanger", "data");

	public void changeDimension() {
		var mc = Minecraft.getInstance();
		mc.level.effects = DimensionSpecialEffects.forType(mc.player.connection.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.END).value());
	}
	
}
