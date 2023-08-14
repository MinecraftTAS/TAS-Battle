package com.minecrafttas.tasbattle.system;

import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TAS Battle mod blacklist
 * @author Pancake
 */
public class ModBlacklist {

	private static final List<String> BLACKLIST = List.of("appleskin");

	/**
	 * Initialize mod whitelist and check installed mods
	 */
	public ModBlacklist() {
		var mods = FabricLoader.getInstance().getAllMods().stream().filter(c -> BLACKLIST.contains(c.getMetadata().getId())).toList();
		if (mods.isEmpty())
			return;

		throw new RuntimeException("Blacklisted mods installed! Mods: " + mods.stream().map(c -> c.getMetadata().getName()).collect(Collectors.joining(", ")));
	}

}
