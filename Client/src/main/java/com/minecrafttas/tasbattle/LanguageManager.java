package com.minecrafttas.tasbattle;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Appends language files either from the resources under assets/{@linkplain #assetDirName}/{@linkplain #currentCode}.json
 * or from a resourcepack to the Locale.
 * 
 * @author Scribble
 *
 */
public class LanguageManager {

	@Getter
	private static LanguageManager instance;
	
	private String currentCode;
	
	/**
	 * The name of the asset directory that should be searched for the asset files
	 */
	private String assetDirName;

	public LanguageManager(String startCode, String assetDirName) {
		this.assetDirName = assetDirName;
		currentCode = startCode;
		instance = this;
	}

	public ClientLanguage reload(ClientLanguage locale) {
		InputStream langfile = getFromResourceManager(); // First check if a resourcepack is loaded and if it has a language file

		if (langfile == null) {
			langfile = getFromResources(); // If that fails, load data from the resources
		}
		if (langfile != null) {
			Map<String, String> oldMap = locale.storage; // getStorage is immutiable so we have to make it mutable again
			Map<String, String> map = new HashMap<>(oldMap);
			Language.loadFromJson(langfile, map::put); // Append to the locale. This also uses vanilla patterns in the translation
														// files
			locale.storage = ImmutableMap.copyOf(map); // Update the storage of the current language file
		}
		return locale;
	}

	public void setLanguage(String code) {
		this.currentCode = code;
	}

	private InputStream getFromResources() {
		InputStream resource = getClass().getResourceAsStream(String.format("/assets/%s/lang/%s.json", assetDirName, currentCode));
		return resource;
	}

	private InputStream getFromResourceManager() {
		ResourceLocation location = new ResourceLocation(assetDirName, String.format("lang/%s.json", currentCode));
		ResourceManager manager = Minecraft.getInstance().getResourceManager();
		if (!manager.getResource(location).isPresent()) {
			return null;
		}
		Resource res = null;
		res = manager.getResource(location).orElse(null);

		if (res != null) {
			try {
				return res.open();
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
}
