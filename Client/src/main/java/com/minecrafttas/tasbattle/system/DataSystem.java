package com.minecrafttas.tasbattle.system;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.minecrafttas.tasbattle.TASBattle;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

/**
 * Data class for tas battle
 * @author Pancake
 */
public class DataSystem {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation("tasbattle", "data");

	@Getter
	private Map<UUID, String> tags;
	
	@Getter
	private Map<UUID, URL> capes;
	
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
		var frags = data.split("\n");
		for (var tag : frags[0].split("\\,")) {
			var tagFrags = tag.split("\\:");
			this.tags.put(UUID.fromString(tagFrags[0]), tagFrags[1]);
		}
		
		for (var cape : frags[1].split("\\,")) {
			var capeFrags = cape.split("\\:");
			this.capes.put(UUID.fromString(capeFrags[0]), new URL("https://data.mgnet.work/" + capeFrags[1]));
		}
		
		TASBattle.LOGGER.info("Parsed " + this.tags.size() + " tags and " + this.capes.size() + " capes.");
	}
	
}
