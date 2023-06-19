package com.minecrafttas.tasbattle.system;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data class for tas battle
 * @author Pancake
 */
public class DimensionSystem {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation("dimensionchanger", "data");

	public void changeDimension() {
		var mc = Minecraft.getInstance();
		mc.level.effects = DimensionSpecialEffects.forType(mc.player.connection.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.END).value());
	}
	
}
