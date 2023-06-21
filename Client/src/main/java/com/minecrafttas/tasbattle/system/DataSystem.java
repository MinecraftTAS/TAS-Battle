package com.minecrafttas.tasbattle.system;

import com.minecrafttas.tasbattle.TASBattle;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data class for tas battle
 * @author Pancake
 */
public class DataSystem {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation("tasbattle", "data");

	@Getter
	private Map<UUID, String> tags;
	
	@Getter
	private Map<UUID, ResourceLocation> capes;
	
	/**
	 * Initialize data system
	 */
	public DataSystem() {
		this.tags = new HashMap<>();
		this.capes = new HashMap<>();
	}
	
	/**
	 * Parse data from plugin message
	 * @param data
	 * @throws Exception Malformed data
	 */
	public void parseData(String data) throws Exception {
		this.tags.clear();
		this.capes.clear();
		
		var mc = Minecraft.getInstance();
		
		var frags = data.split("\n");
		for (var tag : frags[0].split("\\,")) {
			var tagFrags = tag.split("\\:");
			this.tags.put(UUID.fromString(tagFrags[0]), tagFrags[1]);
		}
		
		for (var cape : frags[1].split("\\,")) {
			var capeFrags = cape.split("\\:");
			var loc = new ResourceLocation("tasbattle", "cape_" + capeFrags[0]);
			this.capes.put(UUID.fromString(capeFrags[0]), loc);
			mc.getTextureManager().register(loc, new DynamicTexture(NativeImage.read(URI.create("https://data.mgnet.work/" + capeFrags[1]).toURL().openStream())));
		}
		
		TASBattle.LOGGER.info("Parsed " + this.tags.size() + " tags and " + this.capes.size() + " capes.");
	}
	
}
